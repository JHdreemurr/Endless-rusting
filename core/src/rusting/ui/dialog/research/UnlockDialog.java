package rusting.ui.dialog.research;

import arc.Core;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Sounds;
import mindustry.type.ItemStack;
import mindustry.ui.Cicon;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import rusting.Varsr;
import rusting.ui.dialog.CustomBaseDialog;
import rusting.world.blocks.pulse.PulseBlock;
import rusting.world.blocks.pulse.utility.PulseResearchBlock.PulseResearchBuild;

import static mindustry.Vars.player;

public class UnlockDialog extends CustomBaseDialog {

    public TextureRegionDrawable unlockIcon = new TextureRegionDrawable();
    public Image unlockImage = new Image();

    public UnlockDialog() {
        super(Core.bundle.get("erui.unlockpage"), Core.scene.getStyle(DialogStyle.class));
    }

    public void show(UnlockableContent content){

        Log.info("shwoing dialog fro searcing ree");

        clear();
        addCloseButton();
        Tile tile = PulseBlock.getCenterTeam(player.team()).tile;

        cont.margin(30);
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
        table(table -> {
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
                Varsr.ui.blocklist.hide();
                Varsr.ui.blocklist.makeList(tile);
                Varsr.ui.blocklist.show();
                hide();
            }).fillX().left().height(75f).width(145).pad(120).padBottom(264);
            table.add(itemsCost).height(75f).width(145).pad(15).padBottom(230);
            table.stack(unlockImage).size(8 * 12).fillX().left().pad(90).padBottom(264);
        });

        super.show();
    }
}
