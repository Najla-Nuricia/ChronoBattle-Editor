package game.models.skills;

import game.contracts.abstracts.Character;
import game.contracts.interfaces.Skill;

/**
 * Simple factory that creates skill instances with sane default parameters.
 * - FireStrike(int base)
 * - PiercingStrike(double multiplier)
 * - HealSkill(int amount, Character self)
 *
 */
public class SkillFactory {

    public static Skill create(String skillName, Character owner) {
        if (skillName == null) return null;
        switch (skillName) {
            case "FireStrike":
            case "Fire Strike":
            case "Fire Strike Skill":
                return new FireStrike(20); // default base
            case "PiercingStrike":
            case "Piercing Strike":
                return new PiercingStrike(1.2); // default multiplier
            case "Heal":
            case "HealSkill":
            case "Heal Skill":
            case "HealSkill(+ 40)":
                // owner required for HealSkill constructor: if owner null, skip/return null
                if (owner == null) return null;
                return new HealSkill(Math.max(10, Math.min( owner.getMaxHealth()/4, 40 )), owner);
            default:
                return null;
        }
    }
}

