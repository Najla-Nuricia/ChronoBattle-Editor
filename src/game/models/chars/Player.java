package game.models.chars;

import game.contracts.abstracts.Character;
import game.contracts.interfaces.*;
import game.models.skills.*;
import java.util.*;

public class Player extends Character {
    private int level;
    private AttackStrategy strategy;
    private final List<Skill> skills = new ArrayList<>();
    private final Random rnd = new Random();

    public Player(String name,int health, int ap, int level , AttackStrategy strategy) {
        super(name, health, ap);

        if (level < 0) throw new IllegalArgumentException("level must be > 0");

        if(strategy == null) throw new IllegalArgumentException("strategy null");

        this.level=level;
        this.strategy=strategy;
    }

    public int getLevel() {
        return level;
    }

    public void addSkill(Skill s){
        if (s == null) throw new IllegalArgumentException("skill is null");

        skills.add(s);
    }
    
    protected String name;

    public void setName(String name) {
    this.name = name;
    }
    
    public AttackStrategy getStrategy() {
    return this.strategy;
    }

    @Override
    public void attack(Character target){
        if(!isAlive() || target==null || !target.isAlive()) return;

        //heal apabila hp kurang dari 50%
        if (this.getHealth() * 2 < this.getMaxHealth() ) {
            for (Skill s : skills) {
                if (s instanceof HealSkill) {
                    s.apply(this, target);

                    return;
                }
            }
        }

        //fire strike 30% kemungkinan 
        for(Skill s : skills){
            if (s instanceof FireStrike){
                if(rnd.nextDouble()< 0.3){
                    s.apply(this, target);
                    return;
                }
            }
        }

        //piercing strike : 50% kemungkinan
        PiercingStrike chosenPierce = null;
        for (Skill s : skills) {
            if (s instanceof PiercingStrike) {
                if (rnd.nextBoolean()) {
                    chosenPierce = (PiercingStrike) s;

                    break;
                }
            }
        }

        if (chosenPierce != null) {
            chosenPierce.apply(this, target);

            return;
        }

        //serangan normal
        int dmg = strategy != null ? strategy.computeDamage(this, target) : getAttackPower() + level * 2;

        System.out.printf("%s -> %s : %d dmg%n", this.getName(), target.getName(), dmg);

        target.takeDamage(dmg);
    }

    public String skillsSummary() {
        List<String> names = new ArrayList<>();

        for (Skill s : skills) names.add(s.name());

        return names.toString();
    }

     public List<Skill> getSkills() {
        return skills;
    }
}
