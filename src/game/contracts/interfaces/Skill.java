package game.contracts.interfaces;

import game.contracts.abstracts.Character;

public interface Skill {
    String name();
    void apply(Character self, Character target);
}
