package mif;

import co.paralleluniverse.fibers.Suspendable;
import mif.Utility.GenericValidator;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

@InitiatedBy(CrossTokenFlowCounterOracle.class)
public class CrossTokenFlowCounterParty extends FlowLogic<Void> {
    private final FlowSession counterPartySession;

    public CrossTokenFlowCounterParty(FlowSession counterPartySession){
        this.counterPartySession = counterPartySession;
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {
        System.out.println("CrossTokenFlowCounterParty");

        subFlow(new SignTransactionFlow(counterPartySession) {
            @Override
            protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException { }
        });

        Boolean isTransactionSuccessful = counterPartySession.receive(Boolean.class).unwrap(new GenericValidator<>());
        if(isTransactionSuccessful)
            subFlow(new ReceiveFinalityFlow(counterPartySession));

        return null;
    }
}
