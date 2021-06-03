package rusting.ui.dialog.research;

import arc.Core;
import arc.graphics.Color;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.Image;
import arc.scene.ui.Tooltip;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Cicon;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.Tile;
import rusting.Varsr;
import rusting.content.Palr;
import rusting.ui.dialog.CustomBaseDialog;
import rusting.world.blocks.pulse.utility.PulseResearchBlock;

import static mindustry.Vars.mobile;
import static mindustry.Vars.player;

public class FieldBlockListDialog extends CustomBaseDialog {

    public Seq<Block> blocks = new Seq<Block>();
    public Seq<String> databaseQuotes = new Seq<String>();

    public FieldBlockListDialog(){
        super(Core.bundle.get("erui.pulseblockdatabasepage"), Core.scene.getStyle(DialogStyle.class));
        addCloseButton();
    }

    public void makeList(Tile tile){
        if(tile.build instanceof Building && tile.build.block instanceof PulseResearchBlock) makeList(((PulseResearchBlock) tile.build.block).fieldNames, ((PulseResearchBlock) tile.build.block).threshold);
    }

    public void makeList(Seq<String> fieldNames, int threshold) {
        blocks.clear();
        Vars.content.blocks().each(b -> {
            int fields = 0;
            for (String field : fieldNames) {
                try {
                    b.getClass().getField(field);
                    fields++;
                } catch (NoSuchFieldException ignored) {}
            }

            if (fields >= 1 && fields >= threshold) blocks.add(b);
        });
    }

    public void show(Tile tile) {
        makeList(tile);
        setup();
        super.show();
    }

    public void setup(){

        Log.info("setup thing");

        cont.reset();
        cont.margin(20);
        cont.pane(table -> {

            table.add(databaseQuotes.random()).growX().left().color(Palr.pulseChargeEnd);
            table.row();
            table.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
            table.row();

            table.table(list -> {

                int cols = Mathf.clamp((Core.graphics.getWidth() - 30) / (32 + 10), 1, 5);
                final int[] count = {0};

                list.left();

                blocks.each(type -> {

                    UnlockableContent unlock = (UnlockableContent) type;
                    if(!unlocked(unlock)) return;
                    final boolean isResearched = PulseResearchBlock.researched(unlock, player.team());
                    Image image = new Image(unlock.icon(Cicon.medium)).setScaling(Scaling.fit);
                    Color imageCol = isResearched ? Color.white : Pal.darkerGray;
                    list.add(image).size(8 * 12).pad(3);
                    ClickListener listener = new ClickListener();
                    image.addListener(listener);
                    if (!mobile) {
                        image.addListener(new HandCursorListener());
                        image.update(() -> image.color.lerp(!listener.isOver() ? imageCol : Palr.pulseChargeEnd, Mathf.clamp(0.4f * Time.delta)));
                    }

                    boolean finalIsResearched = isResearched;
                    image.clicked(() -> {
                        if(isResearched){
                            if (Core.input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(unlock.name) != 0) {
                                Core.app.setClipboardText((char) Fonts.getUnicode(unlock.name) + "");
                                Vars.ui.showInfoFade("@copied");
                            }

                            else Varsr.ui.blockEntry.show(unlock);
                        }

                        else if(unlocked(unlock)) Varsr.ui.unlock.show(unlock);
                    });
                    boolean finalIsResearched1 = isResearched;
                    image.addListener(new Tooltip(t -> t.background(Tex.button).add((finalIsResearched1 ? "The " : "Unlock the ") + unlock.localizedName + (finalIsResearched1 ? "" : "?"))));

                    if ((++count[0]) % cols == 0) {
                        list.row();
                    }
                });
            }).growX().left().padBottom(10);
            table.row();
        });
    }

    private boolean unlocked(UnlockableContent content){
        return (!Vars.state.isCampaign() && !Vars.state.isMenu()) || content.unlocked();
    }

}
