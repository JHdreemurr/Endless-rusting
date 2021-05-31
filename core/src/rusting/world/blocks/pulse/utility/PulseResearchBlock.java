package rusting.world.blocks.pulse.utility;

import arc.Core;
import arc.graphics.Color;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.Tooltip;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.ui.Cicon;
import mindustry.ui.Fonts;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import mindustry.world.meta.*;
import rusting.graphics.ResearchCenterUI;
import rusting.world.blocks.pulse.PulseBlock;

import static mindustry.Vars.*;

public class PulseResearchBlock extends PulseBlock {


    public int threshold = 2;
    public Seq<Block> blocks = new Seq();
    public Seq<String> fieldNames = new Seq();
    public BaseDialog dialog, blockDialog, unlockDialog;
    public boolean dialogSetup = false;
    public Seq<String> randomQuotes = new Seq();
    public Seq<String> databaseQuotes = new Seq();
    public TextureRegionDrawable unlockIcon, blockHolderIcon;
    private Image unlockImage, blockHolderImage;
    private ResearchBulletType researchBullet = new ResearchBulletType();

    public PulseResearchBlock(String name) {
        super(name);
        configurable = true;
        needsResearching = false;
        destructible = false;

        config(String.class, (PulseResearchBuild entity, String contentName) -> {
            if(!entity.researchedBlocks.contains(contentName)){
                entity.researchedBlocks.add(contentName);
            }
        });

    }

    @Override
    public void load(){
        super.load();

        dialog = new BaseDialog(Core.bundle.get("erui.pulseblockdatabasepage"));
        dialog.addCloseButton();

        blockDialog = new BaseDialog(Core.bundle.get("erui.pulseblockpage"));
        blockDialog.addCloseButton();

        unlockDialog = new BaseDialog(Core.bundle.get("erui.unlockquestion"));

        unlockIcon = new TextureRegionDrawable().set(Core.atlas.find(name + "-charged"));
        blockHolderIcon = new TextureRegionDrawable().set(Core.atlas.find(name + "-blockholder"));

        //Unchanged, so it can be defined in load
        blockHolderImage = new Image(blockHolderIcon);
        blockHolderImage.setSize(8 * 36);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        if(getCenterTeam(player.team()) != null){
            drawPlaceText(Core.bundle.get("bar.centeralreadybuilt"), x, y, valid);
        }
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team){
        //must have been researched, but for now checks if research center exists
        if(getCenterTeam(team) != null) return false;
        return super.canPlaceOn(tile, team);
    }

    public void loadBlockList() {
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

    public void showDialog(){

        dialog.show();
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

            if(content instanceof PulseBlock) {
                ResearchCenterUI.displayCustomStats(table, (PulseBlock) content, randomQuotes);
            }

        });

        blockDialog.show();
    }

    public void showUnlockDialog(UnlockableContent content, Tile tile){

        unlockDialog.clear();
        unlockDialog.cont.margin(30);
        unlockDialog.addCloseButton();
        unlockIcon.set(content.icon(Cicon.tiny));
        unlockImage = new Image(unlockIcon).setScaling(Scaling.fit);
        ItemStack[] rCost = ((PulseBlock) content).centerResearchRequirements;
        Table itemsCost = new Table();
        itemsCost.table(table -> {
            for(ItemStack costing: rCost) {
                Image itemImage = new Image(new TextureRegionDrawable().set(costing.item.icon(Cicon.medium))).setScaling(Scaling.fit);

                table.stack(
                        itemImage,
                        new Table(t -> {
                            t.add(costing.amount + "/" + Math.min(tile.build.team.core().items.get(costing.item), costing.amount));
                        }).left().margin(1, 3, 2, 0)
                ).pad(10f);
            }
        });
        unlockDialog.table(table -> {
            table.center();
            table.right();
            table.button("Unlock?", () -> {
                if(tile.build instanceof PulseResearchBuild && content instanceof PulseBlock){
                    PulseResearchBuild building = (PulseResearchBuild) tile.build;
                    CoreBuild coreBlock = building.team.core();
                    boolean canResearch = false;
                    if(Vars.state.rules.infiniteResources || coreBlock.items.has(rCost, 1)){
                        for(int i = 0; i < ((PulseBlock) content).centerResearchRequirements.length; i++){
                            coreBlock.items.remove(((PulseBlock) content).centerResearchRequirements[i]);
                        }
                        building.configure(content.localizedName);
                        Sounds.unlock.at(player.x, player.y);
                    }
                }
                try {
                    dialog.hide();
                    makelist(tile);
                }
                catch (NoSuchFieldException err){
                    Log.err(err);
                }
                finally {
                    dialog.show();
                }
                unlockDialog.hide();
            }).fillX().left().height(75f).width(145).pad(120).padBottom(264);
            table.add(itemsCost).height(75f).width(145).pad(15).padBottom(230);
            table.stack(unlockImage/*, blockHolderImage*/).size(8 * 12).fillX().left().pad(90).padBottom(264);
        });

        unlockDialog.show();
    }

    public void makelist(Tile tile) throws NoSuchFieldException{

        loadBlockList();

        dialog.cont.reset();
        dialog.cont.margin(20);
        dialog.cont.pane(table -> {

            table.add(databaseQuotes.random()).growX().left().color(chargeColourEnd);
            table.row();
            table.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
            table.row();

                table.table(list -> {

                    int cols = Mathf.clamp((Core.graphics.getWidth() - 30) / (32 + 10), 1, 5);
                    final int[] count = {0};

                    list.left();

                    blocks.each(type -> {

                    UnlockableContent unlock = type;
                    if(!unlocked(unlock)) return;
                    boolean isResearched = false;
                    if(tile.build instanceof PulseResearchBuild){
                        isResearched = researched(unlock, (PulseResearchBuild) tile.build);
                    }
                    else isResearched = researched(unlock, player.team());
                    Image image = new Image(unlock.icon(Cicon.medium)).setScaling(Scaling.fit);
                    Color imageCol = isResearched ? Color.white : Pal.darkerGray;
                    list.add(image).size(8 * 12).pad(3);
                    ClickListener listener = new ClickListener();
                    image.addListener(listener);
                    if (!mobile) {
                        image.addListener(new HandCursorListener());
                        image.update(() -> image.color.lerp(!listener.isOver() ? imageCol : chargeColourEnd, Mathf.clamp(0.4f * Time.delta)));
                    }

                        boolean finalIsResearched = isResearched;
                        image.clicked(() -> {
                        if(unlocked(unlock)){
                            if (Core.input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(unlock.name) != 0) {
                                Core.app.setClipboardText((char) Fonts.getUnicode(unlock.name) + "");
                                ui.showInfoFade("@copied");
                            }

                            else if(finalIsResearched) showBlockEntry(unlock);

                            else showUnlockDialog(unlock, tile);
                        }
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
        dialogSetup = true;
    }

    private boolean unlocked(UnlockableContent content){
        return (!Vars.state.isCampaign() && !Vars.state.isMenu()) || content.unlocked();
    }

    public static boolean researched(UnlockableContent content, Team team){
            return getCenterTeam(team) != null && getCenterTeam(team).researchedBlocks.contains(content.localizedName) || state.rules.infiniteResources;
    }

    public static boolean researched(UnlockableContent content, PulseResearchBuild building){
        boolean returnBool = false;
        returnBool = building.researchedBlocks.contains(content.localizedName) || state.rules.infiniteResources;
        return returnBool;
    }

    public void buildDialog(Tile tile){
        Vars.control.input.frag.config.hideConfig();
        if(!(tile.build instanceof PulseResearchBuild)) return;
        try{
            makelist(tile);
        }
        catch (NoSuchFieldException e){
            Log.info(e);
        }
        finally {
            showDialog();
        }
    }

    public class ResearchBulletType extends BulletType{
        @Override
        public Bullet create(Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data) {
            Building build = world.buildWorld(x, y);
            if(build instanceof PulseResearchBuild && data instanceof String) ((PulseResearchBuild) build).researchedBlocks.add((String) data);
            return Bullet.create();
        }
    }

    public class PulseResearchBuild extends PulseBlockBuild{
        public Seq<String> researchedBlocks = new Seq<>();

        public void buildConfiguration(Table table){
            super.buildConfiguration(table);
            table.button(Icon.pencil, () -> {
                buildDialog(tile);
            }).size(40f);
            table.button(Icon.eraser, () -> {
                buildDialog(tile);
            }).size(40f);
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.d(researchedBlocks.size);
            researchedBlocks.each(block -> {
                w.str(block);
            });
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            //might mess up any classes which extend off this, only keep temporarily or have one block
            double index = r.d();
            for(int i = 0; i < index; i++){
                configure(r.str());
            }
        }
    }
}
