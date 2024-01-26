package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.network.play.server.SPacketChat
import java.util.regex.Pattern
@ModuleInfo(name = "AutoGG", category = ModuleCategory.PLAYER, description = "Auto GG")
class AutoGG : Module() {

    private val modeValue = ListValue("Server", arrayOf( "HuaYuTingBW","HuaYuTingSw","HuaYuTing16"), "HuaYuTingBW")
    private val prefix = BoolValue("@",true)
    private val textValue = TextValue("Text", "[LiquidBounceReborn]GG")
    private val textValu = TextValue("Text2", "@我正在使用LiquidBounceReborn")
    var totalPlayed = 0
    var win = 0
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        if (packet is SPacketChat) {
            val text = packet.chatComponent.unformattedText
            when (modeValue.get().toLowerCase()) {
                "huayutingbw" -> {
                    if (text.contains("      喜欢      一般      不喜欢", true)) {
                        mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "")+textValue.get())
                        win += 1
                        LiquidBounce.hud.addNotification(Notification("AutoGG", "恭喜胜利！", NotifyType.INFO))

                    }
                    if (text.contains("起床战争>> 游戏开始 ...", true)) {
                        totalPlayed ++
                        LiquidBounce.hud.addNotification(Notification("AutoGG", "游戏开始！！", NotifyType.INFO))
                        mc.thePlayer!!.sendChatMessage(textValu.get())

                    }
                }
                "huayuting16" -> {
                    if (text.contains("[起床战争] Game 结束！感谢您的参与！", true)) {
                        LiquidBounce.hud.addNotification(Notification("AutoGG","Game Over", NotifyType.INFO))
                        mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "")+textValue.get())
                    }
                }
                "huayutingsw" -> {

                    val matcher = Pattern.compile("你在地图 (.*?)\\(").matcher(packet.chatComponent.unformattedText)
                    if (text.contains("你现在是观察者状态. 按E打开菜单.", true)) {
                        LiquidBounce.hud.addNotification(Notification("AutoGG","Game Over", NotifyType.INFO))
                        mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "")+textValue.get())

                    }
                }

            }

        }
    }
    override fun handleEvents() = true
    override val tag: String
        get() = modeValue.get()
}
