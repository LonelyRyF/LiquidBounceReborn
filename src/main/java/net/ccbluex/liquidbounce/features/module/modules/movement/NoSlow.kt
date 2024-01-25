
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.WEnumHand
import net.ccbluex.liquidbounce.api.minecraft.item.IItem
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.SlowDownEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.render.Animations
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.createUseItemPacket
import net.ccbluex.liquidbounce.utils.render.BlockAnimationUtils.thePlayerisBlocking
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos

@ModuleInfo(name = "NoSlow", category = ModuleCategory.MOVEMENT, description = "OMG")
class NoSlow : Module() {

    private val packet = BoolValue("Packet", false)
    private val msTimer = MSTimer()
    private val modeValue = ListValue("PacketMode", arrayOf("Hyt","GrimAC","AntiCheat", "Custom", "NCP", "AAC", "AAC5"), "GrimAC")
    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val customOnGround = BoolValue("CustomOnGround", false)
    private val customDelayValue = IntegerValue("CustomDelay", 60, 10, 200)

    // Soulsand
    val soulsandValue = BoolValue("Soulsand", false)

    var pendingFlagApplyPacket = false

    override fun onDisable() {
        msTimer.reset()
        pendingFlagApplyPacket = false
    }

    private fun sendPacket(
            event: MotionEvent,
            sendC07: Boolean,
            sendC08: Boolean,
            delay: Boolean,
            delayValue: Long,
            onGround: Boolean
    ) {
        val digging = classProvider.createCPacketPlayerDigging(ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM, WBlockPos(-1, -1, -1), classProvider.getEnumFacing(EnumFacingType.DOWN))
        val blockMain = createUseItemPacket(mc.thePlayer!!.inventory.getCurrentItemInHand(), WEnumHand.MAIN_HAND)
        val blockOFF = createUseItemPacket(mc.thePlayer!!.inventory.getCurrentItemInHand(), WEnumHand.OFF_HAND)
        if (onGround && !mc.thePlayer!!.onGround) {
            return
        }
        if (sendC07 && event.eventState == EventState.PRE) {
            if (delay && msTimer.hasTimePassed(delayValue)) {
                mc.netHandler.addToSendQueue(digging)
            } else if (!delay) {
                mc.netHandler.addToSendQueue(digging)
            }
        }
        if (sendC08 && event.eventState == EventState.POST) {
            if (delay && msTimer.hasTimePassed(delayValue)) {
                if (LiquidBounce.moduleManager[Animations::class.java].state) {
                    mc.netHandler.addToSendQueue(blockOFF)
                } else {
                    mc.netHandler.addToSendQueue(blockMain)
                    mc.netHandler.addToSendQueue(blockOFF)
                }
                msTimer.reset()
            } else if (!delay) {
                if (LiquidBounce.moduleManager[Animations::class.java].state) {
                    mc.netHandler.addToSendQueue(blockOFF)
                } else {
                    mc.netHandler.addToSendQueue(blockMain)
                    mc.netHandler.addToSendQueue(blockOFF)
                }
            }
        }
    }

    val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura

    fun isBlock(): Boolean {
        return thePlayerisBlocking || killAura.blockingStatus
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (!MovementUtils.isMoving) {
            return
        }
        when (modeValue.get().toLowerCase()) {
            "anticheat" -> {
                this.sendPacket(event, true, false, false, 0, false)
                if (mc.thePlayer!!.ticksExisted % 2 == 0) {
                    this.sendPacket(event, false, true, false, 0, false)
                }
            }
            "hyt" -> {
                if (mc.thePlayer!!.isBlocking || isBlock() && event.eventState == EventState.PRE) {
                    mc2.connection!!.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                    mc2.connection!!.sendPacket(CPacketHeldItemChange((mc2.player.inventory.currentItem + 1) % 9))
                    mc2.connection!!.sendPacket(CPacketConfirmTransaction(Integer.MAX_VALUE, 32767.toShort(), true))
                    mc2.connection!!.sendPacket(CPacketHeldItemChange(mc2.player.inventory.currentItem))
                }

                if (mc.thePlayer!!.isBlocking || isBlock() && event.eventState == EventState.POST) {
                    mc2.connection!!.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
                }

            }
            "grimac" -> {
                if (!mc2.player.isActiveItemStackBlocking) return
                if (event.eventState == EventState.PRE) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(
                        ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM, WBlockPos.ORIGIN, classProvider.getEnumFacing(
                            EnumFacingType.DOWN)))
                    mc2.connection!!.sendPacket(CPacketHeldItemChange(mc2.player.inventory.currentItem % 9 + 1))
                    mc2.connection!!.sendPacket(
                        CPacketConfirmTransaction(
                            mc.thePlayer!!.inventoryContainer.windowId,
                            1.toShort(),
                            true
                        )
                    )
                    mc2.connection!!.sendPacket(CPacketHeldItemChange(mc2.player.inventory.currentItem))
                } else {
                    mc2.connection!!.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
                }
            }
            "aac" -> {
                if (mc.thePlayer!!.ticksExisted % 3 == 0) {
                    sendPacket(event, true, false, false, 0, false)
                } else {
                    sendPacket(event, false, true, false, 0, false)
                }
            }

            "aac5" -> {
                if (mc.thePlayer!!.isUsingItem || mc.thePlayer!!.isBlocking || isBlock()) {
                    mc.netHandler.addToSendQueue(createUseItemPacket(mc.thePlayer!!.inventory.getCurrentItemInHand(), WEnumHand.MAIN_HAND))
                    mc.netHandler.addToSendQueue(createUseItemPacket(mc.thePlayer!!.inventory.getCurrentItemInHand(), WEnumHand.OFF_HAND))
                }
            }

            "custom" -> {
                sendPacket(event, true, true, true, customDelayValue.get().toLong(), customOnGround.get())
            }

            "ncp" -> {
                sendPacket(event, true, true, false, 0, false)
            }
        }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer!!.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: IItem?, isForward: Boolean): Float {
        return when {
            classProvider.isItemFood(item) || classProvider.isItemPotion(item) || classProvider.isItemBucketMilk(item) -> {
                if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
            }
            classProvider.isItemSword(item) -> {
                if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
            }
            classProvider.isItemBow(item) -> {
                if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
            }
            else -> 0.2F
        }
    }
}