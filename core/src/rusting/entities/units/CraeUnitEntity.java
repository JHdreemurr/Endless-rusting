package rusting.entities.units;

import mindustry.gen.UnitEntity;
import rusting.content.RustingUnits;

public class CraeUnitEntity extends UnitEntity {

    public float repairRange(){
        return ((CraeUnitType) type()).repairRange;
    }

    public float pulseAmount(){
        return ((CraeUnitType) type()).pulseAmount;
    }

    public float pulseGenRange(){
        return ((CraeUnitType) type()).pulseGenRange;
    }

    @Override
    public int classId(){
        return RustingUnits.classID(CraeUnitEntity.class);
    }

}
