package mif;

import mif.Contract.TokenState;
import mif.Utility.RpcCommands;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.NetworkHostAndPort;

import java.util.List;

@InitiatingFlow
@StartableByRPC
public class CheckOracleFlow extends FlowLogic<String> {
    public CheckOracleFlow() {

    }

    @Override
    public String call() throws FlowException {
        System.out.println("CheckOracleFlow");
        CordaRPCClient clientCounter = new CordaRPCClient(new NetworkHostAndPort("localhost", 10041));
        CordaRPCClient clientOur = new CordaRPCClient(new NetworkHostAndPort("localhost", 10051));

        List<StateAndRef<TokenState>> counterContractStates =
                RpcCommands.vaultQueryFromClient(TokenState.class, clientCounter).getStates();
        List<StateAndRef<TokenState>> ourContractStates =
                RpcCommands.vaultQueryFromClient(TokenState.class, clientOur).getStates();
        int matchCounter = 0;

        for(StateAndRef<TokenState> ourState : ourContractStates){
            String ourStateData = ourState.getState().getData().toString();
            for(StateAndRef<TokenState> counterState : counterContractStates){
                String counterStateData = counterState.getState().toString().split("[{}]")[1];
                if(ourStateData.equals(counterStateData)) {
                    matchCounter++;
                    break;
                }
            }
        }

        if (counterContractStates.size() == matchCounter)
            return "All good";

        return "Something bad";
    }
}
