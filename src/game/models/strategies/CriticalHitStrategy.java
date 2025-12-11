package game.models.strategies;

import game.contracts.abstracts.Character;
import game.contracts.interfaces.AttackStrategy;
import java.util.Random;

public class CriticalHitStrategy implements AttackStrategy {
    private final Random rand = new Random();

    @Override
    public int computeDamage(Character self, Character target) {
        int base = self.getAttackPower();

        // Critical hit muncul saat hp kurang dari 20%
        boolean critical = self.getHealth() * 2 < self.getMaxHealth() * 20/100;

        // Critical hit menambah 50% damage
        if (critical) {
            int dmg = (base * 50 / 100) + base;
            System.out.printf("Critical Hit by %s! Damage: %d%n",
                    self.getName(), dmg);
            return dmg;
        } else {
            return base;
        }
    }
}
