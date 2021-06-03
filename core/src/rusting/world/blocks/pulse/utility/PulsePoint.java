package rusting.world.blocks.pulse.utility;

import rusting.world.blocks.pulse.PulseBlock;

public class PulsePoint extends PulseBlock {

    public float pulseAmount;

    public boolean bursts;

    public PulsePoint(String name) {
        super(name);
        bursts = false;
        pulseAmount = 0.15f;
    }
}
