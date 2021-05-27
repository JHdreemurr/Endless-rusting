package rusting.content;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
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
        }),

    launchCraeWeavers = new Effect(85f, 80f, e -> {
        color(Palr.pulseChargeStart, Color.sky, Palr.pulseChargeEnd, e.fin() * e.fin());
        Draw.alpha(e.fout());
        randLenVectors(e.id, 4, e.fin() * 55f, e.rotation, 6, (x, y) -> {
            float rotx =  Angles.trnsx(e.fslope() * e.fslope(), e.fslope() * e.fslope() * 8, e.fin() * 360 - 90), roty = Angles.trnsx(e.fslope() * e.fslope(), e.fslope() * e.fslope() * 9, e.fin() * 360 - 90);
            Fill.circle(e.x + x + rotx, e.y + y + roty, e.fin() * 3f);
        });


        randLenVectors(e.id, 10, e.fin() * 55f, e.rotation, 6, (x, y) -> {
            Lines.stroke(e.fout());
            float rotx =  Angles.trnsx(e.fout() * e.fout(), e.fout() * e.fout() * 8, e.fout() * 360 - 90), roty = Angles.trnsx(e.fout() * e.fout(), e.fout() * e.fout() * 9, e.fin() * 360 - 90);
            lineAngle(e.x + x + rotx, e.y + y + roty, Mathf.angle(x, y), 1f + e.fout() * 3f);
        });

        Fill.light(e.x, e.y, 15,  42 + 3 * e.fout(), Tmp.c1.set(Palr.pulseChargeStart).a(e.fout() * 0.45f), Tmp.c2.set(Palr.pulseChargeEnd).a(e.fout() * 0.25f));
    }),

    craeCorsair = new Effect(135f, 80f, e -> {
        color(Palr.pulseChargeStart, Color.sky, Palr.pulseChargeEnd, e.fin() * e.fin());
        alpha(e.fout() * e.fout() * 0.5f);
        if(e.data instanceof TextureRegion) Draw.rect((TextureRegion) e.data, e.x, e.y, e.rotation);
        alpha(e.fout() * e.fout());
        for(int i = 0; i < 4; i++){
            float tnx = Angles.trnsx(i * 90 + e.fin() * 360, 0, 3), tny = Angles.trnsy(i * 90 + e.fin() * 360, 0, 3);
            randLenVectors(e.id, 3, e.fin() * 6 + 5, i * 90 - 90, 2, (x, y) -> {
                Lines.lineAngle(e.x + tnx + x, e.y + tny + y, Mathf.angle(x, y), e.fout());
            });
        }
    }),

    craeWeaversResidue = new Effect(32f, 80f, e -> {
        color(Palr.pulseChargeStart, Color.sky, Palr.pulseChargeEnd, e.fin() * e.fin());
        Draw.alpha(e.fout() * 0.65f);
        randLenVectors(e.id, 1, e.fin() * 55f, e.rotation, 3, (x, y) -> {
            float rotx =  Angles.trnsx(e.fslope(), e.fslope() * 8, e.fin()), roty = Angles.trnsx(e.fslope() * e.fslope() * 360, e.fslope() * 9, e.fin());
            Fill.circle(e.x + x + rotx, e.y + y + roty, e.fin() * 3f);
        });


        randLenVectors(e.id, 3, e.fin() * 55f, e.rotation, 3, (x, y) -> {
            Lines.stroke(e.fout());
            float rotx =  Angles.trnsx(e.fslope(), e.fslope() * 8, e.fin() * 360 - 90), roty = Angles.trnsx(e.fslope(), e.fslope() * 9, e.fin() * 360 - 90);
            lineAngle(e.x + x + rotx, e.y + y + roty, Mathf.angle(x, y), 1f + e.fout() * 3f);
        });
    }),

    craeWeaverShards = new Effect(75f, e -> {
        color(Palr.pulseChargeStart, Color.sky, Palr.pulseChargeEnd, e.fin() * e.fin());
        Draw.alpha(e.fout() * e.fout());
        for(int i = 0; i < 3; i++){
            float tnx = Angles.trnsx(i * 120 + e.finpow() * 360, 0, 3), tny = Angles.trnsy(i * 90 + e.finpow() * 360, 0, 3);
            randLenVectors(e.id, 2, e.fin() * 6 + 5, i * 90 - 90, 2, (x, y) -> {
                Lines.lineAngle(e.x + tnx + x, e.y + tny + y, 0, e.fout() * e.fout());
            });
        }
    });
}