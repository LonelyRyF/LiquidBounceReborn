package cn.langya

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura

@ModuleInfo(name = "HytScaffoldFix", description = "HytScaffoldFix", category = ModuleCategory.HYT)
class HytScaffoldFix : Module() {
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val scaffoldModule = LiquidBounce.moduleManager[KillAura::class.java] as KillAura

        scaffoldModule.state = !mc.thePlayer!!.onGround
    }
}
