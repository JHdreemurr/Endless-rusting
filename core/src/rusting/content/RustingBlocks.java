package rusting.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.ctype.ContentList;
import mindustry.gen.Sounds;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.meta.Attribute;
import rusting.entities.holder.PanelHolder;
import rusting.entities.holder.ShootingPanelHolder;
import rusting.world.blocks.defense.turret.PanelTurret;
import rusting.world.blocks.environment.FixedOreBlock;
import rusting.world.blocks.pulse.*;

import static mindustry.type.ItemStack.with;

public class RustingBlocks implements ContentList{
    public static Block
        //environment
        //liquids
        melainLiquae,
        //floor
        //pailean
        paileanStolnen, paileanPathen, paileanWallen,
        //navy
        classemStolnene, classemPathen, classemWallen,
        //ore blocks
        melonaleum,
        //pulse
        //Pulse collection
        pulseGenerator, pulseCollector,
        //Nodes
        pulseNode, pulseTesla,
        //Storage
        pulseResonator,
        //Siphon
        pulseSiphon,
        //Defense
        pulseBarrier, pulseBarrierLarge,
        //Research
        pulseResearchCenter,
        //Suport
        pulseUpkeeper,
        //turrets
        //environment/turrets
        archangel,
        //pannel turrets
        prikend, prsimdeome, prefraecon, pafleaver;
        
    public void load(){
        //region environment

        melainLiquae = new Floor("melain-liquae"){{
            speedMultiplier = 0.5f;
            variants = 0;
            status = RustingStatusEffects.macotagus;
            statusDuration = 350f;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.5f;
            drawLiquidLight = true;
            emitLight = true;
            lightColor = new Color(Palr.pulseChargeStart).a(0.15f);
            lightRadius = 8;
        }};

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

        classemStolnene = new Floor("classem-stolnene"){{
            speedMultiplier = 0.85f;
            variants = 3;
            emitLight = true;
            lightColor = new Color(Palr.pulseChargeStart).a(0.05f);
            lightRadius = 4;
            attributes.set(Attribute.water, 0.65f);
            attributes.set(Attribute.heat, -0.15f);
        }};

        classemPathen = new Floor("classem-pathen"){{
            speedMultiplier = 0.85f;
            variants = 2;
            emitLight = true;
            lightColor = new Color(Palr.pulseChargeStart).a(0.25f);
            lightRadius = 7;
            attributes.set(Attribute.water, 1.15f);
            attributes.set(Attribute.heat, -0.35f);
        }};

        paileanWallen = new StaticWall("pailean-wallen"){{
            variants = 2;
        }};

        classemWallen = new StaticWall("classem-wallen"){{
            variants = 2;
        }};

        melonaleum = new FixedOreBlock("melonaleum"){{
            itemDrop = RustingItems.melonaleum;
            overrideMapColor = itemDrop.color;
            variants = 2;
        }};

        //endregion

        //region pulse

        //Generates pulse. Requires some sort of Siphon to collect the pulse.
        pulseCollector = new PulseGenerator("pulse-collector"){{
            requirements(Category.power, with(Items.copper, 35, Items.coal, 15, Items.titanium, 10));
            centerResearchRequirements = with(Items.copper, 100,  Items.coal, 50, Items.titanium, 25);
            size = 1;
            canOverload = false;
            configurable = false;
            productionTime = 30;
            pulseAmount = 2.5f;
            connectionsPotential = 0;
            connectable = false;
            pulseStorage = 15;
            resistance = 0.75;
            laserOffset = 4;
        }};

        //Generates pulse. Quite good at storing pulse, but requires additional fuel.
        pulseGenerator = new PulseGenerator("pulse-generator"){{
            requirements(Category.power, with(Items.copper, 90, Items.silicon, 55, Items.titanium, 45));
            centerResearchRequirements = with(Items.copper, 350,  Items.coal, 95, Items.graphite, 55, Items.titanium, 225, RustingItems.melonaleum, 75);
            consumes.item(RustingItems.melonaleum, 1);
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
            laserOffset = 10;
            laserRange = 7;
        }};

        //Loses power fast, but is great at transmitting pulses to far blocks.
        pulseNode = new PulseNode("pulse-node"){{
            requirements(Category.power, with(Items.copper, 5, Items.lead, 4, Items.titanium, 3));
            centerResearchRequirements = with(Items.copper, 120, Items.lead, 95, Items.titanium, 65);
            size = 1;
            alwaysUnlocked = true;
            powerLoss = 0.0025f;
            pulseReloadTime = 15;
            energyTransmission = 3f;
            pulseStorage = 25;
            resistance = 0.075;
            laserRange = 13;
            canOverload = false;
        }};

        //Shoots lightning around itself when overloaded. Easly overloads. Acts as a large power node, with two connections, but slower reload
        pulseTesla = new PulseNode("pulse-tesla"){{
            requirements(Category.power, with(Items.copper, 85, Items.lead, 65, Items.graphite, 25, Items.titanium, 20));
            centerResearchRequirements = with(Items.copper, 365, Items.lead, 125, Items.coal, 85, Items.titanium, 80);
            size = 2;
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
            canOverload = true;
        }};

        //stores power for later usage less effectively than nodes, but stores more power. Transmits power to blocks nearby with less pulse power percentage.
        pulseResonator = new ConductivePulseBlock("pulse-resonator"){{
            requirements(Category.power, with(Items.copper, 35, Items.silicon, 20, Items.titanium, 10));
            centerResearchRequirements = with(Items.copper, 175, Items.coal, 35, Items.silicon, 90, Items.titanium, 65);
            size = 1;
            powerLoss = 0.00425f;
            resistance = 0;
            pulseStorage = 175;
            canOverload = false;
        }};

        pulseSiphon = new PulseSiphon("pulse-siphon"){{
            requirements(Category.power, with(Items.copper, 10, Items.silicon, 20, Items.titanium, 15));
            centerResearchRequirements = with(Items.copper, 125,  Items.coal, 65, Items.graphite, 45, Items.titanium, 35);
            size = 1;
            powerLoss = 0.000035f;
            siphonAmount = 1.5f;
            pulseReloadTime = 35;
            pulseStorage = 35;
            laserRange = 6;
            canOverload = false;
        }};

        pulseBarrier = new PulseBlock("pulse-barrier"){{
            requirements(Category.defense, with(Items.copper, 8, Items.graphite, 6, Items.titanium, 5));
            centerResearchRequirements = with(Items.copper, 115, Items.coal, 65, Items.titanium, 30);
            size = 1;
            health = 410 * size * size;
            powerLoss = 0.000035f;
            pulseStorage = 35;
            canOverload = false;
        }};

        pulseBarrierLarge = new PulseBlock("pulse-barrier-large"){{
            requirements(Category.defense, with(Items.copper, 32, Items.graphite, 24, Items.titanium, 20));
            centerResearchRequirements = with(Items.copper, 450, Items.graphite, 75, Items.titanium, 120);
            size = 2;
            health = 410 * size * size;
            powerLoss = 0.000035f;
            pulseStorage = 35;
            canOverload = false;
        }};

        pulseResearchCenter = new PulseResearchBlock("pulse-research-center"){{
            requirements(Category.effect, with(Items.copper, 65, Items.lead, 50, Items.coal, 25));
            centerResearchRequirements = with(Items.copper, 40,  Items.coal, 15);
            size = 2;
            alwaysUnlocked = true;
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
            requirements(Category.effect, with(Items.copper, 95, Items.lead, 75, Items.silicon, 45, Items.titanium, 25));
            centerResearchRequirements = with(Items.copper, 550,  Items.coal, 355, Items.metaglass, 100, Items.graphite, 125, Items.titanium, 175, RustingItems.melonaleum, 75);
            size = 2;
            powerLoss = 0.0000155f;
            minRequiredPulsePercent = 0.5f;
            pulseReloadTime = 165;
            connectionsPotential = 4;
            energyTransmission = 0.5f;
            pulseStorage = 70;
            overloadCapacity = 30;
            laserRange = 10;
            laserOffset = 9;
            healingPercentCap = 13;
            healPercent = 26;
            healPercentFalloff = healPercent/3;
            overdrivePercent = 65;
        }};

        archangel = new DysfunctionalMonolith("archangel"){{
            requirements(Category.effect, with(Items.copper, 300, Items.lead, 115, Items.metaglass, 50, Items.titanium, 45));
            centerResearchRequirements = with(Items.copper, 350,  Items.coal, 95, Items.graphite, 55, Items.titanium, 225);
            size = 3;
            health = 135 * size * size;
            projectile = RustingBullets.craeWeaver;
            projectileChanceModifier = 0;
            reloadTime = 85;
            shots = 2;
            bursts = 3;
            burstSpacing = 3;
            inaccuracy = 5;
            customConsumes.pulse = 10;
            cruxInfiniteConsume = true;
            pulseStorage = 70;
            overloadCapacity = 30;
            powerLoss = 0;
            minRequiredPulsePercent = 0;
            canOverload = true;
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
