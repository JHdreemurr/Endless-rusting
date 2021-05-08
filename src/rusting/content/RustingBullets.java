package rusting.content;

import arc.func.Cons;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;
import mindustry.gen.Bullet;
import rusting.entities.bullet.BounceBulletType;
import rusting.math.Mathr;
import rusting.world.blocks.defense.turret.PanelTurret;

public class RustingBullets implements ContentList{
    public static BulletType
    //weather bullets
            fossilShard, mhenShard, fraeShard;
    
    @Override
    public void load(){

        fossilShard = new BounceBulletType(4, 9, "bullet"){{
            width = 7;
            height = 8;
            lifetime = 54;
            hitEffect = Fx.hitFuse;
            despawnEffect = Fx.plasticburn;
            bounceEffect = Fx.blockExplosionSmoke;
            status = RustingStatusEffects.amberstriken;
            statusDuration = 45;
            knockback = 1;
            drag = 0.005f;
            bounciness = 0.6;
        }};

        mhenShard = new BounceBulletType(6, 25, "bullet"){{
            consUpdate = new Cons<Bullet>() {
                @Override
                public void get(Bullet bullet) {
                    Fxr.singingFlame.at(bullet.x, bullet.y, bullet.rotation());
                    //essentualy goes to owner aim pos if owner has instance of the Panel Turret Build.
                    if(bullet.owner instanceof PanelTurret.PanelTurretBuild) bullet.rotation((bullet.angleTo(((PanelTurret.PanelTurretBuild) bullet.owner).targetPos) + Mathf.lerpDelta(bullet.angleTo(((PanelTurret.PanelTurretBuild) bullet.owner).targetPos) - bullet.rotation(), 0, 0.0525f * bullet.fin())));
                }
            };
            despawnEffect = Fx.fireSmoke;
            hitEffect = Fx.fire;
            bounceEffect = Fxr.shootMhemFlame;
            incendChance = 1;
            incendAmount = 10;
            status = StatusEffects.burning;
            statusDuration = 3600;
            width = 10;
            height = 12;
            lifetime = 35;
            hitEffect = Fx.hitFuse;
            trailLength = 0;
            homingPower = 0.125F;
            drag = 0.015f;
            bounciness = 0.95;
        }};

        fraeShard = new BounceBulletType(10, 25, "bullet"){{
            consUpdate = new Cons<Bullet>() {
                @Override
                public void get(Bullet bullet) {
                    fossilShard.create(bullet.owner, bullet.team, bullet.x, bullet.y, bullet.rotation() - 90, Mathr.helix(7, 1, bullet.fin()));
                    fossilShard.create(bullet.owner, bullet.team, bullet.x, bullet.y, bullet.rotation() + 90, Mathr.helix(7, 1, bullet.fin()));
                }
            };
            despawnEffect = Fx.plasticburn;
            hitEffect = Fx.plasticExplosion;
            bounceEffect = Fx.plasticburn;
            status = StatusEffects.corroded;
            statusDuration = 7200;
            width = 10;
            height = 12;
            lifetime = 20;
            hitEffect = Fx.hitFuse;
            trailLength = 0;
            bounciness = 1;
        }};

        UnitTypes.alpha.weapons.get(0).bullet = fraeShard;
        UnitTypes.beta.weapons.get(0).bullet = fossilShard;
        UnitTypes.gamma.weapons.get(0).bullet = mhenShard;
    }
}
