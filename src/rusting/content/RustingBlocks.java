package rusting.content;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.ctype.*;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.meta.*;
import mindustry.world.blocks.environment.*;
import rusting.world.blocks.defense.turret.PanelTurret;

import static mindustry.type.ItemStack.*;

public class RustingBlocks implements ContentList{
    public static Block
        //environment
        paileanStolnen, paileanPathen,
        //turrets
        //pannel turrets
        prikend, prsimdeome, prefraecon;
        
    public void load(){
        //region environment
        
        paileanStolnen = new Floor("pailean-stolnen"){{
            speedMultiplier = 0.95f;
            variants = 3;
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

        //region turrets
        prikend = new PowerTurret("prikend"){{
            requirements(Category.turret, with(Items.copper, 60, Items.lead, 70, Items.silicon, 50));
            range = 185f;
            shootLength = 2;
            chargeEffects = 7;
            recoilAmount = 2f;
            reloadTime = 75f;
            cooldown = 0.03f;
            powerUse = 1.25f;
            shootShake = 2f;
            shootEffect = Fx.hitFlameSmall;
            smokeEffect = Fx.none;
            heatColor = Color.orange;
            size = 1;
            health = 280 * size * size;
            shootSound = Sounds.bigshot;
            shootType = RustingBullets.fossilShard;
            shots = 2;
            burstSpacing = 15f;
            inaccuracy = 2;
        }};

        //region turrets
        prsimdeome = new PanelTurret("prsimdeome"){{
            requirements(Category.turret, with(Items.copper, 60, Items.lead, 70, Items.silicon, 50));
            range = 165f;
            chargeEffects = 7;
            recoilAmount = 2f;
            reloadTime = 96f;
            cooldown = 0.03f;
            powerUse = 4f;
            shootShake = 2f;
            shootEffect = Fxr.shootMhemFlame;
            smokeEffect = Fx.none;
            heatColor = Color.red;
            size = 2;
            health = 280 * size * size;
            shootSound = Sounds.flame2;
            shootType = RustingBullets.mhenShard;
            shots = 6;
            spread = 10f;
            burstSpacing = 5f;
            inaccuracy = 10;
            panelX = 6;
            panelY = -4;
        }};

        prefraecon = new PanelTurret("prefraecon"){{
            requirements(Category.turret, with(Items.copper, 60, Items.lead, 70, Items.silicon, 50));
            range = 175f;
            recoilAmount = 2f;
            reloadTime = 65f;
            powerUse = 6f;
            shootShake = 2f;
            shootEffect = Fxr.shootMhemFlame;
            smokeEffect = Fx.none;
            heatColor = Pal.darkPyraFlame;
            size = 3;
            health = 280 * size * size;
            shootSound = Sounds.flame2;
            shootType = RustingBullets.fraeShard;
            panelX = 10;
            panelY = -4;
        }};
        //endregion

    }
}
