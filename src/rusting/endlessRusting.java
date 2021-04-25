package rusting;

import mindustry.mod.Mod;
import arc.util.Log;

public class Rusting extends Mod{

    @Override
    public void init(){
        private contentLists = new Seq(rBullets());
        Events.onWorldLoadEvent(e -> {
            Log.info(rBullets.basicBulletT)
        });
    }
    
    @Override
    public void loadContent(){
        @Overide
        public void loadContent(){
            contentLists.forEach(e -> {
                e.load()
            })
        }
    }
}
