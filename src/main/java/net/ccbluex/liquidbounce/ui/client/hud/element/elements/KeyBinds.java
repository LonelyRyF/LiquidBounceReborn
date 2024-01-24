package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import cn.liying.utils.animation.AnimationUtils;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.ui.client.hud.element.Border;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.RoundedUtil;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

@ElementInfo(name = "KeyBinds")
public class KeyBinds extends Element {
    public static ArrayList<Module> bind = new ArrayList<Module>();
    float GameInfoRows = 0;
    int y2 = 8;
    float width = 110f;
    @Override
    public Border drawElement() {
        bind = LiquidBounce.moduleManager.getModules().stream().filter(module -> module.getKeyBind() != 0 && module.getState()).collect(Collectors.toCollection(ArrayList::new));


        RoundedUtil.drawRound(0F,-2f,width,y2, 0f,new Color(0,0,0,40));
        RenderUtils.resetColor();
        Fonts.font35.drawStringWithShadow("KeyBinds", (int) (width/2-18f), 0,Color.WHITE.getRGB());

        y2 = (int) AnimationUtils.smoothAnimation(y2,(bind.size() * 10) + 10,60,0.3f);


        for (int i = 0; i < bind.size(); i++) {
            Module m = bind.get(i);
            Fonts.font35.drawStringWithShadow(m.getName(), 4, (int) m.getKeyBindY(), new Color(255,255,255,255).getRGB());
            Fonts.font35.drawStringWithShadow(Keyboard.getKeyName(m.getKeyBind()), (int) 100f, (int) m.getKeyBindY(),new Color(255,255,255,255).getRGB());
            m.setKeyBindY((int) AnimationUtils.smoothAnimation((int) m.getKeyBindY(),10 + (i * 10),60,0.3f));
        }

        return new Border(0F, this.GameInfoRows * 18F + 12F, 176F, 80F);
    }
}
