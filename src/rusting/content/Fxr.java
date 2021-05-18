package rusting.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import mindustry.entities.Effect;
import mindustry.gen.Bullet;
import mindustry.gen.Posc;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.graphics.Pal;

import java.lang.reflect.Field;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;

public class Fxr{
    public static final Effect

            none = new Effect(0, 0f, e -> {}),

            blackened = new Effect(35, 0f, e -> {
                color(Color.black, Color.black, e.fin());
                randLenVectors(e.id, 2, e.finpow() * 3, e.rotation, 360, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, (float) (e.fout() * 1 + Math.sin(e.fin() * 2 * Math.PI)));
                    Fill.circle(e.x + x, e.y + y, (float) (e.fout() * 1.2 + Math.sin(e.fin() * 2 * Math.PI)));
                });
                Draw.reset();
                color(Color.valueOf("#9c7ae1"), Color.valueOf("#231841"), e.fin());
                Draw.alpha(0.35F * e.fout());
                randLenVectors(e.id, 2, e.finpow() * 5, e.rotation, 360, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, (float) (e.fout() * 1 + Math.sin(e.fin() * 2 * Math.PI)));
                });
            }),

            singingFlame = new Effect(18, e ->{
                color(Pal.lightPyraFlame, Pal.darkPyraFlame, e.fin() * e.fin());
                float vx = e.x, vy = e.y;
                if(e.data instanceof Position){
                    //fidn the offset from the bullet to it's data
                    vx += ((Bullet) e.data()).getX() * e.fin() - e.x;
                    vy += ((Bullet) e.data()).getY() * e.fin() - e.y;
                }
                float finalVx = vx;
                float finalVy = vy;
                randLenVectors(e.id, 3, 2f + e.fin() * 16f, e.rotation + 180, 15, (x, y) -> {
                    Fill.circle(finalVx + x, finalVy + y, 0.2f + e.fout() * 1.5f);
                });
            }),

            paveFlame = new Effect(45, e ->{
                randLenVectors(e.id, 5, 7f + e.fin() * 16f, e.rotation + 180, 15, (x, y) -> {
                    color(Pal.lighterOrange, Pal.lightFlame, Math.abs(x * y/4));
                    Fill.circle(e.x + x, e.y + y, e.fout() * e.fout() * 2.3f);
                });
            }),

            shootMhemFlame = new Effect(25f, 80f, e -> {
                color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin() * e.fin());

                randLenVectors(e.id, 6, e.finpow() * 45f, e.rotation, 10f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.6f);
                });
            });
    }