package rusting.entities.abilities;

import arc.Core;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class EffectsAbility extends Ability {

    Effect trailEffect;
    float chance;
    boolean dynamicRotation, drawTrail;

    public EffectsAbility(Effect trailfx, float Chance, boolean dynamicRotation, boolean drawTrail){
        this.trailEffect = trailfx;
        this.chance = Chance;
        this.dynamicRotation = dynamicRotation;
        this.drawTrail = drawTrail;
    }

    @Override
    public void update(Unit unit) {
        if (drawTrail && Mathf.chance(chance)) trailEffect.at(unit.x, unit.y, dynamicRotation ? unit.rotation : 0, unit);
    }

    @Override
    public String localized(){
        return Core.bundle.get("ERability.effects");
    }

}
