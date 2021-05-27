package rusting.content;

import arc.graphics.Color;
import mindustry.ctype.ContentList;
import mindustry.type.Item;

public class RustingItems implements ContentList {
    public static Item
        melonaleum, bulastelt
    ;
    @Override
    public void load() {
        melonaleum = new Item("melonaleum", Color.valueOf("#6572ca")){{
            flammability = 0.1f;
            explosiveness = 3;
            radioactivity = 0.25f;
            charge = 2.25f;
            hardness = 3;
            cost = 1.35f;
        }};

        bulastelt = new Item("bulastelt", Color.valueOf("#bcbcbc")){{
            hardness = 2;
            cost = 1.05f;
        }};
    }
}