package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.FontLoaders
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.RoundedUtil
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color


/**
 * @author LangYa
 * @ClassName TargetHUD
 * @date 2024/1/24 下午 11:21
 * @Version 1.0
 */

@ElementInfo("TargetHUD")
class TargetHUD : Element() {

    val blur = BoolValue("Blur",true)

    override fun drawElement(): Border {
        val mc = mc2

        GlStateManager.pushMatrix()
        GlStateManager.translate(10f, 15f, 0.0f)

        RoundedUtil.drawRound(
            10f,
            10f,
            60f + mc.fontRenderer.getStringWidth(mc.player.name),
            28f,
            5f,
            Color(0, 0, 0, 80)
        )
        RenderUtils.resetColor()
        RoundedUtil.drawRound(40f, 30f, mc.player.health * 3f, 3f, 1f,Color.WHITE)
        RenderUtils.resetColor()

        // draw head
        drawBigHead(13.5f, 12.5f, 23.0f, 23.0f, mc.player)

        // draw string
        FontLoaders.F18.drawString(mc.player.name, 47f, 16.0f, -1)

        GlStateManager.resetColor()
        GlStateManager.enableAlpha()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
        return Border(10f,10f,60f + mc.fontRenderer.getStringWidth(mc.player.name),28f)
    }

    fun drawBigHead(x: Float, y: Float, width: Float, height: Float, player: AbstractClientPlayer) {
        val offset = -(player.hurtTime * 23).toDouble()
        RenderUtils.glColor(
            Color(
                255,
                (255.0 + offset).toInt(),
                (255.0 + offset).toInt()
            ).rgb
        )
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)
        mc2.textureManager.bindTexture(player.locationSkin)
        Gui.drawScaledCustomSizeModalRect(
            x.toInt(),
            y.toInt(),
            8.0f,
            8.0f,
            8,
            8,
            width.toInt(),
            height.toInt(),
            64.0f,
            64.0f
        )
        GlStateManager.disableBlend()
        GlStateManager.resetColor()
    }

}