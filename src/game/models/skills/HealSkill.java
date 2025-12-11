package game.models.skills;

import game.contracts.abstracts.Character;
import game.contracts.interfaces.Skill;

public class HealSkill implements Skill {


    private final int amount;

    public HealSkill(int amount, Character self) {
        if(amount <= 0 || amount >= self.getMaxHealth()) throw new IllegalArgumentException("0 < amount < " + self.getMaxHealth());

        this.amount = amount;
    }

    @Override
    public String name() {
       return "HealSkill(+ " + amount + ")";
    }

    @Override
    public void apply(Character self, Character target) {
       if (self == null || !self.isAlive()) return;

       int before = self.getHealth();

       self.heal(amount);

       System.out.printf("%s uses HealSkill: %d -> %d%n", self.getName(), before, self.getHealth());
    }
}
