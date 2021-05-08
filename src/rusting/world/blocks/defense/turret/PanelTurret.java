package rusting.world.blocks.defense.turret;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import mindustry.graphics.Layer;
import mindustry.world.blocks.defense.turrets.PowerTurret;

//one pannel turrets
public class PanelTurret extends PowerTurret {
    public float panelDampening = 3f;
    public int panelX = 0, panelY = 0;
    protected TextureRegion panelRegion, panelOutlineRegion, panelHeatRegion;

    public void load(){
        super.load();
        panelRegion = Core.atlas.find(name + "-panel");
        panelOutlineRegion = Core.atlas.find(name + "-panel-outline");
        panelHeatRegion = Core.atlas.find(name + "-panel-heat");
        if(panelHeatRegion == Core.atlas.find("error")) panelHeatRegion = panelRegion;
    }

    public PanelTurret(String name) {
        super(name);
    }

    public class PanelTurretBuild extends PowerTurretBuild {

        public void draw(){
            super.draw();
            Draw.reset();
            float px = panelX * (1 + heat/panelDampening - recoil/3), py = panelY - recoil * heat;
            float panx = x + Angles.trnsx(rotation - 90, px, py), pany = y + Angles.trnsy(rotation - 90, px, py);
            Draw.z(Layer.turret - 1);
            Draw.rect(panelRegion, panx, pany, rotation - 90);
            Draw.rect(panelOutlineRegion, panx, pany, rotation - 90);
            Draw.color(heatColor, heatColor, panelRegion == panelHeatRegion ? reload/reloadTime/4 : reload/reloadTime);
            Draw.alpha(panelRegion == panelHeatRegion ? reload/reloadTime/4 : reload/reloadTime);
            Draw.rect(panelHeatRegion, panx, pany, rotation - 90);
        }
    }
}
