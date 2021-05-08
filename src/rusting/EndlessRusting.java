package rusting;

import arc.struct.Seq;
import mindustry.ctype.ContentList;
import mindustry.mod.Mod;

import rusting.content.*;

public class EndlessRusting extends Mod{
    public static String modname = "endless-rusting";
    private static final Seq<ContentList> contentLists = Seq.with(
        new RustingStatusEffects(),
        new RustingBullets(),
        new RustingBlocks(),
        new RustingWeathers()
    );
    
    @Override
    public void init(){
        
    }
    
    @Override
    public void loadContent(){
        contentLists.each(e -> e.load());
    }
}
