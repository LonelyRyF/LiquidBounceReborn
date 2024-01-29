package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import cn.langya.utils.GaussianBlur
import cn.liying.utils.info.Recorder
import cn.liying.utils.info.Recorder.killCounts
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.RoundedUtil
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FontValue
import net.ccbluex.liquidbounce.value.ListValue
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*

@ElementInfo(name = "SessionInfo")
class SessionInfo(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale) {
    private val GameInfo = ListValue("Mode", arrayOf("Normal"), "Normal")
    val blur = BoolValue("Blur",true)
    val shaodw = BoolValue("Shadow",true)
    private var fontValue = FontValue("Font", Fonts.productSans35)
    val DATE_FORMAT = SimpleDateFormat("HH:mm:ss")

    override fun drawElement(): Border {
        val fontRenderer = fontValue.get()
        val y2 = fontRenderer.fontHeight * 5 + 11.0.toInt()
        val x2 = 140.0.toInt()
        if(GameInfo.get().equals("normal" , true)){
            if(blur.get()) {
                GaussianBlur.startBlur()
                RoundedUtil.drawRound(-2f, -2f, x2.toFloat(), y2.toFloat(),2f,Color(0,0,0,80))
                GaussianBlur.endBlur(2f, 2f)
            }
            if(shaodw.get()) {
                RenderUtils.drawShadow(-2f, -2f, x2.toFloat(), y2.toFloat())
            }
            RoundedUtil.drawRound(-2f, -2f, x2.toFloat(), y2.toFloat(),2f,Color(0,0,0,80))
            RenderUtils.resetColor()

            Fonts.font40.drawCenteredString("Session Info", 31.5F, 3f, Color.WHITE.rgb, true)
            fontRenderer.drawStringWithShadow("Play Time: ${DATE_FORMAT.format(Date(System.currentTimeMillis() - Recorder.startTime - 8000L * 3600L))}", 2, (fontRenderer.fontHeight + 8f).toInt(), Color.WHITE.rgb)
            fontRenderer.drawStringWithShadow("Players Killed: $killCounts", 2, (fontRenderer.fontHeight * 2 + 8f).toInt(), Color.WHITE.rgb)
            fontRenderer.drawStringWithShadow("Win: " + Recorder.totalPlayed, 2,
                    (fontRenderer.fontHeight * 3 + 8f).toInt(), Color.WHITE.rgb)
            fontRenderer.drawStringWithShadow("Total: " +Recorder.totalPlayed , 2,
                    (fontRenderer.fontHeight * 4 + 8f).toInt(), Color.WHITE.rgb)
        }
        return Border(-2f, -2f, x2.toFloat(), y2.toFloat())
    }
}
