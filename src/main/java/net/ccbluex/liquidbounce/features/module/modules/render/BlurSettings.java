/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.BlurEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.BloomUtil;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.blur.GaussianBlur;
import net.ccbluex.liquidbounce.utils.render.blur.KawaseBlur;
import net.ccbluex.liquidbounce.utils.render.blur.StencilUtil;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.shader.Framebuffer;

@ModuleInfo(name = "BlurSettings", description = "Shader effect.", category = ModuleCategory.RENDER)
public class BlurSettings extends Module {

    private final IntegerValue iterations = new IntegerValue("BlurIterations", 4, 1, 15);
    private final IntegerValue offset = new IntegerValue("BlurOffset", 3, 1, 20);
    public BoolValue blurValue = new BoolValue("Blur", false);
    public ListValue blurMode = new ListValue("BlurMode", new String[]{"Kawase", "Gaussian"}, "Gaussian");
    public IntegerValue blurRadius = new IntegerValue("BlurRadius", 6, 1, 60);
    public BoolValue shadowValue = new BoolValue("ShadowOn", false);
    public IntegerValue shadowRadius = new IntegerValue("ShadowRadius", 6, 1, 60);
    public IntegerValue shadowOffset = new IntegerValue("ShadowOffset", 2, 1, 15);

    public Framebuffer bloomFramebuffer = new Framebuffer(1, 1, false);


    public void drawBlurShadow() {

        if (blurValue.get()) {
            StencilUtil.initStencilToWrite();
            LiquidBounce.eventManager.callEvent(new BlurEvent(false));
            StencilUtil.readStencilBuffer(1);

            switch (blurMode.get()) {
                case "Gaussian":
                    GaussianBlur.renderBlur(blurRadius.getValue().floatValue());
                    break;
                case "Kawase":
                    KawaseBlur.renderBlur(iterations.getValue(), offset.getValue());
                    break;
            }
            StencilUtil.uninitStencilBuffer();
        }

        if (this.shadowValue.get()) {
            bloomFramebuffer = RenderUtils.createFrameBuffer(bloomFramebuffer);

            bloomFramebuffer.framebufferClear();
            bloomFramebuffer.bindFramebuffer(true);
            LiquidBounce.eventManager.callEvent(new BlurEvent(true));

            bloomFramebuffer.unbindFramebuffer();

            BloomUtil.renderBlur(bloomFramebuffer.framebufferTexture, shadowRadius.get(), shadowOffset.get());
        }
    }
}
