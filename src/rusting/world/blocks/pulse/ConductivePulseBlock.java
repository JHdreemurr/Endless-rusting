package rusting.world.blocks.pulse;

import arc.math.Mathf;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.meta.Stat;

import static mindustry.Vars.tilesize;

//preferably used for batteries since syphons are unlocked later
public class ConductivePulseBlock extends PulseBlock{
    //Reload of the node till it can transmit a pulse to a nearby block. Preferably not every tick
    public float reloadTime = 5;
    //How many bursts the plating sends
    public float bursts = 1;
    //Spacing between bursts
    public float burstSpacing = 0;
    //How much energy is transmitted
    public float energyTransmission = 1;

    public ConductivePulseBlock(String name) {
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        this.stats.add(Stat.reload, reloadTime * 60);
    }

    public class ConductivePulseBlockBuild extends PulseBlockBuild{
        public float reload = 0;

        @Override
        public void updateTile(){
            super.updateTile();
            if(reload >= reloadTime) {
                reload = 0;
                addPulseAdjacent();
            }
            else reload += pulseEfficiency();
        }

        public void addPulseAdjacent(){
            proximity().each(l -> {
                if (pulseEnergy > 0 && l instanceof PulseBlockBuild) {
                    float energyTransmitted = pulseEnergy >= energyTransmission ? energyTransmission : pulseEnergy;
                    if (((PulseBlockBuild) l).canRecievePulse(energyTransmitted) && ((PulseBlockBuild) l).chargef() < chargef()) {
                        ((PulseBlockBuild) l).receivePulse(energyTransmitted, this);
                        pulseEnergy -= energyTransmitted;
                    }
                }
            });
        }
    }
}
