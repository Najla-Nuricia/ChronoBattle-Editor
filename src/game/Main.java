package game;

import game.contracts.abstracts.Character;
import game.models.chars.*;
import game.models.effects.*;
import game.models.skills.*;
import game.models.strategies.*;
import game.simulation.Battle;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Player p = new Player("HeroVipkas", 120, 25, 5, new LevelScaledStrategy(2));
        Player p2 = new Player("HeroMirana", 120, 35, 3, new LevelScaledStrategy(2));

        //objek ujji validasi
        //Player uji = new Player("HeroBatrider", 120, 10, 3, new LevelScaledStrategy(2));

        //add skill
        p.addSkill(new HealSkill(15,p));
        p.addSkill(new PiercingStrike(1.2));
        p.addSkill(new FireStrike(p.getAttackPower()));

        p2.addSkill(new HealSkill(10,p2));
        p2.addSkill(new PiercingStrike(1.5));
        p2.addSkill(new FireStrike(p2.getAttackPower()));


        //uji.addSkill(new HealSkill(121,uji));

        //add effect
        p.addEffect(new Shield(10, 3));
        p.addEffect(new Regen(8, 4));

        p2.addEffect(new Shield(5, 3));
        p2.addEffect(new Regen(10, 4));

        List<Character> teamA = new ArrayList<>();
        teamA.add(p);
        teamA.add(p2);

        // teamA.add(uji);

        BossMonster boss = new BossMonster("Drake", 300, 28, 5, new CriticalHitStrategy());
        Monster goblin = new Monster("Goblin", 50, 12, 2, new FixedStrategy());
        Monster goblin2 = new Monster("Goblin 1", 80, 12, 2, new CriticalHitStrategy());

        //add effect enemy
        // boss.addEffect(new Burn(10, 3));
 

        List<Character> teamB = new ArrayList<>();
        teamB.add(boss);
        teamB.add(goblin);
        teamB.add(goblin2);

        Battle battle = new Battle(teamA, teamB);
        battle.run();

        
    }
}
