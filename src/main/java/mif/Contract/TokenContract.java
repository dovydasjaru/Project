package mif.Contract;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class TokenContract implements Contract {
    public static String ID = "mif.Contract.TokenContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
//        if(tx.getInputStates().size() != 0)
//            throw new IllegalArgumentException("TokenContract does not have an input");
//        if(tx.getOutputStates().size() != 1)
//            throw new IllegalArgumentException("TokenContract can only have 1 output");
//        if(tx.getCommands().size() != 1)
//            throw new IllegalArgumentException("TokenContract can only have 1 command");
//        if(!(tx.getOutput(0) instanceof TokenState))
//            throw new IllegalArgumentException("TokenContract can ony have TokenState as output");
//
//        TokenState output = (TokenState) tx.getOutput(0);
//        if(output.getAmount() <= 0)
//            throw new IllegalArgumentException("TokenContract output can only have positive amount");
//        if(!(tx.getCommands().get(0).getValue() instanceof Commands.Issue))
//            throw new IllegalArgumentException("TokenContract CommandData can only be Issue type");
//
//        if(!tx.getCommands().get(0).getSigners().contains(output.getIssuer().getOwningKey()))
//            throw new IllegalArgumentException("TokenContract issuer must be command signer");
    }

    public interface Commands extends CommandData {
        class Issue implements Commands { }
        class Transfer implements Commands { }
    }
}