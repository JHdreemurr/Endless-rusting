package rusting;

import arc.struct.Seq;
import mindustry.mod.Mod;

import rusting.content.*;

public class EndlessRusting extends Mod{
    
    private static Seq<ContentList> contentLists = Seq.with(
        new RustingBullets(),
        new RustingBlocks()
    );
    
    @Override
    public void init(){
        
    }
    
    @Override
    public void loadContent(){
        contentLists.each(e -> e.load());
    }
}
