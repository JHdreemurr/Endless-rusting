package rusting.type.statusEffect;

import arc.func.Cons;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class ConsStatusEffect extends StatusEffect {

    public Cons<Unit> updateCons;

    public ConsStatusEffect(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit, float time) {
        super.update(unit, time);
        if(updateCons != null) updateCons.get(unit);
    }
}
