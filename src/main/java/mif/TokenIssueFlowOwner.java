package mif;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(TokenIssueFlow.class)
public class TokenIssueFlowOwner extends FlowLogic<SignedTransaction> {
    private final FlowSession counterPartySession;

    public TokenIssueFlowOwner(FlowSession counterPartySession){
        this.counterPartySession = counterPartySession;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        return subFlow(new ReceiveFinalityFlow(counterPartySession));
    }
}
