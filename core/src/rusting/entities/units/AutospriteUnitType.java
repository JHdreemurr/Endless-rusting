package rusting.entities.units;

import mindustry.type.UnitType;
import rusting.graphics.Drawr;

public class AutospriteUnitType extends UnitType {


    public AutospriteUnitType(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        Drawr.fullSpriteGenerator(this);
    }
}
