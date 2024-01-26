package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity
import net.ccbluex.liquidbounce.script.api.global.Chat
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.TextValue

@ModuleInfo(name = "HytAutoLeos", description = "SB", category = ModuleCategory.HYT)
class HytAutoLeos : Module() {
    private val healths = FloatValue("Health", 5f, 1f, 20f)
    private val loa = BoolValue("MessageA", false)
    private val lobbyValue = TextValue("Message", "")
    private var check = true
    private var keepArmor = false
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer!!.health <= healths.get()) {
            for (i in 3 downTo 0) {
                val stack = mc.thePlayer!!.inventory.armorInventory[i]
                if(mc.thePlayer!!.inventory.armorInventory[3] != null) {
                    move(5, true)
                }

                if (stack != null && !(stack.unlocalizedName == "item.chestplateChain"
                                || stack.unlocalizedName == "item.chestplateChain"
                                || stack.unlocalizedName == "item.leggingsChain"
                                || stack.unlocalizedName == "item.chestplateIron"
                                || stack.unlocalizedName == "item.leggingsIron"
                                || stack.unlocalizedName == "item.bootsIron")) {
                    move(8 - i, true)
                }

                if (i == 0) {
                    keepArmor = false
                }
            }

            if (loa.get() && check) {
                mc.thePlayer!!.sendChatMessage(lobbyValue.get())
            }

            if (check && !keepArmor) {
                mc.thePlayer!!.sendChatMessage("/hub")
                LiquidBounce.moduleManager[KillAura::class.java].state = false
                LiquidBounce.moduleManager[Velocity::class.java].state = false
                check = false
            }
            Chat.print("§b[LiquidBounceReborn] §d为你的装备保驾护航")
        }
    }

    private fun move(item: Int, isArmorSlot: Boolean) {
        if (item != -1) {
            val openInventory = !classProvider.isGuiInventory(mc.currentScreen)
            if (openInventory) mc.netHandler.addToSendQueue(
                    classProvider.createCPacketEntityAction(
                            mc.thePlayer!!,
                            ICPacketEntityAction.WAction.OPEN_INVENTORY
                    )
            )

            mc.playerController.windowClick(
                    mc.thePlayer!!.inventoryContainer.windowId,
                    if (isArmorSlot) item else if (item < 9) item + 36 else item,
                    0,
                    1,
                    mc.thePlayer!!
            )
            if (openInventory) mc.netHandler.addToSendQueue(classProvider.createCPacketCloseWindow())
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        check = true
        keepArmor = true
    }
    override val tag: String?
        get() = "Health"+" "+healths.get()
}