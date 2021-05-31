package rusting.world.blocks.pulse.distribution;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import rusting.world.blocks.pulse.PulseBlock;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

//a block which can connect to other pulse blocks and transmit a pulse
public class PulseNode extends PulseBlock {
    //Reload of the node till it can transmit a pulse to a nearby block
    public float pulseReloadTime = 60;
    //How many bursts the node sends
    public float pulseBursts = 1;
    //Spacing between bursts
    public float pulseBurstSpacing = 0;
    //How much energy is transmitted
    public float energyTransmission = 3;
    //How many nodes you can connect to
    public int connectionsPotential = 1;
    //Range of the node
    public double laserRange = 15;
    //Colour of the laser
    public Color laserColor = chargeColourStart;

    public PulseNode(String name) {
        super(name);
        configurable = true;
        hasPower = false;
        consumesPower = false;
        outputsPower = false;
        canOverdrive = false;
        swapDiagonalPlacement = true;
        schematicPriority = -10;
        drawDisabled = false;


        config(Integer.class, (PulseNodeBuild entity, Integer i) -> {
            Building other = world.build(i);
            if(!(other instanceof PulseBlockBuild)) return;
            if(entity.connections.contains(i)){
                //unlink
                entity.connections.remove(i);
            }
            else if(nodeCanConnect(entity, other)){
                entity.connections.add(i);
            }
        });
    }

    @Override
    public void setStats(){
        super.setStats();
        this.stats.add(Stat.range, (float) laserRange);
        this.stats.add(Stat.reload, pulseReloadTime /60);
    }

    @Override
    public void setPulseStats() {
        super.setPulseStats();
        pStats.pulseReloadTime.setValue(60/pulseReloadTime);
        pStats.energyTransmission.setValue(energyTransmission);
        pStats.pulseBursts.setValue(pulseBursts);
        pStats.pulseBurstSpacing.setValue(pulseBurstSpacing);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Tile tile = world.tile(x, y);

        if(tile != null && laserRange > 0) {
            Lines.stroke(1f);
            Draw.color(Pal.placing);
            Drawf.circles(x * tilesize + offset, y * tilesize + offset, (float) (laserRange * tilesize));
        }
    }

    public static boolean nodeCanConnect(PulseNodeBuild build, Building target){
        return (!(target instanceof PulseBlockBuild) || ((PulseBlockBuild) target).connectableTo()) && !build.connections.contains(target.pos()) & build.connections.size < ((PulseNode) (build.block)).connectionsPotential && ((PulseNode) build.block).laserRange * 8 >= Mathf.dst(build.x, build.y, target.x, target.y);
    }

    protected void getPotentialLinks(PulseNode.PulseNodeBuild build, Team team, Seq<Building> others){

        tempTileEnts.clear();

        Geometry.circle((int)build.x, (int)build.y, (int)(laserRange + 2), (x, y) -> {
            Building other = world.build(x, y);
            if(nodeCanConnect(build, other)){
                tempTileEnts.add(other);
            }
        });

        tempTileEnts.sort((a, b) -> {
            int type = -Boolean.compare(a.block instanceof PulseNode, b.block instanceof PulseNode);
            if(type != 0) return type;
            return Float.compare(a.dst2(build), b.dst2(build));
        });
    }


    public class PulseNodeBuild extends PulseBlock.PulseBlockBuild{
        public Seq<Integer> connections = new Seq();
        public Seq<Integer> loadSeq = new Seq<>();
        public float reload = 0;

        public void dropped(){
            connections.clear();
        }

        @Override
        public boolean onConfigureTileTapped(Building other){

            if(this == other){
                deselect();
                return false;
            }

            if(nodeCanConnect(this, other) || connections.contains(other.pos())){
                configure(other.pos());

                return false;
            }


            return super.onConfigureTileTapped(other);
        }

        @Override
        public void updateTile() {
            super.updateTile();

            connections.each(l -> {
                Building j = world.build(l);
                if(j == null || j.isNull() || !j.isAdded()) {
                    connections.remove(l);
                }
            });

            if(reload >= pulseReloadTime && chargef(true) >= minRequiredPulsePercent) {
                interactConnected();
                reload = 0;
            }
            else reload += pulseEfficiency();
        }

        public void interactConnected(){
            addPulseConnected();
        }

        public void addPulseConnected(){
            final int[] index = {0};
            connections.each(l -> {
                Building j = world.build(l);
                //need to double check, jic, because I've experienced crashes while a generator was pumping out energy
                if(pulseEnergy < 0 || j == null) return;
                if(index[0] > connectionsPotential) connections.remove(l);
                float energyTransmitted = Math.min(pulseEnergy, energyTransmission);
                if(((PulseBlockBuild)j).canRecievePulse(energyTransmitted)){
                    ((PulseBlockBuild) j).receivePulse(energyTransmitted, this);
                    pulseEnergy -= energyTransmitted;
                }
                index[0]++;
            });
        }

        @Override
        public void drawConfigure(){

            Drawf.circles(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));
            Drawf.circles(x, y, (float) (laserRange * tilesize));


            connections.each(l -> {
                Building link = world.build(l);
                Drawf.square(link.x, link.y, link.block.size * tilesize / 2f + 1f, Pal.place);
            });

            Draw.reset();
        }

        @Override
        public void draw() {
            super.draw();
            connections.each(l -> {
                Building other = world.build(l);
                if(other == null || other.isNull() || !other.isAdded()) return;
                drawLaser((PulseBlockBuild) other, chargef(), laserColor, chargeColourEnd);
            });
        }

        @Override
        public void write(Writes w){
            super.write(w);
            w.f(reload);
            w.s(connections.size);
            for(int i = 0;  i < connections.size; i++){
                w.d(connections.get(i));
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            reload = read.f();
            int n = read.s();
            connections = new Seq<Integer>();
            connections.clear();
            int rpos;
            for(int i = 0;  i < n; i++){
                rpos = ((int) read.d());
                connections.add(rpos);
            }
        }
    }
}