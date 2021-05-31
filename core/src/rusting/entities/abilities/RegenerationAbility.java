package rusting.entities.abilities;

import arc.Core;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class RegenerationAbility extends Ability {

    public float health;

    public RegenerationAbility(float health){
        this.health = health;
    }

    @Override
    public void update(Unit unit) {
        unit.heal(health);
    }

    @Override
    public String localized() {
        return Core.bundle.get("ERability.regeneration");
    }
}
