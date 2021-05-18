package rusting.world.blocks.pulse;

import arc.Core;
import arc.graphics.Color;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.Image;
import arc.scene.ui.Tooltip;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Scaling;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Cicon;
import mindustry.ui.Fonts;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.Stats;
import rusting.graphics.ResearchCenterUI;

import static mindustry.Vars.*;

public class PulseResearchBlock extends PulseBlock{

    public Seq<Block> blocks = new Seq();
    public int threshold = 2;
    public Seq<String> fieldNames = new Seq();
    public BaseDialog dialog;
    public BaseDialog blockDialog;
    public boolean dialogSetup = false;
    public Seq<String> randomQuotes = new Seq();
    public Seq<String> databaseQuotes = new Seq();

    public PulseResearchBlock(String name) {
        super(name);
        configurable = true;
    }

    @Override
    public void load(){
        super.load();
        dialog = new BaseDialog(Core.bundle.get("erui.pulseblockdatabasepage"));
        dialog.addCloseButton();

        blockDialog = new BaseDialog(Core.bundle.get("erui.pulseblockpage"));
        blockDialog.addCloseButton();
    }

    public void loadBlockList() {
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

    public void showBlockEntry(UnlockableContent content){
        blockDialog.cont.clear();

        blockDialog.cont.pane(table -> {

            table.margin(10);

            //initialize stats if they haven't been yet
            content.checkStats();

            table.table(title1 -> {
                Cicon size = content.prefDatabaseIcon();

                title1.image(content.icon(size)).size(size.size).scaling(Scaling.fit);
                title1.add("[accent]" + content.localizedName).padLeft(5);
            });

            table.row();

            if(content.description != null){
                boolean any = content.stats.toMap().size > 0;

                if(any){
                    table.add("@category.purpose").color(Pal.accent).fillX().padTop(10);
                    table.row();
                }

                table.add("[lightgray]" + content.displayDescription()).wrap().fillX().padLeft(any ? 10 : 0).width(500f).padTop(any ? 0 : 10).left();
                table.row();

                if(!content.stats.useCategories && any){
                    table.add("@category.general").fillX().color(Pal.accent);
                    table.row();
                }
            }

            Stats stats = content.stats;

            for(StatCat cat : stats.toMap().keys()){
                OrderedMap<Stat, Seq<StatValue>> map = stats.toMap().get(cat);

                if(map.size == 0) continue;

                //TODO check
                if(stats.useCategories){
                    table.add("@category." + cat.name()).color(Pal.accent).fillX();
                    table.row();
                }

                for(Stat stat : map.keys()){
                    table.table(inset -> {
                        inset.left();
                        inset.add("[lightgray]" + stat.localized() + ":[] ").left();
                        Seq<StatValue> arr = map.get(stat);
                        for(StatValue value : arr){
                            value.display(inset);
                            inset.add().size(10f);
                        }

                    }).fillX().padLeft(10);
                    table.row();
                }
            }

            if(content.details != null){
                table.add("[gray]" + content.details).pad(6).padTop(20).width(400f).wrap().fillX();
                table.row();
            }

            if(content instanceof PulseBlock) ResearchCenterUI.displayCustomStats(table, (PulseBlock) content, randomQuotes);

        });

        blockDialog.show();
    }

    public void makelist(Tile tile) throws NoSuchFieldException{

        loadBlockList();

        dialog.cont.clear();
        dialog.cont.margin(20);
        dialog.cont.pane(table -> {

            table.add(databaseQuotes.random()).growX().left().color(chargeColourEnd);
            table.row();
            table.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
            table.row();

                table.table(list -> {

                    int cols = Mathf.clamp((Core.graphics.getWidth() - 30) / (32 + 10), 1, 6);
                    final int[] count = {0};

                    list.left();

                    blocks.each(type -> {

                    UnlockableContent unlock = (UnlockableContent) type;

                    Image image = unlocked(unlock) ? new Image(unlock.icon(Cicon.medium)).setScaling(Scaling.fit) : new Image(Icon.lock, Pal.gray);
                    list.add(image).size(8 * 12).pad(3);
                    ClickListener listener = new ClickListener();
                    image.addListener(listener);
                    if (!mobile && unlocked(unlock)) {
                        image.addListener(new HandCursorListener());
                        image.update(() -> image.color.lerp(!listener.isOver() ? Color.white : chargeColourEnd, Mathf.clamp(0.4f * Time.delta)));
                    }

                    if (unlocked(unlock)) {
                        image.clicked(() -> {
                            if (Core.input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(unlock.name) != 0) {
                                Core.app.setClipboardText((char) Fonts.getUnicode(unlock.name) + "");
                                ui.showInfoFade("@copied");
                            } else {
                                showBlockEntry(unlock);
                            }
                        });
                        image.addListener(new Tooltip(t -> t.background(Tex.button).add(unlock.localizedName)));
                    }

                    if ((++count[0]) % cols == 0) {
                        list.row();
                    }
                });
            }).growX().left().padBottom(10);
            table.row();
        });
        dialogSetup = true;
    }

    private boolean unlocked(UnlockableContent content){
        return (!Vars.state.isCampaign() && !Vars.state.isMenu()) || content.unlocked();
    }

    public void buildDialog(Tile tile) throws Exception{
        Vars.control.input.frag.config.hideConfig();
        if(!(tile.build instanceof PulseResearchBuild)) return;
        try{
            if(!dialogSetup) makelist(tile);
        }
        catch (NoSuchFieldException e){
            Log.info(e);
        }
        finally {
            dialog.show();
        }
    }

    public class PulseResearchBuild extends PulseBlockBuild{

        public void buildConfiguration(Table table){
            super.buildConfiguration(table);
            table.button(Icon.pencil, () -> {
                try {
                    buildDialog(tile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).size(40f);
        }
    }
}
