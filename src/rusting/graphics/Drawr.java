package rusting.graphics;


import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;

public class Drawr {
    //Learned somewhat how to do this from sk's Drawm
    public static Pixmap pigmentae(PixmapRegion map, Color pigment, float percent){
        Pixmap stencil = new Pixmap(map.width, map.height, map.pixmap.getFormat());
            for (int x = 0; x < map.width; x ++){
                for (int y = 0; y < map.height; y ++){
                    int point = map.getPixel(x, y);
                    Color lerpPoint = new Color(point).lerp(pigment, percent);
                    if(lerpPoint.a != percent && lerpPoint.a == pigment.a) stencil.draw(x, y, lerpPoint);
                }
            }
        return stencil;
    }

    public static Pixmap pigmentae(PixmapRegion map, Color pigment){
        return pigmentae(map, pigment, 0.5f);
    }

    public static @Nullable TextureRegion addTexture(Pixmap map, String name){
        Texture texture  = new Texture(map);
        return Core.atlas.addRegion(name, new TextureRegion(texture));
    }
}
