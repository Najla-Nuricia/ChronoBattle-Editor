package game.models.chars;

import game.contracts.abstracts.Character;
import game.contracts.abstracts.Enemy;
import game.contracts.interfaces.AttackStrategy;

public class BossMonster extends Enemy {
    private int turnCounter = 0;

    public BossMonster(String name, int hp, int ap, int threatLevel, AttackStrategy strategy) {
        super(name, hp, ap, threatLevel, strategy);
    }
    
    public AttackStrategy getStrategy() {
    return this.strategy;
    }

    @Override
    public void attack(Character target){
        if (!isAlive() || target == null || !target.isAlive()) return;
        turnCounter++;

        //menghitung base damage berdasarkan strategi , jika strategi kosong maka base menggunakan attackPower
        int base = strategy != null ? strategy.computeDamage(this, target) : getAttackPower();

        //variabel digunakan untuk kondisi
        boolean rageHp = getHealth() * 2 < getMaxHealth();
        boolean rageTurn = ((turnCounter %3) ==0);
        int dmg;

        // Rage Strike: 2x damage jika hp < 50% atau setiap 3 giliran
        if(rageHp || rageTurn){
            dmg = (int)Math.round(base * 2.0);
            System.out.printf("%s -> %s : RAGE STRIKE %d dmg%n", this.getName(), target.getName(), dmg);

        } else {
            dmg = base;
            System.out.printf("%s -> %s : %d dmg%n", this.getName(), target.getName(), dmg);
        }

        target.takeDamage(dmg);
    }

}
