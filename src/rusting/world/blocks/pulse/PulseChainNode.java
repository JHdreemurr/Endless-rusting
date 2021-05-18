package rusting.world.blocks.pulse;

import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import rusting.content.RustingBullets;

public class PulseChainNode extends PulseNode{

    //How much of a block's health it can heal
    public float healPercent;
    //Speedup percentage for blocks
    public float overdrivePercent;
    //Falloff for heal percent. Not a percent.
    public float healPercentFalloff;

    public PulseChainNode(String name) {
        super(name);
        projectile = RustingBullets.paveBolt;
        projectileChanceModifier = 0.1f;
        pulseReloadTime = 150;
        requiresOverload = false;
    }

    @Override
    public void setPulseStats() {
        super.setPulseStats();
        pStats.healPercent.setValue(healPercent * pulseReloadTime * 60/100);
        pStats.overdrivePercent.setValue(overdrivePercent);
        pStats.healPercentFalloff.setValue(healPercentFalloff);
    }

    public class PulseMenderBuild extends PulseNodeBuild{

        @Override
        public void overloadEffect() {
            super.overloadEffect();
            healFract(healPercent/600);
        }

        public void affectChainConnected(){
            affectChainConnected(connections, healPercent * pulseEfficiency(), overdrivePercent * pulseEfficiency());
        }

        public void affectChainConnected(Seq<Building> connectedTargets, float healingPercent, float overdrivePercent){
            connectedTargets.each(l -> {
                if(healPercent != 0) l.healFract(healPercent/100);
                if(overdrivePercent != 0) l.applyBoost(overdrivePercent, pulseReloadTime + 1f);
                (l.health == l.maxHealth ? Fx.healBlock : Fx.healBlockFull).at(l.x, l.y, l.block.size, Pal.heal.lerp(chargeColourEnd, chargef()));
                if(l instanceof PulseNodeBuild && healingPercent > 0) affectChainConnected(((PulseNodeBuild) l).connections, Math.max(healingPercent - healPercentFalloff, 0), overdrivePercent);
            });
        }

        @Override
        public void interactConnected(){
            if(!canOverload || !requiresOverload || overloaded()) affectChainConnected();
            super.interactConnected();
        }
    }
}
