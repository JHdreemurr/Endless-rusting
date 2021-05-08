package rusting.math;

import arc.math.Mathf;

public class Mathr {
    public static float helix(double helixes, float magnitude, float scaling, float base){
        scaling = Math.abs(scaling);
        return Mathf.sin((float) (base * helixes * 3.142)) * scaling * magnitude;
    };

    public static float helix(double helixes, float magnitude, float scaling){
        return helix(helixes, magnitude, scaling, scaling);
    };
}
