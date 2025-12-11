package game.models.effects;

import game.contracts.abstracts.Character;
import game.contracts.interfaces.StatusEffect;

public class Burn implements StatusEffect {
    private final int dmgPerTurn;
    private int remainPerTurn;

    public Burn(int dmgPerTurn, int remainPerTurn) {
        this.dmgPerTurn = dmgPerTurn;
        this.remainPerTurn = remainPerTurn;
    }

    @Override
    public void onTurnStart(Character self) {
    }

    @Override
    public void onTurnEnd(Character target) {
       if (target.isAlive()) {
            System.out.printf("%s burning and loss %d HP!%n", target.getName(), dmgPerTurn);
            target.takeDamage(dmgPerTurn);
        }
        remainPerTurn--;
    }

    @Override
    public boolean isExpired() {
        return remainPerTurn <= 0;
    }

    @Override
    public String description() {
       return "Burn(-" + dmgPerTurn + "/turn, rem=" + remainPerTurn + ")";
    }
    
    @Override
    public int getValue1() {
        return dmgPerTurn;
    }

    @Override
    public int getValue2() {
        return remainPerTurn;
    }

}
