package rusting.content;

import arc.graphics.Color;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.content.StatusEffects;
import mindustry.ctype.*;
import mindustry.gen.Sounds;
import mindustry.type.Weather;
import mindustry.world.meta.Attribute;
import rusting.type.weather.*;

public class RustingWeathers implements ContentList{
    public static Weather
            //destructive
            fossilStorm, corrosiveDeluge;

    @Override
    public void load(){
        fossilStorm = new BulletParticleWeather("fossil-storm"){{
            particleBullet = RustingBullets.fossilShard;
            dynamicSpawning = false;
            randRange = new Vec2(4, 4);
            color = noiseColor = Color.valueOf("#c4cf6f");
            particleRegion = "particle";
            drawNoise = true;
            useWindVector = true;
            sizeMax = 8;
            sizeMin = 4;
            minAlpha = 0.1f;
            maxAlpha = 0.8f;
            density = 1850;
            baseSpeed = 3.45f;
            status = StatusEffects.wet;
            statusDuration = 500f;
            opacityMultiplier = 0.45f;
            force = 0.15f;
            sound = Sounds.wind;
            soundVol = 0.7f;
            duration = 2 * Time.toMinutes;
            attrs.set(Attribute.light, -0.4f);
            attrs.set(Attribute.water, -0.2f);
            attrs.set(Attribute.water, -0.2f);
        }};

        corrosiveDeluge = new BulletParticleWeather("corrosive-deluge") {{
            color = noiseColor = regionColour = Color.coral;
            dynamicSpawning = false;
            chanceSpawn = 0;
        }};
    }
}
