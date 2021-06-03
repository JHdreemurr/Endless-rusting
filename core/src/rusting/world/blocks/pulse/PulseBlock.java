package rusting.world.blocks.pulse;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Items;
import mindustry.core.UI;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.*;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import rusting.content.Palr;
import rusting.content.RustingBullets;
import rusting.entities.holder.CustomConsumerModule;
import rusting.entities.holder.CustomStatHolder;
import rusting.world.blocks.pulse.utility.PulseResearchBlock;

import static mindustry.Vars.*;

public class PulseBlock extends Block{

    public CustomStatHolder pStats = new CustomStatHolder();

    public float pulseStorage = 10;
    //decreases pulse energy received
    public float resistance = 0.1f;
    //decreases power over time
    public float powerLoss = 0;
    //minimum power required to work
    public float minRequiredPulsePercent = 0;
    //base efficiency
    public float baseEfficiency = 0.5f;
    //how long before the charged region draw x and y changes
    public int timeOffset = 1;
    //if it requires overloading to work, enable. Default should be true;
    public boolean requiresOverload = true;
    //Bool for whether block can overload. Unused by normal pulse block.
    public boolean canOverload = true;
    //how much the block can store when overloaded
    public float overloadCapacity = 3;
    //Whether the building can be connected to by PulseNodes with power lasers
    public boolean connectable = true;
    //what the block can shoot when overloaded
    public BulletType projectile = RustingBullets.craeShard;
    //chance modifier for projectile spawning
    public float projectileChanceModifier = 1;
    //how far away the laser is from the block, is used for drawing to and from block,
    public float laserOffset = 3;
    //custom consumer module used purely to store values
    public CustomConsumerModule customConsumes = new CustomConsumerModule();
    //whether crux has infinite resources. PVP excluded
    public boolean cruxInfiniteConsume = false;
    //self explanatory
    public boolean needsResearching = true;
    //requirements to be researched
    public ItemStack[] centerResearchRequirements = ItemStack.with(Items.copper, 1);
    //regions for charge and shake
    public TextureRegion chargeRegion, shakeRegion;
    //colours for charge
    public Color chargeColourStart, chargeColourEnd;

    public PulseBlock(String name){
        super(name);
        update = true;
        solid = true;
        hasPower = false;
        group = BlockGroup.power;
        chargeColourStart = Palr.pulseChargeStart;
        chargeColourEnd = Palr.pulseChargeEnd;
    }

    @Override
    public void setStats(){
        super.setStats();
        this.stats.add(Stat.powerCapacity, pulseStorage);
        setPulseStats();
    }

    public void setPulseStats(){
        pStats.pulseStorage.setValue(pulseStorage);
        pStats.resistance.setValue(resistance);
        pStats.powerLoss.setValue(powerLoss * 60);
        pStats.connectable.setValue(connectable);
        pStats.canOverload.setValue(canOverload);
        pStats.requiresOverload.setValue(requiresOverload);
        pStats.overloadCapacity.setValue(overloadCapacity);
        pStats.projectileChanceModifier.setValue(projectileChanceModifier);
        pStats.projectileRange.setValue(projectileRange()/8);
    }

    public void load(){
        super.load();
        chargeRegion = Core.atlas.find(name + "-charged");
        shakeRegion = Core.atlas.find(name + "-shake");
    }


    @Override
    public void setBars(){
        super.setBars();
        bars.add("power", entity -> new Bar(() ->
                Core.bundle.format("bar.pulsebalance", UI.formatAmount((int)(((PulseBlockBuild) entity).pulseEnergy/pulseStorage * 60))),
                () -> Tmp.c1.set(chargeColourStart).lerp(chargeColourEnd,
                         ((PulseBlockBuild) entity).chargef()),
                () -> Mathf.clamp(((PulseBlockBuild) entity).chargef())
        ));
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team){
        //must have been researched, but for now checks if research center exists
        if(tile == null || (needsResearching && !PulseResearchBlock.researched(this, team))) return false;
        return super.canPlaceOn(tile, team);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Tile tile = world.tile(x, y);

        if(tile != null) {
            if(canShoot()){
                Lines.stroke(1f);
                Draw.color(Pal.placing);
                Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, projectileRange(), chargeColourEnd);
                Draw.reset();
            }
            if(!canPlaceOn(tile, player.team()) && needsResearching){
                drawPlaceText(Core.bundle.get(validCenter(player.team()) ? "bar.requitesresearching" : "bar.dosnthavecenter"), x, y, valid);
            }
        }
    }

    //note: only used for display
    public float projectileRange(){
        return (float) (projectile instanceof LightningBulletType ? (projectile.lightningLength * 2 + projectile.lightningLengthRand) * tilesize : projectile.range() * size * 0.6);
    }

    public boolean canShoot(){
        return canOverload;
    }

    public static boolean validCenter(Team team){
        return getCenterTeam(team) != null;
    }

    public static PulseResearchBlock.PulseResearchBuild getCenterTeam(Team team){
        final PulseResearchBlock.PulseResearchBuild[] returnBuilding = {null};
        Groups.build.each(e -> {
            if(e != null && e.team == team && e instanceof PulseResearchBlock.PulseResearchBuild) returnBuilding[0] = (PulseResearchBlock.PulseResearchBuild) e;
        });
        return returnBuilding[0];
    }

    public class PulseBlockBuild extends Building {
        public float pulseEnergy = 0;
        public float falloff = resistance;
        public float xOffset = 0, yOffset = 0, alphaDraw = 0;
        public float shake = 0;

        public float pulseEfficiency(){
            return Math.max(baseEfficiency, chargef(false) * timeScale());
        }

        public void customConsume(){
            pulseEnergy -= customConsumes.pulse;
        }

        public boolean customConsumeValid(){
            return (pulseEnergy >= customConsumes.pulse) || (!state.rules.pvp && (team == Team.derelict || team == state.rules.waveTeam && cruxInfiniteConsume));
        }

        public boolean allConsValid(){
            return customConsumeValid() && ((team == Team.derelict || (team == state.rules.waveTeam && cruxInfiniteConsume)) && !state.rules.pvp || consValid());
        }

        public boolean canRecievePulse(float charge){
            return charge + pulseEnergy < pulseStorage + (canOverload ? overloadCapacity : 0);
        }

        public boolean connectableTo(){
            return connectable;
        }

        public void receivePulse(float pulse, Building source){
            if(canRecievePulse(pulse)) addPulse(pulse, source);
        }

        public void addPulse(float pulse){
            addPulse(pulse, null);
        }

        public void addPulse(float pulse, @Nullable Building building){
            float storage = pulseStorage + (canOverload ? overloadCapacity : 0);
            float resistAmount = (building != this ? falloff : 0);
            pulseEnergy += Math.max(pulse - resistAmount, 0);
            normalizePulse();
        }

        public void removePulse(float pulse){
            removePulse(pulse, null);
        }

        public void removePulse(float pulse, @Nullable Building building){
            float storage = pulseStorage + (canOverload ? overloadCapacity : 0);
            pulseEnergy -= pulse;
            normalizePulse();
        }

        public void normalizePulse(){
            float storage = pulseStorage + (canOverload ? overloadCapacity : 0);
            pulseEnergy = Math.max(Math.min(pulseEnergy, storage), 0);
        }

        public void overloadEffect(){
            //for now, sprays projectiles around itself, and damages itself.
            if(Mathf.chance(overloadChargef() * projectileChanceModifier)) projectile.create(this, team, x, y, Mathf.random(360), (float) ((Mathf.random(0.5f) + 0.3) * size));
        }

        public boolean overloaded(){
            return pulseEnergy > pulseStorage && canOverload;
        }

        public float overloadChargef(){
            return (pulseEnergy - pulseStorage)/overloadCapacity;
        }

        public float chargef(boolean overloadaccount){
            return pulseEnergy/(pulseStorage + (canOverload && overloadaccount ? overloadCapacity : 0));
        }

        public float chargef(){
            return chargef(true);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(shake >= timeOffset){
                xOffset = (float) (block.size * 0.3 * Mathf.range(2));
                yOffset = (float) (block.size * 0.3 * Mathf.range(2));
                alphaDraw = Mathf.absin(Time.time/100, chargef());
            }
            else shake++;
            pulseEnergy = Math.max(pulseEnergy - powerLoss, 0);
            if(overloaded()) overloadEffect();

        }

        @Override
        public void drawSelect(){
            if(canShoot()){
                Drawf.dashCircle(x, y, projectileRange(), chargeColourStart.lerp(chargeColourEnd, chargef()));
            }
            Draw.reset();
        }

        public void drawLaser(PulseBlockBuild building, float lerpPercent, Color laserCol, Color laserCol2) {
            Draw.z(Layer.power);
            float angle = angleTo(building.x, building.y) - 90;
            float sourcx = x + Angles.trnsx(angle, 0, laserOffset), sourcy = y + Angles.trnsy(angle, 0, laserOffset);
            float edgex = building.x + Angles.trnsx(angle + 180, 0, ((PulseBlock) building.block).laserOffset), edgey = building.y + Angles.trnsy(angle + 180, 0, ((PulseBlock) building.block).laserOffset);
            Draw.color(laserCol, laserCol2, lerpPercent);
            Lines.stroke(1.35f);
            Lines.line(sourcx, sourcy, edgex, edgey);
            Fill.circle(edgex, edgey, 1.35f);
            Fill.circle(sourcx, sourcy, 0.85f);
            Draw.reset();
        }

        public void draw(){
            super.draw();
            if(chargeRegion != Core.atlas.find("error")) {
                Draw.z(Layer.bullet);
                Draw.color(chargeColourStart, chargeColourEnd, chargef());
                Draw.alpha(alphaDraw);
                Draw.rect(shakeRegion, x + xOffset, y + yOffset, (chargeRegion.width + yOffset)/4, (chargeRegion.height + xOffset)/4, 270);
                Draw.alpha(chargef());
                Draw.rect(chargeRegion, x, y, 270);
                Draw.alpha((float) (chargef() * 0.5));
                Draw.rect(chargeRegion, x, y, (float) (chargeRegion.height * 1.5/4), (float) (chargeRegion.width * 1.5/4), 270);
            }
        }

        @Override
        public void write(Writes w){
            super.write(w);
            w.f(pulseEnergy);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            pulseEnergy = read.f();
        }
    }
}