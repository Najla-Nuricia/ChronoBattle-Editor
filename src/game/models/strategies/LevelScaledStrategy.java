package game.models.strategies;

import game.contracts.abstracts.Character;
import game.contracts.interfaces.AttackStrategy;
import game.models.chars.Player;

public class LevelScaledStrategy implements AttackStrategy {
    private final int bonusPerLevel;

    public LevelScaledStrategy(int bonusPerLevel) {
        this.bonusPerLevel = bonusPerLevel;
    }

    @Override
    public int computeDamage(Character self, Character target) {
        int level = 0;

        if (self instanceof Player) {
            level = ((Player) self).getLevel();
        }

        return self.getAttackPower() + (level * bonusPerLevel);
    }
}
