package net.ccbluex.liquidbounce.ui.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public abstract class FontLoaders {
    public static FontDrawer F18;
    public static FontDrawer F15;
    public static FontDrawer F16
            ;

    public static void initFonts() {
        F15 = getFont("misans", 15, true);
        F16 = getFont("misans", 16, true);
        F18 = getFont("misans", 18, true);
    }

    public static FontDrawer getFont(String name, int size, boolean antiAliasing) {
        Font font;
        try {
            font = Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("furrysense/font/" + name + ".ttf")).getInputStream()).deriveFont(Font.PLAIN, (float) size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return new FontDrawer(font, antiAliasing);
    }
}
