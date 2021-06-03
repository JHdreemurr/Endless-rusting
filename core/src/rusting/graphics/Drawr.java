package rusting.graphics;


import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.*;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import rusting.EndlessRusting;
import rusting.entities.abilities.MountAbility;
import rusting.math.Mathr;

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

    public static Pixmap blend(PixmapRegion map, PixmapRegion information, float percent, boolean clearAlpha){
        return blend(map, information, percent, clearAlpha, new Vec2(0, 0), new Vec2(0, 0), new Vec2(
                Math.max(map.width, information.width),
                Math.max(map.height, information.height)
        ));
    }

    //I got bored with the names, so I started treating these like maps
    public static Pixmap blend(PixmapRegion map, PixmapRegion information, float percent, boolean clearAlpha, Vec2 mapPosition, Vec2 informationPosition, Vec2 size){
        Pixmap stencil = new Pixmap((int) size.x, (int) size.y, map.pixmap.getFormat());
        for (int x = 0; x < map.width; x ++){
            for (int y = 0; y < map.height; y ++){
                int point = map.getPixel(x, y);
                //dot
                //dot
                //dot
                //tod
                //To do
                int info = map.getPixel(x, y);
                Color lerpPoint = new Color(point).lerp(new Color(info), percent);
                if(clearAlpha) lerpPoint.a = lerpPoint.a < 0.5 ? 0 : 1;
                stencil.draw(x, y, lerpPoint);
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

    //flips a sprite along the y axis
    public static Pixmap mirror(Pixmap map){
        Pixmap stencil = new Pixmap(map.getWidth(), map.getHeight());
        Vec2 pos = new Vec2(0, 0);
        map.each((x, y) -> {
            pos.set(Mathr.reflect(x, y, map.getWidth()/2, y));
            int point = 0;
            point = map.getPixel((int) pos.x,(int) pos.y);
            stencil.draw(point, x, y);
        });
        return stencil;
    };

    //oh boy, here we go again
    public static void fullSpriteGenerator(UnitType unit){

        Log.info("making the region for " + unit.name);

        //create stencil which we can draw on
        Pixmap stencil;
        //height and width of the full sprite
        int width = 0;
        int height = 0;

        //find the pixmap region for the unit
        PixmapRegion map = Core.atlas.getPixmap(unit.name);

        //sets the width to the unit region's width
        width = map.width;
        //sets the height to the unit region's height
        height = map.height;

        //stores width and height for usage in lambda
        int[] sizes = {width, height};

        //initialize both Seqs for top and bottom weapons. This will be usefully soon
        Seq<Weapon> topWeapons = new Seq<Weapon>();
        Seq<Weapon> botomWeapons = new Seq<Weapon>();
        Seq<MountAbility> drawableAbilites = new Seq<MountAbility>();

        unit.abilities.each(a -> {
            if(a instanceof MountAbility) drawableAbilites.add((MountAbility) a);
        });

        unit.weapons.each(w -> {
            //add weapon to the corresponding Sequence
            if(w.top) topWeapons.add(w);
            else botomWeapons.add(w);
            //this is painful and I regret everything
            float weaponOffsetx = 0;

            boolean regionFound = Core.atlas.isFound(w.region);
            boolean outlineRegionFound = Core.atlas.isFound(w.outlineRegion);

            //finds which region is the largest. the actual weapon, or the outline
            if(regionFound) weaponOffsetx = w.region.width/2;
            if(outlineRegionFound && w.outlineRegion.width/2 > weaponOffsetx) weaponOffsetx = w.outlineRegion.width/2;

            //add x, unit of measurement being pixels
            weaponOffsetx += Math.abs(w.x * 4);

            //actual width, since weapons can be mirrored
            int estimatedWidth = (int) (weaponOffsetx + (weaponOffsetx % 1 > 0 ? 1 : 0));
            if(w.mirror) estimatedWidth *= 2;

            //set size to the highest number. the width or the width
            sizes[0] = Math.max(sizes[0], estimatedWidth);

            //repeat the proceeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeEEEEEEEEEEEEEEEEEEEEEEEEEEEEEES
            float weaponOffsety = 0;

            //finds which region is the largest. the actual weapon, or the outline
            if(regionFound) weaponOffsety = w.region.height/2;
            if(outlineRegionFound && w.outlineRegion.height/2 > weaponOffsety) weaponOffsety = w.outlineRegion.height/2;

            //add y, unit of measurement being pixels
            weaponOffsety += Math.abs(w.y * 4);

            //check if theres overhanging pixels
            int estimatedHeight = (int) (weaponOffsety + (weaponOffsety % 1 > 0 ? 1 : 0));

            sizes[1] = Math.max(sizes[1], estimatedHeight);
        });

        width = sizes[0];
        height = sizes[1];

        stencil = new Pixmap(width, height);

        map = Core.atlas.getPixmap(unit.name + "-outline");

        if(Core.atlas.isFound(unit.region)) stencil.draw(map);

        botomWeapons.each(w -> {
            drawPixmapWeapons(stencil, w, false, false);
        });

        map = Core.atlas.getPixmap(unit.name);

        stencil.draw(map);

        botomWeapons.each(w -> {
            drawPixmapWeapons(stencil, w, false, true);
        });

        topWeapons.each(w -> {

            drawPixmapWeapons(stencil, w, true, true);
        });

        drawableAbilites.each(a -> {
            Pixmap outin;

            outin = Core.atlas.getPixmap(EndlessRusting.modname + "-" + a.mountName).crop();

            for(int i = 0; i < 2; i++){
                if(!a.mirror && i > 1) break;
                int reverse = 1 - i * 2;
                stencil.drawPixmap(
                    outin,
                    (int) (stencil.getWidth()/2 - outin.getWidth()/2 + a.x * 4 * reverse),
                    (int) (stencil.getHeight()/2 - outin.getHeight()/2 - a.y * 4),
                    0,
                    0,
                    outin.getWidth() * reverse,
                    outin.getHeight()
                );
            }
        });

        addTexture(stencil, unit.name + "-full");

    }

    public static void drawPixmapWeapons(Pixmap stencil, Weapon w, boolean top, boolean pastMainRegion){

        Pixmap outin;

        boolean done = false;

        int progress = 0;

        while (!done){

            String regionName = top && progress == 0 || pastMainRegion ? w.name : w.name + "-outline";

            outin = Core.atlas.getPixmap(regionName).crop();

            if(validateRegion(w.name)) for(int i = 0; i < 2; i++){
                if(!w.mirror && i > 1) break;
                int reverse = 1 - i * 2;
                if(reverse == -1) outin = mirror(outin);
                stencil.drawPixmap(
                    outin,
                    (int) (stencil.getWidth()/2 - outin.getWidth()/2 + w.x * 4 * reverse),
                    (int) (stencil.getHeight()/2 - outin.getHeight()/2 - w.y * 4),
                    0,
                    0,
                    outin.getWidth(),
                    outin.getHeight()
                );
            }
            if(progress < 1 && (top || pastMainRegion)) progress++;
            else done = true;
        }
    }

    public static boolean validateRegion(String weapon){
        return Core.atlas.isFound(Core.atlas.find(weapon)) && validateRegion(Core.atlas.getPixmap(weapon));
    }

    public static boolean validateRegion(PixmapRegion weapon){
        return weapon != Core.atlas.getPixmap("none");
    }
}
