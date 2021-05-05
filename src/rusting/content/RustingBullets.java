package rusting.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.Fx;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class RustingBullets implements ContentList{
    public static BulletType
    //weather bullets
            fossilShard;
    
    @Override
    public void load(){
    
        fossilShard = new BasicBulletType(1, 5, "shell"){{
            hitEffect = Fx.hitFuse;
        }};
    }
}
