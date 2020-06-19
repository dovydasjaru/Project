package mif;

import co.paralleluniverse.fibers.Suspendable;
import mif.Utility.GenericValidator;
import mif.Utility.RpcCommands;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.StatesToRecord;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.NetworkHostAndPort;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.NotNull;

@InitiatedBy(CrossTokenFlow.class)
public class CrossTokenFlowOracle extends FlowLogic<Void> {
    private final FlowSession counterPartySession;

    public CrossTokenFlowOracle(FlowSession counterPartySession) {
        this.counterPartySession = counterPartySession;
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {
        System.out.println("CrossTokenFlowOracle");
        CordaRPCClient client = new CordaRPCClient(new NetworkHostAndPort("localhost", 10041));

        String ownerName = counterPartySession.receive(String.class).unwrap(new GenericValidator<>());
        Party owner = RpcCommands.getPartyFromClient(ownerName, client);

        if(owner == null)
            throw new FlowException("Could not receive owner party");
        counterPartySession.send(owner);

        SignedTransaction fullySignedTransaction = subFlow(new SignTransactionFlow(counterPartySession) {
            @Override
            protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException { }
        });

        SecureHash transactionId = RpcCommands.startFlowWithRPCClient(CrossTokenFlowCounterOracle.class, client,
                fullySignedTransaction, owner, System.currentTimeMillis());
        if(transactionId == null){
            System.out.println("Could not receive confirmation from other side");
            counterPartySession.send(false);
            return null;
        }

        counterPartySession.send(true);
        subFlow(new ReceiveFinalityFlow(counterPartySession, fullySignedTransaction.getId(),
                StatesToRecord.ALL_VISIBLE));

        return null;
    }
}
