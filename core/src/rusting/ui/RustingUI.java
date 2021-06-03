package rusting.ui;

import arc.ApplicationListener;
import arc.assets.Loadable;
import rusting.ui.dialog.research.*;

public class RustingUI implements ApplicationListener, Loadable {
    public UnlockDialog unlock;
    public BlockEntryDialog blockEntry;
    public FieldBlockListDialog blocklist;

    @Override
    public void init(){
        unlock = new UnlockDialog();
        blockEntry = new BlockEntryDialog();
        blocklist = new FieldBlockListDialog();
    }
}
