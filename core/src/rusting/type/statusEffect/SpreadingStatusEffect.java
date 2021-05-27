package rusting.type.statusEffect;

import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Unit;

public class SpreadingStatusEffect extends ConsStatusEffect {
    public float spreadRadius = 5;
    public float spreadInterval = 60;
    public boolean spreadSingle = true;
    public Effect spreadEffect = Fx.plasticburn;
    public SpreadingStatusEffect(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit, float time){
        super.update(unit, time);
        if(time % spreadInterval < 1){
            if(spreadSingle){
                Unit u = Units.closest(unit.team, unit.x, unit.y, unit.hitSize + spreadRadius, un -> !un.isImmune(this) && !un.hasEffect(this));
                if(u != null){
                    u.apply(this, time * 0.9f);
                    spreadEffect.at(u.x, u.y, unit.angleTo(u.x, u.y));
                }
            }
            else {
                Units.nearby(unit.team, unit.x, unit.y, unit.hitSize + spreadRadius, u -> {
                    if (!u.isImmune(this) && !u.hasEffect(this)) {
                        u.apply(this, time * 0.9f);
                        spreadEffect.at(u.x, u.y, unit.angleTo(u.x, u.y));
                    } else u.damagePierce(damage * spreadInterval / 2.25f);
                });
            }
        }
    }
}
