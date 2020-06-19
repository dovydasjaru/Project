package mif;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import mif.Contract.*;

/* Our flow, automating the process of updating the ledger.
 * See src/main/java/examples/ArtTransferFlowInitiator.java for an example. */
@InitiatingFlow
@StartableByRPC
public class TokenIssueFlow extends FlowLogic<SignedTransaction> {
    private Party owner;
    private final int amount;

    public TokenIssueFlow(/*Party owner, int amount*/) {
        //this.owner =
        this.amount = 555;
    }

    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        owner = getServiceHub().getIdentityService()
                .partiesFromName("PartyA", true).iterator().next();
        // We choose our transaction's notary (the notary prevents double-spends).
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        // We get a reference to our own identity.
        Party issuer = getOurIdentity();

        // We create our new TokenState.
        TokenState tokenState = new TokenState(issuer, owner, amount);

        // We build our transaction.
        TransactionBuilder transactionBuilder = new TransactionBuilder();
        transactionBuilder.setNotary(notary);
        transactionBuilder.addOutputState(tokenState, TokenContract.ID);
        TokenContract.Commands.Issue issue = new TokenContract.Commands.Issue();
        transactionBuilder.addCommand(issue, issuer.getOwningKey());

        // We check our transaction is valid based on its contracts.
        transactionBuilder.verify(getServiceHub());

        // We sign the transaction with our private key, making it immutable.
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);
        FlowSession ownerSession = initiateFlow(owner);

        // We get the transaction notarised and recorded automatically by the platform.
        return subFlow(new FinalityFlow(signedTransaction, ownerSession));
    }
}