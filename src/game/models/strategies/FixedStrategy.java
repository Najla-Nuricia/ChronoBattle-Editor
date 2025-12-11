package game.models.strategies;

import game.contracts.abstracts.Character;
import game.contracts.interfaces.AttackStrategy;

public class FixedStrategy implements AttackStrategy {
    @Override
    public int computeDamage(Character self, Character target) {
        return self.getAttackPower();
    }
}



