package game.models.chars;

import java.util.Random;

import game.contracts.abstracts.Character;
import game.contracts.interfaces.AttackStrategy;
import game.contracts.abstracts.Enemy;

public class Monster extends Enemy {
    private final Random rnd = new Random();

    public Monster(String name , int health , int ap, int threatLevel , AttackStrategy strategy ) {
        super(name, health , ap, threatLevel, strategy);
    }
    
    public AttackStrategy getStrategy() {
    return this.strategy;
    }

    @Override
    public void attack(Character target) {
       //menambah 40% hingga 120% dari attackPower
       double multiply = 0.8 + rnd.nextDouble() * 0.4;
       int dmg = (int) Math.round(getAttackPower()* multiply);

       System.out.printf("%s -> %s : %d dmg%n", this.getName(), target.getName(), dmg);
       target.takeDamage(dmg);
    }


}
