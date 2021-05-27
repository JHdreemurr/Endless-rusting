package rusting.entities.bullet;

import arc.func.Cons;
import arc.util.Nullable;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

//includes cons for some methods in BulletType
public class ConsBulletType extends BasicBulletType {
    @Nullable
    public Cons<Bullet> consUpdate;

    public ConsBulletType(int speed, int damage, String sprite){
        super(speed, damage, sprite);
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        if(consUpdate != null) consUpdate.get(b);
    }
}
