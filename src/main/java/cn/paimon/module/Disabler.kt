package cn.paimon.module

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketClientStatus
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketKeepAlive

@ModuleInfo(name = "Disabler", description = "Fucking Grim", category = ModuleCategory.HYT)
class Disabler : Module() {
    var postValue: BoolValue
    var badPacketsA: BoolValue
    var badPacketsF: BoolValue
    var fakePingValue: BoolValue
    private val packetsMap: HashMap<Packet<*>, Long?>
    var lastSlot: Int
    var startChiJiDisabler = false
    var lastSprinting = false

    init {
        postValue = BoolValue("NewGrim-Post", false)
        badPacketsA = BoolValue("Grim-BadPacketsA", false)
        badPacketsF = BoolValue("Grim-BadPacketsF", false)
        fakePingValue = BoolValue("Grim-FakePing", false)
        packetsMap = HashMap()
        lastSlot = -1
    }

    override val tag: String?
        get() = "Grim"

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (fakePingValue.get()) {
            try {
                synchronized(packetsMap) {
                    val iterator: MutableIterator<Map.Entry<Packet<*>, Long?>> = packetsMap.entries.iterator()
                    while (iterator.hasNext()) {
                        val (key, value) = iterator.next()
                        if (value!! < System.currentTimeMillis()) {
                            mc2.connection!!.networkManager.sendPacket(key)
                            iterator.remove()
                        }
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent?) {
        lastSlot = -1
        lastSprinting = false
        startChiJiDisabler = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()

        if (mc2.player == null) {
            return
        }
        if (mc2.player.isDead) {
            return
        }
        if (badPacketsF.get() && packet is CPacketEntityAction) {
            if (packet.action == CPacketEntityAction.Action.START_SPRINTING) {
                if (lastSprinting) {
                    event.cancelEvent()
                }
                lastSprinting = true
            } else if (packet.action == CPacketEntityAction.Action.STOP_SPRINTING) {
                if (!lastSprinting) {
                    event.cancelEvent()
                }
                lastSprinting = false
            }
        }
        if (badPacketsA.get() && packet is CPacketHeldItemChange) {
            val slot = packet.slotId
            if (slot == lastSlot && slot != -1) {
                event.cancelEvent()
            }
            lastSlot = packet.slotId
        }
        if (fakePingValue.get() && (packet is CPacketKeepAlive || packet is CPacketClientStatus) && mc2.player.health > 0.0f && !packetsMap.containsKey(
                packet
            )
        ) {
            event.cancelEvent()
            synchronized(packetsMap) {
                packetsMap.put(
                    packet,
                    System.currentTimeMillis() + TimeUtils.randomDelay(199999, 9999999)
                )
            }
        }
    }
}
