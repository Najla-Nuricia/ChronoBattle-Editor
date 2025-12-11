package game.models.effects;

import game.contracts.abstracts.Character;
import game.contracts.interfaces.StatusEffect;

public class Regen implements StatusEffect {
    private final int perTurn;
    private int turnRemain;


    public Regen(int perTurn, int turnRemain) {
        if (perTurn <=0 || turnRemain <= 0) throw new IllegalArgumentException("invalid regen");

        this.perTurn=perTurn;
        this.turnRemain=turnRemain;
    }

    @Override
    public void onTurnStart(Character self) {

    }

    @Override
    public void onTurnEnd(Character self) {
        if (turnRemain <= 0) return;

        self.heal(perTurn);

        turnRemain--;
        
        System.out.printf("%s Regen effect heals %d (remaining %d)%n", self.getName(), perTurn, turnRemain);
    }

    @Override
    public boolean isExpired() {
        return turnRemain <=0;
    }

    @Override
    public String description() {
         return "Regen(+" + perTurn + ", remains =" + turnRemain + ")";
    }
    
    @Override
    public int getValue1() {
        return perTurn;
    }

    @Override
    public int getValue2() {
        return turnRemain;
    }



}
