package game.simulation;

import game.contracts.abstracts.Character;
import game.contracts.abstracts.Enemy;
import game.models.chars.Player;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Battle {
    private final List<Character> teamA; // players
    private final List<Character> teamB; // enemies
    private int turn = 0;

    public Battle(List<Character> teamA, List<Character> teamB) {
        this.teamA = new ArrayList<>(teamA);
        this.teamB = new ArrayList<>(teamB);
    }

    public void run() {
        System.out.println("=== SETUP ===");
        printTeams();

        while (teamAlive(teamA) && teamAlive(teamB)) {
            turn++;
            System.out.println("\n=== TURN " + turn + " ===");

            // Team A menyerang
            for (Character c : new ArrayList<>(teamA)) {
                if (!c.isAlive()) continue;
                Character target = autoTargetForPlayer(teamB);
                System.out.print("[Team A]");
                if (target == null) break;
                c.performTurn(target);
                System.out.println();
            }


            // me remove character mati
            removeDead(teamA); removeDead(teamB);
            if (!teamAlive(teamA) || !teamAlive(teamB)) break;

            // Team B menyerang
            for (Character c : new ArrayList<>(teamB)) {
                if (!c.isAlive()) continue;
                Character target = autoTargetForEnemy(teamA);
                System.out.print("[Team B]");
                if (target == null) break;
                c.performTurn(target);
            }

            removeDead(teamA); removeDead(teamB);
        }

        //hasil
        System.out.println("\n=== RESULT ===");
        if (teamAlive(teamA)) {
            System.out.println("Team A menang!");

        } else {
            System.out.println("Team B menang!");
        }

        printSummary();
    }

    private boolean teamAlive(List<Character> team) {
        return team.stream().anyMatch(Character::isAlive);
    }

    private void removeDead(List<Character> team) {
        team.removeIf(c -> !c.isAlive());
    }

    private Character autoTargetForEnemy(List<Character> players) {
        // enemy menargetkan player dengan hp tertinggi
        Optional<Character> opt = players.stream().filter(Character::isAlive)
                .max(Comparator.comparingInt(Character::getHealth));

        return opt.orElse(null);
    }

    private Character autoTargetForPlayer(List<Character> enemies) {
        // Player menargetkan enemy dengan threat tertinggi lalu hp terendah
        return enemies.stream().filter(Character::isAlive)
                .sorted(Comparator.comparingInt((Character e) -> {
                    if (e instanceof Enemy) return -((Enemy) e).getThreatLevel();
                    return 0;
                }).thenComparingInt(Character::getHealth))
                .findFirst().orElse(null);
    }

    private void printTeams() {
        System.out.println("Team A:");
        for (Character c : teamA) {
            System.out.println("  - " + c);

            if (c instanceof Player) {
                Player p = (Player)c;
                System.out.print("    Skills: ");
                System.out.println(p.skillsSummary());

                System.out.print("    Effects: ");
                System.out.println(p.effectsSummary());
            }
        }

        System.out.println("Team B:");
        for (Character c : teamB) {
            System.out.println("  - " + c);
            // System.out.println("  - " + c + (c instanceof Enemy ? (" Threat=" + ((Enemy)c).getThreatLevel()) : ""));

            if (c instanceof Enemy){
                Enemy e = (Enemy)c;
                System.out.print("Threat Level : " + e.getThreatLevel());
                System.out.print("    Effects: ");
                System.out.println(e.effectsSummary());
            }
        }
    }

    private void printSummary() {
        System.out.println("Remaining:");

        for (Character c : teamA) System.out.println(" - " + c);
        for (Character c : teamB) System.out.println(" - " + c);
    }
}

