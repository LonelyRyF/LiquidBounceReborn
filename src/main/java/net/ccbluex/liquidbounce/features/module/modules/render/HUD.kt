package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.tenacity.ColorUtil
import net.ccbluex.liquidbounce.utils.render.tenacity.GradientUtil
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
import kotlin.math.hypot
import kotlin.math.roundToLong


@ModuleInfo(
    name = "HUD",
    description = "Toggles visibility of the HUD.",
    category = ModuleCategory.RENDER,
    array = false
)
class HUD : Module() {
    val fontChatValue = BoolValue("FontChat", false)
    val chatRect = BoolValue("ChatRect", false)
    val chatAnimValue = BoolValue("ChatAnimation", true)
    val blurStrength = IntegerValue("GlobalBlurStrength", 1, 1, 20)
    val inventoryParticle = BoolValue("InventoryParticle", false)
    private val blurValue = BoolValue("GuiBlur", false)
    val Radius = IntegerValue("BlurRadius", 10 , 1 , 50 )
    private val bottomLeftText: MutableMap<String, String> = LinkedHashMap()

    private fun getClientColor(hud: HUD): Color {
        return Color(CustomColor.r.get(), CustomColor.g.get(), CustomColor.b.get())
    }

    private fun getAlternateClientColor(hud: HUD): Color {
        return Color(CustomColor.r2.get(), CustomColor.g2.get(), CustomColor.b2.get())
    }

    private fun getClientColors(hud: HUD): Array<Color> {
        val firstColor: Color = mixColors(
            getClientColor(hud),
            getAlternateClientColor(hud)
        )
        val secondColor: Color = mixColors(
            getAlternateClientColor(hud),
            getClientColor(hud)
        )
        return arrayOf(firstColor, secondColor)
    }

    @EventTarget
    fun shader(event: BlurEvent) {
        if (classProvider.isGuiHudDesigner(mc.currentScreen))
            return
        draw()
    }

    private fun draw() {
        GlStateManager.resetColor()
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if (classProvider.isGuiHudDesigner(mc.currentScreen))
            return

        LiquidBounce.hud.render(false)

        draw()
    }


    private fun mixColors(color1: Color, color2: Color): Color {
        return ColorUtil.interpolateColorsBackAndForth(
            15,
            1,
            color1,
            color2,
                CustomColor.hueInterpolation.get()
        )
    }

    private fun calculateBPS(): Double {
        val bps = hypot(
            mc.thePlayer!!.posX - mc.thePlayer!!.prevPosX,
            mc.thePlayer!!.posZ - mc.thePlayer!!.prevPosZ
        ) * mc.timer.timerSpeed * 20
        return (bps * 100.0).roundToLong() / 100.0
    }


    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        LiquidBounce.hud.update()
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        LiquidBounce.hud.handleKey('a', event.key)
    }

    @EventTarget(ignoreCondition = true)
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (state && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.guiScreen != null &&
            !(classProvider.isGuiChat(event.guiScreen) || classProvider.isGuiHudDesigner(event.guiScreen))
        ) mc.entityRenderer.loadShader(classProvider.createResourceLocation("More" + "/blur.json")) else if (mc.entityRenderer.shaderGroup != null &&
            mc.entityRenderer.shaderGroup!!.shaderGroupName.contains("lb/blur.json")
        ) mc.entityRenderer.stopUseShader()
    }

    init {
        state = true
    }
}