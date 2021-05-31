package rusting.entities.units;

import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class CraeUnitType extends UnitType {

    public float repairRange = 0;
    public float pulseAmount = 0;
    public float pulseGenRange = 0;

    public CraeUnitType(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
    }
}
