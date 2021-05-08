package rusting.type;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;

public class CrystalStatusEffect extends StatusEffect {
    public TextureRegion crystalRegion;
    public float colorOpacity;
    public Color drawColor;


    @Override
    public void load() {
        super.load();
        crystalRegion = Core.atlas.find(name + "-crystal");
    }

    public CrystalStatusEffect(String name) {
        super(name);
        this.speedMultiplier = 0;
        this.reloadMultiplier = 1.35f;
        this.drawColor = Color.sky;
        this.colorOpacity = 0.1f;
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);
        Draw.color(drawColor, Color.white, (float) (Mathf.absin(Time.time, 1) * 0.45 + 0.35));
        Draw.z(Layer.groundUnit + 0.1f);
        Draw.alpha((float) (Mathf.absin(Time.time, 1) * 0.45 + 0.35));
        Draw.rect(crystalRegion, unit.x, unit.y, 0);
        Drawf.light(Team.derelict, unit.x, unit.y, unit.type.hitSize, drawColor, colorOpacity);
    }
}
