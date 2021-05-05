package rusting.type;

import mindustry.ctype.*;
import rusting.ctype.*;

public class Capsule<itemStack, liquidStack> extends UnlockableContent {
    public itemStack itemStore;
    public liquidStack liquidStore;
    //durability of the capsule
    public int durability = 100;
    //Insulation of the capsule, used for handling hot liquids 0 means it's heat conductive, and leaks heat everywhere, 1 means that it retains all heat
    public int insulation = 0;
    //Resistance to heat. Used in game, only a stat outside of in game usage.
    public int heatResistance = 0;

    public Capsule(String name) {
        super(name);
        this.itemStore = null;
        this.liquidStore = null;
    }

    public Capsule(String name, itemStack itemPayload, liquidStack liquidPayload) {
        super(name);
        this.itemStore = itemPayload;
        this.liquidStore = liquidPayload;
    }

    @Override
    public boolean isDisposed() {
        return super.isDisposed();
    }

    @Override
    public ContentType getContentType() {
        return null;
    }

    //@Override
    /*public ContentType getContentType(){
        return ERContentType.capsule;
    }*/
}