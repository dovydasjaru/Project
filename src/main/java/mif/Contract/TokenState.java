package mif.Contract;

import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@BelongsToContract(TokenContract.class)
public class TokenState implements ContractState {
    private Party owner;
    private Party issuer;
    private Party oracle;
    private int amount;
    private boolean isIssuerSide;

    public Party getOwner(){
        return owner;
    }

    public Party getIssuer(){
        return issuer;
    }

    public int getAmount(){
        return amount;
    }

    public void setOracle(Party oracle) { this.oracle = oracle; }
    public Party getOracle() { return oracle; }

    public void setIsIssuerSide(boolean isIssuerSide) { this.isIssuerSide = isIssuerSide; }
    public boolean getIsIssuerSide() { return isIssuerSide; }

    public TokenState(Party issuer, Party owner, int amount){
        this.owner = owner;
        this.issuer = issuer;
        this.amount = amount;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        ArrayList<AbstractParty> participants = new ArrayList<>();
        if(isIssuerSide)
            participants.add(issuer);
        else
            participants.add(owner);

        if(oracle != null)
            participants.add(oracle);

        return participants;
    }

    @Override
    public String toString(){
        return "amount=" + amount + ", issuer=" + issuer.getName().toString() + ", owner=" + owner.getName().toString();
    }
}