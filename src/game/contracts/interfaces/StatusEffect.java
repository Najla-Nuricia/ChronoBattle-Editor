package game.contracts.interfaces;

import game.contracts.abstracts.Character;

public interface StatusEffect {
    void onTurnStart(Character self);
    
    void onTurnEnd(Character self);

    boolean isExpired();

    String description(); //menampilkan deskripsi log
    
    int getValue1();  
    int getValue2();
}
