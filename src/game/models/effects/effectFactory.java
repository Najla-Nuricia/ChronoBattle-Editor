package game.models.effects;

import game.contracts.interfaces.StatusEffect;

public class effectFactory {

    public static StatusEffect createEffect(String name, int v1, int v2) {
        switch (name) {
            case "Burn":
                return new Burn(v1, v2);
            case "Regen":
                return new Regen(v1, v2);
            case "Shield":
                return new Shield(v1, v2);
            default:
                return null;
        }
    }
}
