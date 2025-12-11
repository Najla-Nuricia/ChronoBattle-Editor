package game.contracts.interfaces;

import game.contracts.abstracts.Character;

public interface AttackStrategy {
    int computeDamage(Character self, Character target);
}
