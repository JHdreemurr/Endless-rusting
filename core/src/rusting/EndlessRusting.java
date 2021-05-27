package rusting;

import arc.Events;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.ctype.ContentList;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import rusting.content.*;
import rusting.entities.holder.ItemScoreHolder;

public class EndlessRusting extends Mod{
    public static String modname = "endless-rusting";
    private static final Seq<ContentList> contentLists = Seq.with(
        new RustingStatusEffects(),
        new RustingItems(),
        new RustingBullets(),
        new RustingBlocks(),
        new RustingWeathers()
    );

    public EndlessRusting(){
        Events.on(EventType.ClientLoadEvent.class,
            e -> {
                itemScorer = new ItemScoreHolder();
                itemScorer.setupItems();
                itemScorer.itemScores.each(module -> {
                    Log.info(module.item + " score: " + module.score);
                });
            }
        );
    }

    public ItemScoreHolder itemScorer;

    @Override
    public void init(){

    }
    
    @Override
    public void loadContent(){
        contentLists.each(e -> e.load());
    }
}
