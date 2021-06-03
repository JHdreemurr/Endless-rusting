package rusting.entities.units;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Damage;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Layer;
import rusting.content.Fxr;
import rusting.content.RustingUnits;

import static mindustry.Vars.state;

public class CraeUnitEntity extends UnitEntity {

    private float shake = 0;
    private final float timeOffset = 3;
    public float xOffset = 0, yOffset = 0;
    public float alphaDraw = 0;
    public float pulse = 0;

    public CraeUnitType unitType(){
        return type instanceof CraeUnitType ? (CraeUnitType) type : null;
    }

    public CraeUnitEntity craeUnit(){
        return (CraeUnitEntity) this;
    }

    public void addPulse(float pulse){
        this.pulse += pulse;
        clampPulse();
    }

    public void clampPulse(){
        Math.max(Math.min(pulse, unitType().pulseAmount), 0);
    }

    public float chargef(){
        return pulse/unitType().pulseStorage;
    }

    @Override
    public void update() {
        super.update();
        if(shake >= timeOffset){
            xOffset = (float) (hitSize/8 * 0.3 * Mathf.range(2));
            yOffset = (float) (hitSize/8 * 0.3 * Mathf.range(2));
        }
        else shake++;
        alphaDraw = Mathf.absin(Time.time/100, chargef(), 1);
    }

    @Override
    public void draw() {
        super.draw();
        Draw.reset();
        if(pulse > 0) {
            if(elevation < 0.9) Draw.z(Layer.bullet);
            else if(type().lowAltitude) Draw.z(Layer.flyingUnitLow + 0.1f);
            else Draw.z(Layer.flyingUnit + 0.1f);

            float rotation = this.rotation - 90;

            Draw.color(unitType().chargeColourStart, unitType().chargeColourEnd, chargef());
            Draw.alpha(alphaDraw * unitType().overloadedOpacity);
            TextureRegion chargeRegion = unitType().pulseRegion;
            TextureRegion shakeRegion = unitType().shakeRegion;
            Draw.rect(shakeRegion, x + xOffset, y + yOffset, (chargeRegion.width + yOffset)/4, (chargeRegion.height + xOffset)/4, rotation);
            Draw.rect(chargeRegion, x, y, rotation);
            Draw.alpha((float) (alphaDraw * unitType().overloadedOpacity * 0.5));
            Draw.rect(chargeRegion, x, y, (float) (chargeRegion.height * 1.5/4), (float) (chargeRegion.width * 1.5/4), rotation);
        }
    }

    @Override
    public void destroy() {
        if(!isAdded()) return;
        float power = chargef() * 150.0F;
        float explosiveness = 1F;
        if (!spawnedByCore) {
            Damage.dynamicExplosion(x, y, 0, explosiveness, power, bounds() / 2.0F, state.rules.damageExplosions, item().flammability > 1, team);
        }
        Fxr.pulseSmoke.at(x, y, rotation, type);
        super.destroy();
    }

    @Override
    public void write(Writes w) {
        super.write(w);
        w.f(pulse);
    }

    @Override
    public void read(Reads r){
        super.read(r);
        pulse = r.f();
    }

    @Override
    public int classId(){
        return RustingUnits.classID(CraeUnitEntity.class);
    }

}
