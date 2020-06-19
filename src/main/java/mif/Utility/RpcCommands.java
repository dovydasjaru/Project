package mif.Utility;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.contracts.ContractState;
import net.corda.core.flows.FlowLogic;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowHandle;
import net.corda.core.node.services.Vault;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RpcCommands {
    private static final String username = "user1";
    private static final String password = "test";

    public static <T, E extends FlowLogic<T>> T startFlowWithRPCClient(Class<E> flowClass, CordaRPCClient client, Object ...argList){
        CordaRPCConnection connection = client.start(username, password);
        CordaRPCOps proxy = connection.getProxy();
        FlowHandle<T> flowHandle = proxy.startFlowDynamic(flowClass, argList);

        CompletableFuture<T> returnValueFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return flowHandle.getReturnValue().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        });

        T returnValue = null;
        try {
            returnValue = returnValueFuture.get(80, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        flowHandle.close();
        connection.notifyServerAndClose();
        return returnValue;
    }

    public static Party getPartyFromClient(String partyName, CordaRPCClient client) {
        CordaRPCConnection connection = client.start(username, password);
        Party foundParty = connection.getProxy().partiesFromName(partyName, true).iterator().next();
        connection.close();
        return foundParty;
    }

    public static <T extends ContractState> Vault.Page<T> vaultQueryFromClient(Class<T> contractStateType, CordaRPCClient client) {
        CordaRPCConnection connection = client.start(username, password);
        Vault.Page<T> queryResult = connection.getProxy().vaultQuery(contractStateType);
        connection.close();
        return queryResult;
    }
}
