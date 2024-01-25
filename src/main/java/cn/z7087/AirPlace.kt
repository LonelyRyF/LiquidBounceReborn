package cn.z7087

import net.ccbluex.liquidbounce.api.minecraft.util.IMovingObjectPosition
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.*
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.injection.backend.wrap
import net.ccbluex.liquidbounce.injection.backend.utils.unwrap
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.util.math.AxisAlignedBB
import java.awt.Color
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

@ModuleInfo(name = "HytAirPlace", description = "7087z", category = ModuleCategory.HYT)
class AirPlace : Module() {
    private val range = FloatValue("Range", 4.5f, 2f, 8f)

    private var hitResult: IMovingObjectPosition? = null
    private var blockPos: WBlockPos? = null

    override fun onEnable() {
        hitResult = null
        blockPos = null
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        val renderViewEntity = mc.renderViewEntity ?: return
        val thePlayer = mc.thePlayer!!
        val theWorld = mc.theWorld!!
        if (mc.objectMouseOver?.typeOfHit != IMovingObjectPosition.WMovingObjectType.MISS) {
            hitResult = null
            blockPos = null
            return
        }

        val distance = range.get().toDouble()

        val eyePos = renderViewEntity.getPositionEyes(1f)
        val rotationVec = renderViewEntity.getLook(1f)
        val rayTraceEndVec = eyePos.addVector(rotationVec.xCoord * distance, rotationVec.yCoord * distance, rotationVec.zCoord * distance)
        val rayTraceEndPos = WBlockPos(rayTraceEndVec.xCoord, rayTraceEndVec.yCoord, rayTraceEndVec.zCoord)
        if (!BlockUtils.isReplaceable(rayTraceEndPos)) {
            hitResult = null
            blockPos = null
            return
        }
        val endAABB = AxisAlignedBB(rayTraceEndPos.unwrap()).wrap()
        hitResult = endAABB.calculateIntercept(eyePos, rayTraceEndVec)
        val hitResultNullSafe = hitResult ?: return

        blockPos = rayTraceEndPos
        val sideHit = hitResultNullSafe.sideHit!!
        val hitVec = hitResultNullSafe.hitVec

        if (thePlayer.unwrap().inventoryContainer != thePlayer.unwrap().openContainer)
            return

        val keyCode = mc.gameSettings.keyBindUseItem.keyCode
        if (if (keyCode < 0) Mouse.isButtonDown(keyCode + 100) else Keyboard.isKeyDown(keyCode)) {
            var heldItem = thePlayer.heldItem
            if (heldItem != null && heldItem.item != null) {
                if (!classProvider.isItemAir(heldItem.item) && !classProvider.isItemBlock(heldItem.item))
                    return
                mc.playerController.windowClick(0, thePlayer.inventory.currentItem + 36, 0, 1, thePlayer)
                thePlayer.closeScreen()
                heldItem = thePlayer.heldItem
                if (heldItem != null && !classProvider.isItemAir(heldItem.item))
                    return
            }

            var blockSlot = -1
            for (i in 0 until 36) {
                if (classProvider.isItemBlock(thePlayer.inventory.mainInventory.get(i)?.item)) {
                    blockSlot = i
                    break
                }
            }

            if (blockSlot == -1)
                return

            mc.playerController.unwrap().pickItem(blockSlot)
            mc.playerController.onPlayerRightClick(thePlayer, theWorld, thePlayer.heldItem, blockPos!!, sideHit, hitVec)
            thePlayer.swingItem()
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (hitResult != null) {
            RenderUtils.drawBlockBox(blockPos!!, Color.WHITE, true)
        }
    }
}