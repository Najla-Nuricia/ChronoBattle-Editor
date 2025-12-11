package game.models.skills;

import game.contracts.abstracts.Character;
import game.contracts.interfaces.Skill;
import game.models.effects.Burn;

public class FireStrike implements Skill {
    private final int base;

    public FireStrike(int base) {
        this.base = base;
    }

    @Override
    public String name() {
        return "Fire Strike Skill ("+ base +")";
    }

    @Override
    public void apply(Character self, Character target) {
        if (self == null || !self.isAlive()) return;
    
        //efek fire strike 10% dari damage yang diperoleh dari burn
        int dmg = base + self.getAttackPower();
        int burned = dmg * 10/100;

        System.out.printf("%s Uses Fire Strike burning %s! damage: %d %n ", self.getName(),target.getName(),dmg);
        target.takeDamage(dmg);
        
        target.addEffect(new Burn(burned, 1));
    }

    
    
}
