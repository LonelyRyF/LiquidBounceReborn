package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.event.MoveEvent
import net.minecraft.entity.EntityLivingBase
import java.math.BigDecimal
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MovementUtils : MinecraftInstance() {
    var bps = 0.0
    val speed: Float
        get() = sqrt(mc.thePlayer!!.motionX * mc.thePlayer!!.motionX + mc.thePlayer!!.motionZ * mc.thePlayer!!.motionZ).toFloat()
    fun setMotion(speed: Double) {
        var forward = mc.thePlayer!!.movementInput.moveForward.toDouble()
        var strafe = mc.thePlayer!!.movementInput.moveStrafe.toDouble()
        var yaw = mc.thePlayer!!.rotationYaw
        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer!!.motionX = 0.0
            mc.thePlayer!!.motionZ = 0.0
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (if (forward > 0.0) -45 else 45).toFloat()
                } else if (strafe < 0.0) {
                    yaw += (if (forward > 0.0) 45 else -45).toFloat()
                }
                strafe = 0.0
                if (forward > 0.0) {
                    forward = 1.0
                } else if (forward < 0.0) {
                    forward = -1.0
                }
            }
            val cos = Math.cos(Math.toRadians(yaw + 90.0f.toDouble()))
            val sin = Math.sin(Math.toRadians(yaw + 90.0f.toDouble()))
            mc.thePlayer!!.motionX = (forward * speed * cos
                    + strafe * speed * sin)
            mc.thePlayer!!.motionZ = (forward * speed * sin
                    - strafe * speed * cos)
        }
    }
    @JvmStatic
    val isMoving: Boolean
        get() = mc.thePlayer != null && (mc.thePlayer!!.movementInput.moveForward != 0f || mc.thePlayer!!.movementInput.moveStrafe != 0f)
    @JvmStatic
    fun getScaffoldRotation(yaw: Float, strafe: Float): Float {
        var rotationYaw = yaw
        rotationYaw += 180f
        val forward = -0.5f
        if (strafe < 0f) rotationYaw -= 90f * forward
        if (strafe > 0f) rotationYaw += 90f * forward
        return rotationYaw
    }
    fun doTargetStrafe(curTarget: EntityLivingBase, direction_: Float, radius: Float, moveEvent: MoveEvent, mathRadius: Int = 0) {
        if(!isMoving) return

        var forward_ = 0.0
        var strafe_ = 0.0
        val speed_ = sqrt(moveEvent.x * moveEvent.x + moveEvent.z * moveEvent.z)

        if(speed_ <= 0.0001)
            return

        var _direction = 0.0
        if(direction_ > 0.001) {
            _direction = 1.0
        }else if(direction_ < -0.001) {
            _direction = -1.0
        }
        var curDistance = (0.01).toFloat()
        if (mathRadius == 1) {
            curDistance = mc2.player.getDistance(curTarget)
        }else if (mathRadius == 0) {
            curDistance = sqrt((mc.thePlayer!!.posX - curTarget.posX) * (mc.thePlayer!!.posX - curTarget.posX) + (mc.thePlayer!!.posZ - curTarget.posZ) * (mc.thePlayer!!.posZ - curTarget.posZ)).toFloat()
        }
        if(curDistance < radius - speed_) {
            forward_ = -1.0
        }else if(curDistance > radius + speed_) {
            forward_ = 1.0
        }else {
            forward_ = (curDistance - radius) / speed_
        }
        if(curDistance < radius + speed_*2 && curDistance > radius - speed_*2) {
            strafe_ = 1.0
        }
        strafe_ *= _direction

        val covert_ = sqrt(forward_ * forward_ + strafe_ * strafe_)
        forward_ /= covert_
        strafe_ /= covert_
        var turnAngle = Math.toDegrees(asin(strafe_))
        if(turnAngle > 0) {
            if(forward_ < 0)
                turnAngle = 180F - turnAngle
        }else {
            if(forward_ < 0)
                turnAngle = -180F - turnAngle
        }
        mc.thePlayer!!.motionX = moveEvent.x
        mc.thePlayer!!.motionZ = moveEvent.z
    }
    fun hasMotion(): Boolean {
        return mc.thePlayer!!.motionX != 0.0 && mc.thePlayer!!.motionZ != 0.0 && mc.thePlayer!!.motionY != 0.0
    }
    fun getBlockSpeed(entityIn: EntityLivingBase): Double {
        return BigDecimal.valueOf(
            Math.sqrt(
                Math.pow(
                    entityIn.posX - entityIn.prevPosX,
                    2.0
                ) + Math.pow(entityIn.posZ - entityIn.prevPosZ, 2.0)
            ) * 20
        ).setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()
    }
    fun isOnGround(height: Double): Boolean {
        return if (!mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer!!, mc.thePlayer!!.entityBoundingBox.offset(0.0, -height, 0.0)).isEmpty()) {
            true
        } else {
            false
        }
    }
    @JvmStatic
    @JvmOverloads
    fun strafe(speed: Float = this.speed) {
        if (!isMoving) return
        val yaw = direction
        val thePlayer = mc.thePlayer!!
        thePlayer.motionX = -sin(yaw) * speed
        thePlayer.motionZ = cos(yaw) * speed
    }

    @JvmStatic
    fun forward(length: Double) {
        val thePlayer = mc.thePlayer!!
        val yaw = Math.toRadians(thePlayer.rotationYaw.toDouble())
        thePlayer.setPosition(thePlayer.posX + -sin(yaw) * length, thePlayer.posY, thePlayer.posZ + cos(yaw) * length)
    }

    @JvmStatic
    val direction: Double
        get() {
            val thePlayer = mc.thePlayer!!
            var rotationYaw = thePlayer.rotationYaw
            if (thePlayer.moveForward < 0f) rotationYaw += 180f
            var forward = 1f
            if (thePlayer.moveForward < 0f) forward = -0.5f else if (thePlayer.moveForward > 0f) forward = 0.5f
            if (thePlayer.moveStrafing > 0f) rotationYaw -= 90f * forward
            if (thePlayer.moveStrafing < 0f) rotationYaw += 90f * forward
            return Math.toRadians(rotationYaw.toDouble())
        }
}