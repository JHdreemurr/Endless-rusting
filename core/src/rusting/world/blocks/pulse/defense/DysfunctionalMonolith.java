package rusting.world.blocks.pulse.defense;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import rusting.content.Fxr;
import rusting.world.blocks.pulse.PulseBlock;

public class DysfunctionalMonolith extends PulseBlock {

    //ticks before the block can shoot again
    public float reloadTime = 60;
    //number of shots
    public float shots = 1;
    //you know where this is going
    public float bursts = 3;
    //separate shot and burst count
    public float burstSpacing = 0;
    //inaccuracy
    public float inaccuracy = 0;
    //sound pitch
    public float soundPitchMin = 0.1f, soundPitchMax = 0.3f;
    //shoot sound
    public Sound shootSound = Sounds.spark;
    //effect played when you start shooting
    public Effect shootStartEffect = Fxr.launchCraeWeavers, corsairEffect = Fxr.craeCorsair;
    public TextureRegion hologramRegion;

    public DysfunctionalMonolith(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        hologramRegion = Core.atlas.find(name + "-hologram");
    }

    @Override
    public boolean canShoot() {
        return true;
    }

    public class DysfunctionalMonolithBuild extends PulseBlockBuild{
        public float randRotation = 0;
        public float retargetTime = 0, reload = 0;
        public boolean randRotationDirection = false, displayHologram = true;
        public float holloPointAlpha = 0;
        public float lockonTime = 0;
        public Vec2 targetPos = new Vec2(x, y);
        public Vec2 holloPos = new Vec2(x, y);
        @Nullable
        public Posc target = null;

        @Override
        public void updateTile() {
            super.updateTile();
            displayHologram = Mathf.randomBoolean(0.85f);
            if(Mathf.randomBoolean(0.1f)) {
                randRotation += 0.1 * (randRotationDirection ? -1 : 1);
            }
            if(Mathf.randomBoolean(0.001f)) randRotationDirection = !randRotationDirection;

            triggerShootingMethods();
        }

        public void triggerShootingMethods(){
            if((retargetTime >= 1 && (!validateTarget(target)) || lockonTime >= 1)) {
                findTarget();
                retargetTime = 0;
            }
            else retargetTime += Math.min(pulseEfficiency(), 1);

            if (shooting()){
                holloPointAlpha = Math.min(holloPointAlpha + 0.01f, 1);
                targetPos.set(target.x(), target.y());
                holloPos.sub(x, y).lerp(Tmp.v1.set(targetPos).sub(x, y), Math.min(0.1f * pulseEfficiency(), 1)).clamp(0, Math.max(dst(targetPos.x, targetPos.y) - 3, 0)).add(x, y);
                if (allConsValid() && reload >= 1) {
                    if(lockonTime >= 1 || Math.abs(angleTo(holloPos.x, holloPos.y) - angleTo(targetPos.x, targetPos.y)) < Math.abs(target instanceof Unit ? ((Unit) target).type.hitSize - 2 + projectile.homingPower/3: ((Building) target).block.size * 8 - 1 + projectile.homingPower/3)) shoot();
                    else lockonTime = Math.min(lockonTime + 0.005f, 1);
                }
            }
            if (reload < 1 && shooting()) reload = Math.min(deltaReload() + reload, 1);
            else if(dst(holloPos) > 5) holloPos.lerp(Tmp.v1.set(x, y), 0.01f);
            else {
                reload = Math.max(reload - deltaReload(), 0);
                holloPointAlpha = Math.max(holloPointAlpha - 0.01f, 0);
            };
        }

        public float deltaReload(){
            return Time.delta * 1 / reloadTime * pulseEfficiency();
        }

        public boolean shooting(){
            return validateTarget(target);
        }

        public boolean validateTarget(Posc inputTarget){
            return inputTarget != null && !Units.invalidateTarget(inputTarget, team == Team.derelict ? Team.sharded : team, x, y) && dst(inputTarget.x(), inputTarget.y()) <= projectileRange();
        }

        public void findTarget(){
            target = Units.closestTarget(team == Team.derelict ? Team.sharded : team, x, y, projectileRange(), unit -> projectile.collidesGround && projectile.collidesAir && !unit.isFlying() && projectile.collidesGround || unit.isFlying() && projectile.collidesAir, tile -> projectile.collidesTiles || projectile.splashDamage > 0 && projectile.collidesGround);
        }

        public void shoot(){
            float speedScl = size * 0.6f;

            startingEffects();
            if(bursts > 1){
                for(int i = 0; i < bursts; i++){
                    Time.run(burstSpacing * i, () -> {
                        effects();
                        for(int i1 = 0; i1 < shots; i1++){
                            bullet(projectile, speedScl);
                        }
                    });
                }
            }
            else if(shots > 1){
                effects();
                for(int i = 0; i < shots; i++){
                    bullet(projectile, speedScl);
                }
            }

            reload = 0;
            lockonTime = 0;
            consume();
            customConsume();
        }

        public void bullet(BulletType bullet, float speedScl){
            Vec2 pointerPos = getPointerPos();
            projectile.create(this, team, pointerPos.x, pointerPos.y, angleTo(holloPos.x, holloPos.y) + Mathf.random(-inaccuracy, inaccuracy), speedScl);
        }

        public void startingEffects(){
            shootStartEffect.at(getPointerPos(), angleTo(getPointerPos()));
            corsairEffect.at(holloPos.x, holloPos.y, angleTo(holloPos.x, holloPos.y), hologramRegion);
        }

        public void effects(){
            shootSound.at(x, y, Mathf.random(soundPitchMin, soundPitchMax));
            projectile.shootEffect.at(getPointerPos(), angleTo(getPointerPos()));
        }

        public Vec2 getPointerPos(){
            return Tmp.v1.set(holloPos).sub(x, y).clamp(-3, 3).add(x, y);
        }

        @Override
        public void draw() {
            super.draw();
            if(displayHologram){
                Draw.color(chargeColourEnd, Color.white, 0.15f);
                if(reload > 0){
                    Draw.alpha(reload * 0.85f);
                    Draw.rect(hologramRegion, getPointerPos(), 270 + randRotation);
                }
                if(holloPointAlpha > 0){
                    Draw.alpha(holloPointAlpha * 0.75f);
                    Draw.rect(hologramRegion, holloPos.x + xOffset * reload, holloPos.y + yOffset * reload, (float) (hologramRegion.height * 1.12/4), (float) (hologramRegion.width * 1.12/4), 270);
                }
            }
        }
    }
}
