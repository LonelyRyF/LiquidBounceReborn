package net.ccbluex.liquidbounce.features.module;

import net.ccbluex.liquidbounce.ui.client.newdropdown.utils.normal.Main;
import net.ccbluex.liquidbounce.ui.client.newdropdown.utils.objects.Drag;
import net.ccbluex.liquidbounce.ui.client.newdropdown.utils.render.Scroll;

public enum ModuleCategory {

    COMBAT("Combat"),
    PLAYER("Player"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    WORLD("World"),
    MISC("Misc"),
    EXPLOIT("Exploit"),
    HYT("Hyt");

    public final String namee;
    public final int posX;
    public final boolean expanded;

    private final Scroll scroll = new Scroll();

    public Scroll getScroll() {
        return scroll;
    }

    private final Drag drag;

    public Drag getDrag() {
        return drag;
    }

    public int posY = 20;

    public String getDisplayName(){
        return namee;
    }

    ModuleCategory(String name) {
        this.namee = name;
        posX = 40 + (Main.categoryCount * 120);
        drag = new Drag(posX, posY);
        expanded = true;
        Main.categoryCount++;
    }

}
