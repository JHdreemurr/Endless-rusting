package rusting.content;

import arc.graphics.Color;
import arc.struct.Seq;
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
import rusting.entities.holder.PanelHolder;
import rusting.entities.holder.ShootingPanelHolder;
import rusting.world.blocks.defense.turret.PanelTurret;
import rusting.world.blocks.pulse.*;

import static mindustry.type.ItemStack.*;

public class RustingBlocks implements ContentList{
    public static Block
        //environment
        paileanStolnen, paileanPathen,
        //pulse
        pulseGenerator, pulseNode, pulseTesla, pulseResonator, pulseSiphon, pulseBarrier, pulseBarrierLarge, pulseResearchCenter, pulseUpkeeper,
        //turrets
        //pannel turrets
        prikend, prsimdeome, prefraecon, pafleaver;
        
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

        //region pulse

        //Generates pulse. Quite good at storing pulse, but expensive
        pulseGenerator = new PulseGenerator("pulse-generator"){{
            requirements(Category.power, with(Items.copper, 90, Items.silicon, 25, Items.titanium, 45));
            size = 3;
            canOverload = true;
            overloadCapacity = 25;
            productionTime = 30;
            pulseAmount = 10f;
            pulseReloadTime = 10;
            energyTransmission = 4.5f;
            connectionsPotential = 3;
            pulseStorage = 75;
            resistance = 0.25;
            productionTime = 25;
            laserOffset = 10;
            laserRange = 7;
        }};

        //Loses power fast, but is great at transmitting pulses to far blocks.
        pulseNode = new PulseNode("pulse-node"){{
            requirements(Category.power, with(Items.copper, 90, Items.silicon, 25, Items.titanium, 45));
            powerLoss = 0.0025f;
            pulseReloadTime = 15;
            energyTransmission = 3f;
            pulseStorage = 25;
            resistance = 0.075;
            laserRange = 13;
            size = 1;
            canOverload = false;
        }};

        //Shoots lightning around itself when overloaded. Easly overloads. Acts as a large power node, with two connections, but slower reload
        pulseTesla = new PulseNode("pulse-tesla"){{
            requirements(Category.power, with(Items.copper, 90, Items.silicon, 25, Items.titanium, 45));
            projectile = RustingBullets.craeBolt;
            projectileChanceModifier = 0.15f;
            powerLoss = 0.00835f;
            pulseReloadTime = 35;
            minRequiredPulsePercent = 0.15f;
            connectionsPotential = 2;
            energyTransmission = 10f;
            pulseStorage = 45;
            overloadCapacity = 15;
            resistance = 0.075;
            laserOffset = 3;
            laserRange = 18;
            size = 2;
            canOverload = true;
        }};

        //stores power for later usage less effectively than nodes, but stores more power. Transmits power to blocks nearby with less pulse power percentage.
        pulseResonator = new ConductivePulseBlock("pulse-resonator"){{
            requirements(Category.power, with(Items.copper, 90, Items.silicon, 25, Items.titanium, 45));
            powerLoss = 0.00425f;
            resistance = 0;
            pulseStorage = 175;
            size = 1;
            canOverload = false;
        }};

        pulseSiphon = new PulseSiphon("pulse-siphon"){{
            requirements(Category.power, with(Items.copper, 90, Items.silicon, 25, Items.titanium, 45));
            powerLoss = 0.000035f;
            siphonAmount = 1.5f;
            pulseReloadTime = 35;
            pulseStorage = 35;
            laserRange = 6;
            canOverload = false;
            size = 1;

        }};

        pulseBarrier = new PulseBlock("pulse-barrier"){{
            requirements(Category.defense, with(Items.copper, 90, Items.silicon, 25, Items.titanium, 45));
            size = 1;
            health = 350 * size * size;
            powerLoss = 0.000035f;
            pulseStorage = 35;
            canOverload = false;
        }};

        pulseBarrierLarge = new PulseBlock("pulse-barrier-large"){{
            requirements(Category.defense, with(Items.copper, 90, Items.silicon, 25, Items.titanium, 45));
            size = 2;
            health = 350 * size * size;
            powerLoss = 0.000035f;
            pulseStorage = 35;
            canOverload = false;
        }};

        pulseResearchCenter = new PulseResearchBlock("pulse-research-center"){{
            requirements(Category.effect, with(Items.copper, 60, Items.lead, 70, Items.silicon, 50));
            size = 2;
            fieldNames.add("pulseStorage");
            fieldNames.add("canOverload");
            //totally random ;)

            databaseQuotes = Seq.with(
                "[cyan] Places of learning",
                "[cyan] Storages of information",
                "[cyan] Database Entries",
                "[#d8e2e0] Welcome back."
            );

            randomQuotes = Seq.with(
                "[cyan] E N L I G H T E N  U S",
                "[lightgrey]Go on, there is much to teach, being of outside",
                "[blue] T H E  P U L S E  C O N S U M E S  A L L",
                "[black] N O T H I N G  V O I D  O F  L I G H T",
                "[purple] R O O M B E R  S M I T H E D  T E S L A",
                "[purple] Sometimes, the ultimate way of of winning a battle is to see how much power you still have to commit warcrimes on a daily basis",
                "[purple] I remember the time before the great crash. It was a wonderful world, being screwed over by those who had only percipiatur.",
                "[sky] B L I N D I N G  L I G H T",
                "[darkgrey] E V E R Y T H I N G, S I M U L A T E D",
                "[#d8e2e0] B E I N G  B E Y O N D  T H I S  C O N F I N E,  L I S T E N, A N D  O B E Y",
                "[#b88041] F A D I N G  L I G H T, G U I D E  U S",
                "[#f5bf79] Our light burns bright, now lets help reignite what was long forgotten",
                "[red] J O I N  U S,\n T R A P P E D,  A N D  I N  A N G U I S H"
            );
        }};

        pulseUpkeeper = new PulseChainNode("pulse-upkeeper"){{
            requirements(Category.effect, with(Items.copper, 60, Items.lead, 70, Items.silicon, 50));
            powerLoss = 0.0000155f;
            minRequiredPulsePercent = 0.5f;
            pulseReloadTime = 115;
            connectionsPotential = 4;
            energyTransmission = 0.5f;
            pulseStorage = 70;
            overloadCapacity = 30;
            laserRange = 10;
            laserOffset = 9;
            healPercent = 8;
            healPercentFalloff = healPercent/4;
            overdrivePercent = 65;
            size = 2;
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
            panels.add(
                new PanelHolder(name){{
                    panelX = 6;
                    panelY = -4;
                }}
            );
        }};

        prefraecon = new PanelTurret("prefraecon"){{
            requirements(Category.turret, with(Items.copper, 60, Items.lead, 70, Items.silicon, 50));
            range = 200f;
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
            panels.add(
                new PanelHolder(name){{
                    panelX = 10;
                    panelY = -4;
                }}
            );
        }};

        pafleaver  = new PanelTurret("pafleaver"){{
            requirements(Category.turret, with(Items.copper, 60, Items.lead, 70, Items.silicon, 50));
            range = 260f;
            recoilAmount = 2f;
            reloadTime = 60f;
            powerUse = 6f;
            shootShake = 2f;
            shootEffect = Fxr.shootMhemFlame;
            smokeEffect = Fx.none;
            heatColor = Pal.darkPyraFlame;
            size = 4;
            health = 280 * size * size;
            shootSound = Sounds.flame2;
            shootType = RustingBullets.paveShard;
            shots = 1;
            panels.add(
                new PanelHolder(name + "1"){{
                    panelX = 10.75;
                    panelY = -4.5;
                }},
                new PanelHolder(name + "2"){{
                    panelX = -10.75;
                    panelY = -4.5;
                }},
                new ShootingPanelHolder(name + "1"){{
                    panelX = 13.75;
                    panelY = -3.75;
                    shootType = RustingBullets.mhenShard;
                }},
                new ShootingPanelHolder(name + "2"){{
                    panelX = -13.75;
                    panelY = -3.75;
                    shootType = RustingBullets.mhenShard;
                }}
            );
        }};
        //endregion
    }
}
