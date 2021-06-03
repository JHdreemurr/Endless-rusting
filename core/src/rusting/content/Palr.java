package rusting.content;

import arc.graphics.Color;
import mindustry.graphics.Pal;

public class Palr {
    public static Color
        pulseChargeStart = new Color(Color.sky).lerp(Pal.lightTrail, 0.35f),
        pulseChargeEnd = new Color(Color.sky).lerp(Pal.lightTrail, 0.25f).lerp(Color.valueOf("#a4ddf2"), 0.05f);
    ;
}
