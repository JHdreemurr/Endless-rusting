package rusting.entities.holder;

import arc.Core;
import arc.struct.Seq;

public class StatGroup {
    public Seq<StatHolder> stats;
    public StatHolder conditionalStat;
    public boolean alwaysLoad = false;
    public String header = "defaultheader";

    public StatGroup(Seq<StatHolder> seq){
        stats = seq;
    }

    public StatGroup(Seq<StatHolder> seq, StatHolder condition){
        stats = seq;
        conditionalStat = condition;
    }

    public StatGroup(Seq<StatHolder> seq, StatHolder condition, String input){
        stats = seq;
        conditionalStat = condition;
        header = input;
    }

    public StatGroup(Seq<StatHolder> seq, Boolean load) {
        stats = seq;
        alwaysLoad = load;
    }
    public StatGroup(Seq<StatHolder> seq, Boolean load, String input){
        stats = seq;
        alwaysLoad = load;
        header = input;
    }

    public boolean canLoadStats(){
        return alwaysLoad || (conditionalStat.hasValue() && !conditionalStat.getValue().equals("false"));
    }

    public String header(){
        return header;
    }
}
