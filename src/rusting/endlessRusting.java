package rusting;

import arc.struct.Seq;
import mindustry.mod.Mod;

import rusting.content.*;

public class EndlessRusting extends Mod{
    
    private Seq<ContentList> contentLists = Seq.with(
        new RustingBullets()
    );
    
    @Override
    public void init(){
        
    }
    
    @Override
    public void loadContent(){
        contentLists.each(e -> e.load());
    }
}
