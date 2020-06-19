package mif;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import mif.Contract.TokenContract;
import mif.Contract.TokenState;
import mif.Utility.GenericValidator;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class CrossTokenFlow extends FlowLogic<SignedTransaction> {
    private final String ownerName;
    private final int amount;

    public CrossTokenFlow(/*String owner, int amount*/) {
        this.ownerName = "PartyB";
        this.amount = 1;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        System.out.println("CrossTokenFlow");

        Party issuer = getOurIdentity();
        List<StateAndRef<TokenState>> tokenStateAndRefs = getServiceHub().getVaultService().queryBy(TokenState.class)
                .getStates();
        StateAndRef<TokenState> inputTokenStateAndRef = tokenStateAndRefs.stream().filter(tokenStateAndRef -> {
            TokenState tokenState = tokenStateAndRef.getState().getData();
            return tokenState.getOwner().equals(issuer);
        }).findAny().orElseThrow(() -> new IllegalArgumentException("Issuer does not have any money."));

        Party notary = inputTokenStateAndRef.getState().getNotary();
        Party oracle = getServiceHub().getIdentityService()
                .partiesFromName("Oracle", true).iterator().next();

        TransactionBuilder transactionBuilder = new TransactionBuilder();
        transactionBuilder.setNotary(notary);
        transactionBuilder.addInputState(inputTokenStateAndRef);

        FlowSession oracleSession = initiateFlow(oracle);
        oracleSession.send(ownerName);
        Party owner = oracleSession.receive(Party.class).unwrap(new GenericValidator<>());

        TokenState outputState = new TokenState(issuer, owner, amount);
        outputState.setOracle(oracle);
        outputState.setIsIssuerSide(true);
        transactionBuilder.addOutputState(outputState, TokenContract.ID);

        TokenContract.Commands.Transfer command = new TokenContract.Commands.Transfer();
        List<PublicKey> requiredSigners = ImmutableList.of(issuer.getOwningKey(), oracle.getOwningKey());
        transactionBuilder.addCommand(command, requiredSigners);
        transactionBuilder.verify(getServiceHub());

        SignedTransaction partlySignedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);
        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(partlySignedTransaction,
                ImmutableList.of(oracleSession)));

        Boolean isTransactionSuccessful = oracleSession.receive(Boolean.class).unwrap(new GenericValidator<>());
        if(isTransactionSuccessful)
            return subFlow(new FinalityFlow(fullySignedTransaction, oracleSession));

        return null;
    }
}
