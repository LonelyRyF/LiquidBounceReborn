package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.autoL
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import java.util.*

@ModuleInfo(name = "AutoL", description = "AutoL. ", category = ModuleCategory.PLAYER)
class AutoL : Module() {
    val modeValue = ListValue("Mode", arrayOf("Chinese", "English","zhuboMessage","yurluMessage","YuJiangJun","Ikun", "L","None","Text"), "None")
    val lobbyValue = TextValue("Text", "FurrySense 2024/Genuine edition")
    private val prefix = BoolValue("@",true)
    private val delay = IntegerValue("Delay",100,0,2000)
    var index = 0
    var R = Random()
    var abuse = arrayOf("FurrySense 2024/Genuine edition")
    var englishabuse = arrayOf("You are loser")
    private var target: IEntity? = null
    var kill = 0
    val msTimer = MSTimer()
    fun AutoL() {
        state = true
    }
    @EventTarget
    fun onAttack(event: AttackEvent) {
        target = event.targetEntity
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (target != null) {
            if (target!!.isDead) {
                if (msTimer.hasTimePassed(delay.get().toLong())) {
                    index ++
                    when (modeValue.get()) {
                        "Chinese" -> {
                            mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "")+":"+ abuse[R.nextInt(abuse.size)]
                            )
                            kill += 1
                            target = null
                        }
                        "YuJiangJun" -> {
                            if (index > autoL.YuJiangJun.size) index = 0
                            mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "") + " " + autoL.YuJiangJun[index])
                            kill += 1
                            target = null
                        }
                        "zhuboMessage" -> {
                            if (index > autoL.zhuboMessage.size) index = 0
                            mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "") + " " + autoL.zhuboMessage[index])
                            kill += 1
                            target = null
                        }
                        "yurluMessage" -> {
                            if (index > autoL.yurluMessage.size) index = 0
                            mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "") + " " + autoL.yurluMessage[index])
                            kill += 1
                            target = null
                        }
                        "Ikun" -> {
                            if (index > autoL.Ikun.size) index = 0
                            mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "") + " " + autoL.Ikun[index])
                            kill += 1
                            target = null
                        }
                        "English" -> {
                            kill += 1
                            mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "") + "  " + englishabuse[R.nextInt(
                                englishabuse.size
                            )]
                            )
                            target = null
                        }
                        "L" -> {
                            mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "")+" L " + target!!.name)
                            kill += 1
                            target = null
                        }
                        "None" -> {
                            kill += 1
                            target = null
                        }
                        "Text" -> {
                            mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "")+lobbyValue.get()+" [" + target!!.name+"]")
                            kill += 1
                            target = null
                        }
                    }
                    msTimer.reset()
                }
            }
        }
    }
    override val tag: String
        get() = "Kills%$kill"
}