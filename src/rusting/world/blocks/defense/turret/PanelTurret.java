package rusting.world.blocks.defense.turret;

import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import mindustry.entities.bullet.BulletType;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import rusting.entities.holder.PanelHolder;

//Multi panel panel now supported
public class PanelTurret extends PowerTurret {
    public Seq<PanelHolder> panels = new Seq<>();

    @Override
    public void load() {
        super.load();
        panels.forEach(panel -> {
            panel.load(panel.name);
        });
    }

    public PanelTurret(String name) {
        super(name);
    }

    public class PanelTurretBuild extends PowerTurretBuild {

        @Override
        protected void shoot(BulletType type) {
            super.shoot(type);
            panels.forEach(panel ->{
                if(!panel.independentBehaviour) panel.shoot(this);
            });
        }

        public void updateTile(){
            super.updateTile();
            panels.forEach(panel -> {
                panel.update(this);
            });
        }

        public void draw(){

            super.draw();
            Draw.reset();

            panels.forEach(panel -> {
                panel.draw(this);
            });
        }
    }
}
