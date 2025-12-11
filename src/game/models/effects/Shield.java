package game.models.effects;

import game.contracts.abstracts.Character;
import game.contracts.interfaces.StatusEffect;

public class Shield implements StatusEffect {
    private final int flatReduce;
    private int turnRemain;



    public Shield(int flatReduce, int duration) {
        if (flatReduce <= 0 || duration <= 0) throw new IllegalArgumentException("invalid shield");

        this.flatReduce = flatReduce;
        this.turnRemain = duration;
    }

    @Override
    public void onTurnStart(Character self) {
    }

    @Override
    public void onTurnEnd(Character self) {
       turnRemain--;
       System.out.printf("%s Shield remaining %d turns%n", self.getName(), Math.max(0, turnRemain));
    }

    @Override
    public boolean isExpired() {
        return turnRemain <= 0;
    }

    @Override
    public String description() {
        return "Shield(-" + flatReduce + " dmg, rem=" + turnRemain + ")";
    }

    public int getFlatReduce(){
        return flatReduce;
    }

    @Override
    public int getValue1() {
        return flatReduce;
    }

    @Override
    public int getValue2() {
        return turnRemain;
    }

}
