package mif.Contract;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class CrossTokenContract implements Contract {
    public static String ID = "mif.Contract.CrossTokenContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        if(tx.getOutputStates().size() != 1 && tx.outputsOfType(TokenState.class).size() != 1)
            throw new IllegalArgumentException("CrossTokenContract must have 1 output of type TokenState");
        if(tx.getInputStates().size() != 1 && tx.inputsOfType(TokenState.class).size() != 1)
            throw new IllegalArgumentException("CrossTokenContract must have 1 input of type TokenState");

        final TokenState output =  tx.outputsOfType(TokenState.class).get(0);
        final TokenState input = tx.inputsOfType(TokenState.class).get(0);

        if(output.getAmount() < 0)
            throw new IllegalArgumentException("CrossTokenContract output can not have negative amount");
        if(output.getOwner().equals(output.getIssuer()))
            throw new IllegalArgumentException("CrossTokenContract output issuer can not be owner");
        if(!input.getOwner().equals(output.getIssuer()))
            throw new IllegalArgumentException("CrossTokenContract input owner must be outputs issuer");
        if(tx.getCommands().size() != 1 && tx.commandsOfType(Commands.Transfer.class).size() != 1)
            throw new IllegalArgumentException("CrossTokenContract must have 1 command of type Transfer");

        if(!tx.getCommands().get(0).getSigners().contains(output.getIssuer().getOwningKey()))
            throw new IllegalArgumentException("CrossTokenContract issuer must be command signer");
        if(!tx.getCommands().get(0).getSigners().contains(output.getOwner().getOwningKey()))
            throw new IllegalArgumentException("CrossTokenContract owner must be command signer");
    }

    public interface Commands extends CommandData {
        class Transfer implements TokenContract.Commands {}
    }
}
