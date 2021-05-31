package rusting.world.blocks.pulse.production;

import mindustry.world.meta.Stat;
import rusting.world.blocks.pulse.distribution.PulseNode;

public class PulseGenerator extends PulseNode {

    public float pulseAmount = 1;
    public float productionTime = 30;

    @Override
    public void setStats(){
        super.setStats();
        this.stats.add(Stat.basePowerGeneration, pulseAmount/productionTime * 60);
    }

    @Override
    public void setPulseStats() {
        super.setPulseStats();
        pStats.pulseProduced.setValue(pulseAmount);
        pStats.pulseProductionInterval.setValue(productionTime/60);
    }

    public PulseGenerator(String name) {
        super(name);
    }

    public class PulseGeneratorBuild extends PulseNode.PulseNodeBuild{
        public float currentProductionTime = 0;

        @Override
        public void updateTile() {
            super.updateTile();
            if(currentProductionTime >= productionTime && allConsValid() && canRecievePulse(pulseAmount)) {
                consume();
                customConsume();
                producePulse();
                currentProductionTime = 0;
            }
            else currentProductionTime += pulseEfficiency();
        }

        //something can happen when the generator makes a pulse, just override the function instead
        public void producePulse(){
            receivePulse(pulseAmount, this);
        }
    }
}
