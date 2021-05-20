package rusting.world.blocks.pulse;

import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.gen.Building;
import rusting.content.RustingBullets;

public class PulseChainNode extends PulseNode{

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
        pStats.healPercent.setValue(Math.min(healPercent, healingPercentCap) * pulseReloadTime * 60/100);
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

        public void affectChainConnected(Seq<Building> connectedTargets, float healingPercent, float overdrivePercent){
            float trueHealingPercent = healPercent / Math.max(1, connectedTargets.size);
            connectedTargets.each(l -> {
                if (trueHealingPercent <= 0 || mended.contains(l)) return;
                mended.add(l);
                l.healFract(Math.min(trueHealingPercent, healingPercentCap) / 100);
                l.applyBoost(overdrivePercent, pulseReloadTime + 1f);
                if (l.healthf() != 1) Fx.healBlockFull.at(l.x, l.y, l.block.size, chargeColourEnd);
                else Fx.healBlock.at(l.x, l.y, l.block.size, chargeColourStart);
                if (l instanceof PulseNodeBuild && ((PulseNode) l.block).connectionsPotential > 0 && trueHealingPercent > 0) affectChainConnected(((PulseNodeBuild) l).connections, Math.max(trueHealingPercent - healPercentFalloff, 0), overdrivePercent);
            });
        }

        @Override
        public void interactConnected(){
            if(!canOverload || !requiresOverload || overloaded()) affectChainConnected();
            super.interactConnected();
        }
    }
}
