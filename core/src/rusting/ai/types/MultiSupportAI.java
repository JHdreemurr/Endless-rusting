package rusting.ai.types;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Interval;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.ai.types.FlyingAI;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.entities.units.UnitCommand;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.meta.BlockFlag;
import rusting.entities.units.CraeUnitType;
import rusting.world.blocks.pulse.PulseBlock.PulseBlockBuild;

import static mindustry.Vars.indexer;
import static mindustry.Vars.state;

public class MultiSupportAI extends FlyingAI {

    protected Interval mineTimer = new Interval(4);
    protected Interval statTimer = new Interval(25);

    protected Vec2 movePos = new Vec2(0, 0);

    public Unit unitAlly = null;
    public Building blockAlly = null;
    boolean mining = true;
    public boolean canTargetAllies = false;
    public boolean canHealAllies = false;
    public float minRepairRange = 45;
    public float pulseAmount = 0;
    public float pulseGenRange = 0;
    public boolean canGenPulse = false;
    public boolean initializedCstats = false;
    Item targetItem;
    Tile ore;

    public MultiSupportAI(){
        super();
    }

    @Override
    public void updateUnit() {
        super.updateUnit();
        if(!initializedCstats){
            initializedCstats = true;
            if(unit.type instanceof CraeUnitType) {
                CraeUnitType unitType = (CraeUnitType) unit.type;
                minRepairRange = unitType.repairRange;
                pulseAmount = unitType.pulseAmount;
                pulseGenRange = unitType.pulseGenRange;
                if(pulseAmount > 0 && pulseGenRange > 0) canGenPulse = true;
                minRepairRange = Math.min(pulseGenRange, minRepairRange);
            }
        }
    }

    @Override
    public void updateMovement(){

        boolean continueFunction = false;
        Building core = state.teams.closestCore(unit.x, unit.y, unit.team);
        Building repairPoint = (Building) targetFlag(unit.x, unit.y, BlockFlag.repair, false);
        if((unit.healthf() < 0.45f && target != null || unit.healthf() < 0.75) && (repairPoint != null || core != null)){
            Building targetBuilding = repairPoint != null ? repairPoint : core;
            if(unit.within(targetBuilding, unit.healthf() * 10 * 7)) continueFunction = true;
            else{
                moveTo(targetBuilding, unit.healthf() * 10 * 7, 25f);
                unit.lookAt(targetBuilding);
            }
        }
        else if(invalid(target) && command() != UnitCommand.idle){
            Unit targetAlly = Units.closest(unit.team, unit.x, unit.y, unit.type.range * 4, u -> u.damaged() && u != this.unit);
            boolean foundAlly = false;
            float findRange = minRepairRange;
            if(targetAlly != null){
                movePos.set(targetAlly.x, targetAlly.y);
                findRange += targetAlly.hitSize/2 * 1.1f;
                foundAlly = true;
            }
            else{
                final Building[] targetBuildingAlly = {null};
                indexer.eachBlock(unit, unit.type.range * 2,
                    other ->
                        other.damaged() ||
                        canGenPulse && other instanceof PulseBlockBuild && ((PulseBlockBuild) other).canRecievePulse(pulseAmount),
                    other -> {
                        if(targetBuildingAlly[0] == null) targetBuildingAlly[0] = other;
                    }
                );
                if(targetBuildingAlly[0] != null) {
                    movePos.set(targetBuildingAlly[0].x, targetBuildingAlly[0].y);
                    findRange += targetBuildingAlly[0].block.size * 4;
                    foundAlly = true;
                }
            }
            if(!foundAlly || Mathf.dst(unit.x, unit.y, movePos.x, movePos.y) <= findRange) continueFunction = true;
            else{
                moveTo(movePos, findRange, 5f);
                unit.lookAt(movePos);
            }
        }
        else continueFunction = true;

        if(continueFunction){
            if(unit.canMine() && core != null && command() != UnitCommand.attack || invalid(target) || !unit.hasWeapons() || unit.disarmed()){

                if (unit.mineTile != null && !unit.mineTile.within(unit, Vars.miningRange)) {
                    unit.mineTile(null);
                }

                if (mining) {
                    if (mineTimer.get(timerTarget2, 60 * 4) || targetItem == null) {
                        if(core != null) targetItem = unit.team.data().mineItems.min(i -> indexer.hasOre(i) && unit.canMine(i), i -> core.items.get(i));
                    }

                    //core full of the target item, do nothing
                    if (targetItem != null && core != null && core.acceptStack(targetItem, 1, unit) == 0) {
                        unit.clearItem();
                        unit.mineTile(null);
                        return;
                    }

                    //if inventory is full, drop it off.
                    if (unit.stack.amount >= unit.type.itemCapacity || (targetItem != null && !unit.acceptsItem(targetItem))) {
                        mining = false;
                    } else {
                        if (mineTimer.get(timerTarget, 60) && targetItem != null) {
                            ore = indexer.findClosestOre(unit, targetItem);
                        }

                        if (ore != null) {
                            moveTo(ore, Math.min(unit.range(), Vars.miningRange / 2f), 20f);

                            if (unit.within(ore, Vars.miningRange)) {
                                unit.mineTile = ore;
                            }

                            if (ore.block() != Blocks.air) {
                                mining = false;
                            }
                        }
                    }
                } else {
                    unit.mineTile = null;

                    if (unit.stack.amount == 0) {
                        mining = true;
                        return;
                    }

                    if (unit.within(core, Math.min(unit.type.range, Vars.mineTransferRange))) {
                        if (core != null && core.acceptStack(unit.stack.item, unit.stack.amount, unit) > 0) {
                            Call.transferItemTo(unit, unit.stack.item, unit.stack.amount, unit.x, unit.y, core);
                        }

                        unit.clearItem();
                        mining = true;
                    }

                    circle(core, Vars.mineTransferRange / 1.8f);
                }
            }
            else if(unit.hasWeapons() && !invalid(target)){
                moveTo(target, Math.max(unit.type.range/2 - 20 * unit.healthf(), unit.hitSize/2), 20f);
                unit.lookAt(target);
            }
        }
    }

    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground) {
        Teamc t = null;
        Teamc targ1 = null;
        Unit[] closestAllly = {null};
        Units.nearby(this.unit.team, x, y, range * 6, u -> {
            if(Units.closestTarget(u.team, u.x, u.y, range * 2) != null){
                if(closestAllly[0] == null) closestAllly[0] = u;
                else if(u.damaged()) closestAllly[0] = u;
            }
        });

        Unit targetAlly = closestAllly[0];

        if (targetAlly != null)
            targ1 = Units.closestTarget(this.unit.team, targetAlly.x, targetAlly.y, range * 2, u -> u.checkTarget(air, ground));

        Teamc targ2 = Units.closestTarget(this.unit.team, x, y, range * 2, u -> u.checkTarget(air, ground), b -> ground);
        if (targ1 != null && targ2 != null) {
            if (targ1 == targ2) t = targ1;
            else{
                Vec2 pos1 = Tmp.v1.set(targ1.x(), targ1.y());
                Vec2 pos2 = Tmp.v2.set(targ2.x(), targ2.y());
                if(Mathf.dst(pos1.x, pos1.y, this.unit.x, this.unit.y) < Mathf.dst(pos2.x, pos2.y, this.unit.x, this.unit.y) * 5){
                    t = targ1;
                }
                else {
                    t = targ2;
                }
            }
        }
        else if(unit.team == state.rules.waveTeam && this.targetFlag(x, y, BlockFlag.core, false) == null){
            Teamc result = null;
            if(ground) result = targetFlag(x, y, BlockFlag.generator, true);
            if(result != null) return result;

            if(ground) result = this.targetFlag(x, y, BlockFlag.core, true);
            if(result != null) return result;
        }
        return t;
    }

    @Override
    protected void updateTargeting(){
        //unescecary
        //if(canTargetAllies && !(blockAlly != null && blockAlly.within(unit, unit.range()))) blockAlly = Vars.indexer.findTile(unit.team, unit.x, unit.y, unit.range(), l -> l.damaged());
        //if(canHealAllies && !(unitAlly != null && unitAlly.within(unit, unit.range()) || blockAlly == null)) blockAlly = Units.findDamagedTile(unit.team, unit.x, unit.y);
        if(unit.hasWeapons()){
            if(retarget()) target = findTarget(unit.x, unit.y, unit.range(), unit.type.targetGround, unit.type.targetAir);
            updateWeapons();
        }
    }

    /*
    @Override
    protected void updateWeapons(){
        if(targets.length != unit.mounts.length) targets = new Teamc[unit.mounts.length];

        for(int i = 0; i < targets.length; i++) {

        }
    }
    */
}
