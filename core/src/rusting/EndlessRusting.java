package rusting;

import arc.Events;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ctype.ContentList;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import rusting.content.*;
import rusting.entities.holder.ItemScoreHolder;
import rusting.graphics.Drawr;

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
        Events.on(EventType.ClientLoadEvent.class,
            e -> {
                itemScorer = new ItemScoreHolder();
                itemScorer.setupItems();
            }
        );
    }

    public ItemScoreHolder itemScorer;

    @Override
    public void init(){
        Vars.enableConsole = true;
        Varsr.init();
        Varsr.ui.init();
    }
    
    @Override
    public void loadContent(){
        Drawr.setMethods();
        contentLists.each(ContentList::load);
    }
}
