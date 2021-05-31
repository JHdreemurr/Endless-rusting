package rusting;

import arc.struct.Seq;
import mindustry.ctype.ContentList;
import mindustry.mod.Mod;
import rusting.content.*;
import rusting.entities.holder.ItemScoreHolder;

public class EndlessRusting extends Mod{
    public static String modname = "endless-rusting";
    private static final Seq<ContentList> contentLists = Seq.with(
        new RustingStatusEffects(),
        new RustingItems(),
        new RustingBullets(),
        new RustingUnits(),
        new RustingBlocks(),
        new RustingWeathers()
    );

    public EndlessRusting(){
        /*
        Events.on(EventType.ClientLoadEvent.class,
            e -> {
                itemScorer = new ItemScoreHolder();
                itemScorer.setupItems();
                itemScorer.itemScores.each(module -> {
                    Log.info(module.item + " score: " + module.score);
                });
            }
        );

         */
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
