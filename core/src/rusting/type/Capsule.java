package rusting.type;

import arc.util.Log;
import mindustry.ctype.*;
import rusting.ctype.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Capsule<itemStack, liquidStack> extends UnlockableContent {
    public itemStack itemStore;
    public liquidStack liquidStore;
    //durability of the capsule
    public int durability = 100;
    //Insulation of the capsule, used for handling hot liquids 0 means it's heat conductive, and leaks heat everywhere, 1 means that it retains all heat
    public int insulation = 0;
    //Resistance to heat. Used in game, only a stat outside of in game usage.
    public int heatResistance = 0;

    public enum changeme
    {
        value1,
        value2;
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


    protected static Field getEnumsArrayField(Class<?> ec) throws Exception {
        Field field = ec.getDeclaredField("ENUM$VALUES");
        field.setAccessible(true);
        return field;
    }

    protected static void clearFieldAccessors(Field field) throws ReflectiveOperationException {
        Field fa = Field.class.getDeclaredField("fieldAccessor");
        fa.setAccessible(true);
        fa.set(field, null);

        Field ofa = Field.class.getDeclaredField("overrideFieldAccessor");
        ofa.setAccessible(true);
        ofa.set(field, null);

        Field rf = Field.class.getDeclaredField("root");
        rf.setAccessible(true);
        Field root = (Field) rf.get(field);
        if (root != null) {
            clearFieldAccessors(root);
        }
    }

    protected static <E extends Enum<E>> void setEnumsArray(Class<E> ec, E... e) throws Exception {
        Field field = ec.getDeclaredField("ENUM$VALUES");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        field.setAccessible(true);
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(ec, e);
    }

    public static void getContent() throws Exception {

        getEnumsArrayField(changeme.class).get(null);
        clearFieldAccessors(getEnumsArrayField(changeme.class));
        setEnumsArray(changeme.class, changeme.value2);

        Field[] declaredFields = changeme.class.getDeclaredFields();
        for (Field field : declaredFields) {
            Log.err(field.getName() + ": " + field.getType());
        }
    }
}