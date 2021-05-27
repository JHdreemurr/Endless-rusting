package rusting.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.graphics.Pal;
import rusting.content.RustingBlocks;
import rusting.world.blocks.pulse.PulseBlock;

public class ResearchCenterUI {

    public static String getQuote(PulseBlock content, Seq<String> randomQuotes){
        return content == RustingBlocks.pulseTesla ? randomQuotes.get(4) : randomQuotes.random();
    }

    public static void displayCustomStats(Table table, PulseBlock content, Seq<String> randomQuotes) {

        table.add(getQuote(content, randomQuotes)).pad(6).padTop(10).width(450f).wrap().fillX();
        table.row();

        table.add(Core.bundle.get("pulsecategory.enlightenedspace")).color(Color.sky.lerp(Pal.lancerLaser, 0.10f)).fillX();
        table.row();

        //iterates over all of the loadable StatGroups in the CustomStatHolder
        content.pStats.loadableGroups.each(l -> {
            //if the StatGroup can load it's stats continue
            if(l.canLoadStats()) {
                //Adds a header for the StatGroup
                table.add(Core.bundle.get("pulsecategory." + l.header())).color(Color.sky.lerp(Pal.lancerLaser, 0.10f)).fillX();
                table.row();

                Log.info(Core.bundle.get("pulsecategory." + l.header()));
                Log.info(l.header);

                //iterates over all stats for this StatGroup
                l.stats.each(s -> {
                    //If the stat has been assigned a value, add a Label to the table
                    if (s.hasValue()) formatTable(table, "pulsefield." + s.name, s.getValue(), "pulsefieldsufix." + s.suffix, 6, 3);
                });
            }
        });
    }

    private static void formatTable(Table table, String bundleID, Object stats, String sufixBundleID, int pad, int padTop) {
        table.add(
                String.format(
                        "%s: %s",
                        Core.bundle.get(bundleID),
                        stats
                ) + Core.bundle.get(sufixBundleID)
        ).pad(pad).padTop(padTop)
                .width(450f)
                .wrap()
                .fillX();
        table.row();
    }
}
