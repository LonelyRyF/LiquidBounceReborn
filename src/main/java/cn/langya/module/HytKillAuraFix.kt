package cn.langya.module

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.network.play.client.CPacketAnimation

@ModuleInfo(name = "HytKillAuraFix", description = "HytKillAuraFix", category = ModuleCategory.HYT)
class HytKillAuraFix : Module() {

    private var hytantifakeattack = BoolValue("HytAntiFakeAttack", true)
    private var hytmovefix = BoolValue("HytKillAuraTargetMoveFix", true)
    private var enablegroundrange = BoolValue("EnableGroundRange", true)
    private var enableairrange = BoolValue("EnableAirRange", true)
    private var groundrange = FloatValue("GroundRange", 3.10F, 1.00F, 4.00F).displayable { enablegroundrange.get() }
    private var airrange = FloatValue("AirRange", 2.96F, 1.00F, 4.00F).displayable { enableairrange.get() }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {

        val aura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura

        if (!aura.state) return
        if (mc.thePlayer!!.onGround) {
            if (enablegroundrange.get()) aura.rangeValue.set(groundrange.get())
        } else {
            if (enableairrange.get()) aura.rangeValue.set(airrange.get())
        }

    }

    @EventTarget
    fun onPacket(event : PacketEvent) {

        val aura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura

        if(event.packet.unwrap() is CPacketAnimation && !aura.blockingStatus && aura.target!!.hurtTime > 0 && aura.target!!.motionY > 0 && !aura.target!!.isInWater && !aura.target!!.sneaking && hytantifakeattack.get()) {
            aura.target = null
            aura.blockingStatus = true
            LiquidBounce.hud.addNotification(Notification("AntiFakeAttack","You attacked is set to null",NotifyType.WARNING))
        }

        if(hytmovefix.get()) {
            if(mc.thePlayer!!.isAirBorne) aura.keepSprintValue.set(false) else aura.keepSprintValue.set(true)
        }

        if(aura.target != null) {
            aura.blockingStatus = false
        }

    }
}
