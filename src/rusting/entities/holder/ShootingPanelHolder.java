package rusting.entities.holder;

import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.world.blocks.defense.turrets.Turret;
import rusting.world.blocks.defense.turret.PanelTurret;

public class ShootingPanelHolder extends PanelHolder{
    Effect shootEffect = Fx.shootSmall;
    double cooldown = 0.02, restitution = 0.02;
    double recoil = 0, heat = 0;
    public ShootingPanelHolder(String name) {
        super(name);
    }

    @Override
    public void update(PanelTurret.PanelTurretBuild Turret){
        if(independentBehaviour && useTurretAmmo && Turret.isShooting()){
            if(reload < reloadTime) reload += 1/60/reloadTime;
            else if(Turret.hasAmmo() || !useTurretAmmo) shoot(Turret);
        }
        heat = Mathf.lerpDelta((float) heat, 0f, (float) cooldown);
    }

    public void effects(PanelTurret.PanelTurretBuild Turret){
        shootEffect.at(getX(Turret), getY(Turret), rotation(Turret));
        heat = 1;
    }

    @Override
    public void shoot(PanelTurret.PanelTurretBuild Turret){
        shootType.create(Turret, Turret.team, getX(Turret), getY(Turret), Turret.rotation);
        reload = 0;
        if(useTurretAmmo) Turret.useAmmo();
        //like turret shoot code but no
        if(independentBehaviour){
            effects(Turret);
        }
    }
}
//friend blossom_3271