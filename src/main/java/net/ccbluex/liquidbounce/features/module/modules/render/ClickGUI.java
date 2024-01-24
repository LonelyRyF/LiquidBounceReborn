package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.AstolfoStyle;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.LiquidBounceStyle;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.NullStyle;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.SlowlyStyle;
import net.ccbluex.liquidbounce.ui.client.newdropdown.DropdownClickGui;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleInfo(name = "ClickGUI", description = "Opens the ClickGUI.", category = ModuleCategory.RENDER, keyBind = Keyboard.KEY_RSHIFT, canEnable = false)
public class ClickGUI extends Module {
    private final ListValue styleValue = new ListValue("Style", new String[] {"LiquidBounce", "Null", "Slowly", "Astolfo"}, "Slowly") {
        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            updateStyle();
        }

    };
    private final ListValue clickguimodeValue = new ListValue("Mode", new String[] {"LiquidBounce", "Tenacity", "Temple"}, "Tenacity");
    public static final ListValue colormode = new ListValue("Setting Accent", new String[]{"White", "Color"},"Color");
    public static final ListValue scrollMode = new ListValue("Scroll Mode", new String[]{"Screen Height", "Value"},"Value");
    public static final IntegerValue clickHeight = new IntegerValue("Tab Height", 250, 100, 500);
    public final FloatValue scaleValue = new FloatValue("Scale", 1F, 0.7F, 2F);
    public final IntegerValue maxElementsValue = new IntegerValue("MaxElements", 15, 1, 20);


    private static final IntegerValue colorRedValue = new IntegerValue("R", 0, 0, 255);
    private static final IntegerValue colorGreenValue = new IntegerValue("G", 160, 0, 255);
    private static final IntegerValue colorBlueValue = new IntegerValue("B", 255, 0, 255);
    private static final BoolValue colorRainbow = new BoolValue("Rainbow", false);
    public static final BoolValue backback = new BoolValue("Background Accent",true);
    public static Color generateColor() {
        return colorRainbow.get() ? ColorUtils.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
    }
    public static int generateRGB() {
        return colorRainbow.get() ? ColorUtils.rainbow().getRGB() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()).getRGB();
    }

    @Override
    public void onEnable() {
        if (clickguimodeValue.get().equalsIgnoreCase("LiquidBounce")) {
            updateStyle();
            mc.displayGuiScreen(classProvider.wrapGuiScreen(LiquidBounce.clickGui));
        }
        if (clickguimodeValue.get().equalsIgnoreCase("Tenacity")) {
            mc.displayGuiScreen(classProvider.wrapGuiScreen(new DropdownClickGui()));
        }


    }

    private void updateStyle() {
        switch(styleValue.get().toLowerCase()) {
            case "liquidbounce":
                LiquidBounce.clickGui.style = new LiquidBounceStyle();
                break;
            case "null":
                LiquidBounce.clickGui.style = new NullStyle();
                break;
            case "slowly":
                LiquidBounce.clickGui.style = new SlowlyStyle();
                break;
            case "astolfo":
                LiquidBounce.clickGui.style = new AstolfoStyle();
                break;
        }
    }

    @EventTarget(ignoreCondition = true)
    public void onPacket(final PacketEvent event) {
        final IPacket packet = event.getPacket();

        if (classProvider.isSPacketCloseWindow(packet) && classProvider.isClickGui(mc.getCurrentScreen())) {
            event.cancelEvent();
        }
    }
}
