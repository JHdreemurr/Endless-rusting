package rusting.content;

import arc.graphics.*;
import arc.struct.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;

import static mindustry.type.ItemStack.*;

import static mindustry.Vars.*;

public class RustingBlocks implements ContentList{
    public static Block;
        //environment
        paileanStolnen, paileanPathen
        
    public void load(){
        //region environment
        
        paileanStolnen = new Floor("pailean-stolnen"){{
            speedMultiplier = 0.95f;
            variants = 1;
            attributes.set(Attribute.water, -0.85f);
        }};

        paileanPathen = new Floor("pailean-pathen"){{
            speedMultiplier = 0.8f;
            variants = 2;
            attributes.set(Attribute.water, -0.85f);
            attributes.set(Attribute.heat, 0.075f);
            blendGroup = paileanStolnen;
        }};
        
        //endregion
    
    }
}
