package rusting.world.blocks.pulse.utility;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Building;
import rusting.content.RustingBullets;
import rusting.world.blocks.pulse.distribution.PulseNode;

public class PulseChainNode extends PulseNode {

    //How much of a block's health it can heal
    public float healPercent;
    //cap on healing percent. not a decimal
    public float healingPercentCap = 5;
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
        pStats.healPercent.setValue(60 * 100/pulseReloadTime/Math.min(healPercent, healingPercentCap));
        pStats.overdrivePercent.setValue(overdrivePercent);
        pStats.healPercentFalloff.setValue(healPercentFalloff);
    }

    public class PulseMenderBuild extends PulseNodeBuild{

        public Seq<Building> mended = new Seq<>();

        @Override
        public void overloadEffect() {
            super.overloadEffect();
            healFract(healPercent/600);
        }

        public void affectChainConnected(){
            affectChainConnected(connections, healPercent * pulseEfficiency(), overdrivePercent * pulseEfficiency());
            clearMendedSeq();
        }

        public void clearMendedSeq(){
            mended.clear();
        }

        public void affectChainConnected(Seq<Integer> connectedTargets, float healingPercent, float overdrivePercent){
            float trueHealingPercent = healPercent / Math.max(1, connectedTargets.size);
            connectedTargets.each(l -> {
                Building j = Vars.world.build(l);
                if (trueHealingPercent <= 0 || mended.contains(j)) return;
                mended.add(j);
                j.healFract(Math.min(trueHealingPercent, healingPercentCap) / 100);
                j.applyBoost(overdrivePercent, pulseReloadTime + 1f);
                if (j.healthf() != 1) Fx.healBlockFull.at(j.x, j.y, j.block.size, chargeColourEnd);
                else Fx.healBlock.at(j.x, j.y, j.block.size, chargeColourStart);
                if (j instanceof PulseNodeBuild && ((PulseNode) j.block).connectionsPotential > 0 && trueHealingPercent > 0) affectChainConnected(((PulseNodeBuild) j).connections, Math.max(trueHealingPercent - healPercentFalloff, 0), overdrivePercent);
            });
        }

        @Override
        public void interactConnected(){
            if(!canOverload || !requiresOverload || overloaded()) affectChainConnected();
            super.interactConnected();
        }
    }
}
