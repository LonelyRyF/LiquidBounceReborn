/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import cn.liying.utils.SuperLib;
import cn.liying.utils.animation.AnimationUtil;
import net.ccbluex.liquidbounce.ui.font.FontDrawer;
import net.ccbluex.liquidbounce.ui.font.FontLoaders;

import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;


import java.awt.*;

@Mixin(GuiButton.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiButton extends Gui {

    @Shadow
    @Final
    protected static ResourceLocation BUTTON_TEXTURES;
    @Shadow
    public boolean visible;
    @Shadow
    public int x;
    @Shadow
    public int y;
    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    public boolean enabled;
    @Shadow
    public String displayString;
    @Shadow
    protected boolean hovered;
    private float cut;
    private float alpha;
    private double animation= 0.10000000149011612;
    @Shadow
    protected abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);

    /**
     * @author CCBlueX
     */
    @Overwrite
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            final FontDrawer fr = FontLoaders.F18;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.enableBlend();
            this.animation = AnimationUtil.moveUD((float) this.animation, (this.hovered ? 0.3F : 0.1F), 10f / Minecraft.getDebugFPS(),4f / Minecraft.getDebugFPS());
            if (this.enabled) {
                RenderUtils.drawGradientSideways((float)this.x, (float)(this.y + this.height) - 1.5F, (float)(this.x + this.width), (float)(this.y + this.height), SuperLib.reAlpha(new Color(90, 184, 255).getRGB(), 0.95F),SuperLib.reAlpha(new Color(90, 184, 255).getRGB(),0.95F));
            }else {
                RenderUtils.drawGradientSideways((float)this.x, (float)(this.y + this.height) - 1.5F, (float)(this.x + this.width), (float)(this.y + this.height), SuperLib.reAlpha(new Color(90, 184, 255).getRGB(), 0.95F),SuperLib.reAlpha(new Color(90, 184, 255).getRGB(),0.95F));
            }
            RenderUtils.drawRect((float)this.x, (float)this.y, (float)(this.x + this.width), (float)(this.y + this.height) - 1.5F, new Color(0,0,0,180).getRGB());
            if (this.enabled) {
                RenderUtils.drawRect((float)this.x, (float)this.y, (float)(this.x + this.width), (float)(this.y + this.height), SuperLib.reAlpha(new Color(225, 225, 225).getRGB(), (float)this.animation));
            } else {
                RenderUtils.drawRect((float)this.x, (float)this.y, (float)(this.x + this.width), (float)(this.y + this.height), SuperLib.reAlpha(new Color(255, 255, 255).getRGB(), 0.1F));
            }

            this.mouseDragged(mc, mouseX, mouseY);
            fr.drawStringWithShadow(displayString,
                    (float) ((this.x + this.width / 2) -
                            fr.getStringWidth(displayString) / 2),
                    this.y + (this.height - 5) / 2F - 1, Color.WHITE.getRGB());
        }
    }
}