package cn.liying.module.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.value.FloatValue

@ModuleInfo(name = "HytKillFix", description = "KillFix", category = ModuleCategory.HYT)
class KillFix : Module() {
    private val AirRange: FloatValue = FloatValue("AirRange", 3f, 0f, 5f)
    private val GroundRange: FloatValue = FloatValue("GroundRange", 3.5f, 0f, 5f)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura

        if (mc.thePlayer!!.isAirBorne) {
            killAura.rangeValue.set(AirRange.get())
        }
        if (mc.thePlayer!!.onGround) {
            killAura.rangeValue.set(GroundRange.get())
        }
    }
}






