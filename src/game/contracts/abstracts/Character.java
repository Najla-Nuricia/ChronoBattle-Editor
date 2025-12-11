package game.contracts.abstracts;

import game.contracts.interfaces.StatusEffect;
import game.models.effects.Shield;
import java.util.*;

public abstract class Character {
    private final String name;
    protected final int maxHealth;
    private int health;
    private final int attackPower;

    private final List<StatusEffect> effects = new ArrayList<>();

    //constructor
    protected Character(String name, int health, int attackPower) {
        if(health < 0){
            throw new IllegalArgumentException("health must be > 0");
        }

        if (attackPower < 0) {
            throw new IllegalArgumentException("attackpower must be > 0");
        }

        this.maxHealth=health;
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
    }

    public final String getName() { return name; }

    public final int getAttackPower() { return attackPower; }

    public final int getHealth() { return health; }

    public final int getMaxHealth() { return maxHealth; }
    
    

    //set health agar 0
    protected final void setHealth(int health){
        if (health < 0) health = 0;

        if (health > maxHealth) health = maxHealth;

        System.out.printf("%s: %d -> %d%n", name, this.health, health);

        this.health=health;
    }

    protected int onIncomingDamage(int base){
        int reduced = base;

        //menjumlahkan flatReduction shield
        int totalShield=0;
        for (StatusEffect se : new ArrayList<>(effects)){
            if(se instanceof Shield){
                totalShield += ((Shield)se).getFlatReduce();
            }
        }
        reduced = Math.max(0,reduced - totalShield);

        if(totalShield > 0){
            System.out.printf("%s's Shield  reduce %d damage (damage %d -> %d)%n", getName(), totalShield,base,reduced );
        }

        return reduced;
    }

    public final boolean isAlive(){
        return health > 0;
    }

    public final void takeDamage(int dmg){
        int damage = Math.max(0,dmg);
        int before = getHealth();

        damage = onIncomingDamage(damage);
        setHealth(getHealth() - damage);
    }

    

    public final void heal(int amount){
        if (amount <= 0) return;

        setHealth((getHealth() + amount));
    }


    public final void addEffect(StatusEffect e ){
        if (e == null) throw new IllegalArgumentException("effect null");
        effects.add(e);
    }

    public final List<StatusEffect> getEffects(){
        return new ArrayList<>(effects);
    }

    private void runOnTurnStart(){
        for(StatusEffect e: new ArrayList<>(effects)){
            e.onTurnStart(this);
        }
    }

    private void runOnTurnEnd(){
        for(StatusEffect e: new ArrayList<>(effects)){
            e.onTurnEnd(this);
        }

        //digunakan untuk remove expired
        Iterator<StatusEffect> it = effects.iterator();

        while (it.hasNext()) {
            StatusEffect se = it.next();
            if (se.isExpired()) it.remove();
        }
    }

    public final void performTurn(Character target){
        if(!isAlive()) return;
        runOnTurnStart();
        attack(target);
        runOnTurnEnd();
    }

    public abstract void attack(Character target);

    @Override
    public String toString() {
        return String.format("%s(HP=%d/%d, AP=%d)", name, getHealth(), getMaxHealth(), getAttackPower());
    }

    public String effectsSummary() {
        List<String> names = new ArrayList<>();

        for (StatusEffect se : getEffects()) names.add(se.description());

        return names.toString();
    }
    

}
