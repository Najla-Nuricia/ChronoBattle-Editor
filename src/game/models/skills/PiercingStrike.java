package game.models.skills;

import game.contracts.abstracts.Character;
import game.contracts.interfaces.*;
import game.models.effects.Shield;

public class PiercingStrike implements Skill {
    private final double multiplier;

    public PiercingStrike(double multiplier) {
        if (multiplier < 0 ) throw new IllegalArgumentException("multiplier > 0");
        this.multiplier = multiplier;
    }

    @Override
    public String name() {
       return "Piercing Strike ( x " + multiplier + ")";
    }

    @Override
    public void apply(Character self, Character target) {
        if (self == null || target == null || !self.isAlive() || !target.isAlive()) return;

        int piercingDmg = (int)Math.round(self.getAttackPower() * multiplier);

        int totalShieldReduction = 0;
        for (StatusEffect effect : target.getEffects()) {
            if (effect instanceof Shield && !effect.isExpired()) {
                totalShieldReduction += ((Shield) effect).getFlatReduce();
            }
        }

        int finalDmgToSend = piercingDmg + totalShieldReduction;

        System.out.printf("%s uses PiercingStrike on %s: %d dmg (ignores %d from shield)%n",
            self.getName(), target.getName(), piercingDmg, totalShieldReduction);

        target.takeDamage(finalDmgToSend);
    }
}
