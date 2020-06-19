package mif;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import mif.Contract.TokenState;
import mif.Utility.GenericValidator;
import net.corda.core.crypto.SecureHash;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

@InitiatingFlow
@StartableByRPC
public class CrossTokenFlowCounterOracle extends FlowLogic<SecureHash> {
    private final SignedTransaction offerTransaction;
    private final Party owner;
    private final long startTime;

    public CrossTokenFlowCounterOracle(SignedTransaction offer, Party owner, long startTime){
        this.offerTransaction = offer;
        this.owner = owner;
        this.startTime = startTime;
    }

    @Override
    @Suspendable
    public SecureHash call() throws FlowException {
        System.out.println("CrossTokenFlowCounterOracle");

        TransactionBuilder builder = new TransactionBuilder();
        builder.setNotary(getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0));

        TokenState outputState = (TokenState) offerTransaction.getTx().getOutputStates().get(0);
        outputState.setOracle(getOurIdentity());
        outputState.setIsIssuerSide(false);

        builder.addOutputState(outputState);
        builder.addCommand(offerTransaction.getTx().getCommands().get(0).getValue(),
                ImmutableList.of(owner.getOwningKey(), getOurIdentity().getOwningKey()));

        SignedTransaction partialTransaction = getServiceHub().signInitialTransaction(builder);
        FlowSession ownerSession = initiateFlow(owner);
        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(partialTransaction,
                ImmutableList.of(ownerSession)));

        long timeDifference = System.currentTimeMillis() - startTime;
        if(timeDifference > 40000) {
            ownerSession.send(false);
            System.out.println("Took too long");
            return null;
        }

        ownerSession.send(true);
        subFlow(new FinalityFlow(fullySignedTransaction, ownerSession));

        return fullySignedTransaction.getId();
    }
}
