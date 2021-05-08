package rusting.content;

import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.ctype.ContentList;
import mindustry.type.StatusEffect;
import rusting.type.*;

public class RustingStatusEffects implements ContentList {
    public static StatusEffect
            amberstriken, umbrafliction;

    @Override
    public void load() {
        amberstriken = new CrystalStatusEffect("amberstriken"){{
            speedMultiplier = 0.35F;
            transitionDamage = 10f;
            effect = Fx.plasticburn;
            init(() -> {
                affinity(StatusEffects.burning, ((unit, time, newTime, result) -> {
                    unit.damagePierce(transitionDamage);
                    unit.apply(umbrafliction, time * 3);
                    Fx.placeBlock.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f));
                    result.set(amberstriken, 0);
                }));
                affinity(umbrafliction, ((unit, time, newTime, result) -> {
                    unit.damagePierce(transitionDamage/3);
                    result.set(amberstriken, 0);
                }));
            });
            disarm = false;
        }};

        umbrafliction = new CrystalStatusEffect("umbrafliction"){{
            speedMultiplier = 0.15F;
            effect = Fxr.blackened;
            disarm = false;
        }};
    }
}
