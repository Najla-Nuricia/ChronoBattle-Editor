package game.models.strategies;

import game.contracts.interfaces.AttackStrategy;


public class StrategyFactory {

    public static AttackStrategy create(String name) {
        if (name == null) return null;
        return switch (name) {
            case "CriticalHitStrategy", "Critical" -> new CriticalHitStrategy();
            case "FixedStrategy", "Fixed" -> new FixedStrategy();
            case "LevelScaledStrategy", "LevelScaled" -> new LevelScaledStrategy(1);
            default -> null;
        };
    }
}
