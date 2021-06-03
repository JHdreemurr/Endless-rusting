package rusting;

import arc.assets.Loadable;
import arc.struct.Seq;
import arc.util.Log;
import rusting.ui.RustingUI;

public class Varsr implements Loadable {

    public static Seq<String> defaultDatabaseQuotes, defaultRandomQuotes;


    public static RustingUI ui;

    public static void init(){

        ui = new RustingUI();

        //totally random ;)

        defaultDatabaseQuotes = Seq.with(
            "[cyan] Places of learning",
            "[cyan] Storages of information",
            "[cyan] Database Entries",
            "[#d8e2e0] Welcome back."
        );

        defaultRandomQuotes = Seq.with(
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
        Log.info("Loaded Varsf");
    }
}
