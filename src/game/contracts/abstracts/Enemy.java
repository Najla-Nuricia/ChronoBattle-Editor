package game.contracts.abstracts;

import game.contracts.interfaces.AttackStrategy;

public abstract class Enemy extends Character {
    private int threatLevel;
    protected AttackStrategy strategy;

    protected Enemy(String name, int health, int ap, int threatLevel, AttackStrategy strategy) {
        super(name, health, ap);

        if (threatLevel < 1 || threatLevel > 5) throw new IllegalArgumentException("must be 1..5");

        this.threatLevel=threatLevel;
        this.strategy=strategy;
    }

    public final int getThreatLevel() {
        return threatLevel;
    }

    public final void setStrategy(AttackStrategy s){
       if(s== null){
        throw new IllegalArgumentException("strategy null");

    }
       this.strategy=s;
    }
    
    protected String name;

    public void setName(String name) {
    this.name = name;
    }
    
    public AttackStrategy getStrategy() {
    return this.strategy;
    }
            

}
