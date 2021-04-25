package EndlessRusting;

import mindustry.mod.Mod;
import arc.util.Log;

public class EndlessRusting extends Mod{

    @Override
    public void init(){
        private Seq contentLists = new Seq(rBullets());
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
