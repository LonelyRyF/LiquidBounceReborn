package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.event.StrafeEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot
import net.ccbluex.liquidbounce.features.module.modules.misc.Teams
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


@ModuleInfo("SilentAura","Furry",ModuleCategory.COMBAT)
class SilentAura : Module() {

    //最大攻击速度
    private val maxCPSValue = IntegerValue("MaxCPS", 8, 1, 20)

    //最小攻击速度
    private val minCPSValue = IntegerValue("MinCPS", 5, 0, 19)

    //攻击距离
    val groudRangeValue = FloatValue("GroundRange", 3F, 3F, 8F)
    val airRangeValue = FloatValue("AirRange", 3F, 3F, 8F)

    //攻击是否穿墙
    private val throughWalls = BoolValue("ThroughWalls", true)

    //AB
    private val autoBlockvalue = BoolValue("AutoBlock", true)

    //静默转头
    private val silentrotationValue = BoolValue("SilentRotation", true)

    //最大转头速度
    private val maxTurnSpeed: FloatValue = object : FloatValue("MaxTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minTurnSpeed.get()
            if (v > newValue) set(v)
        }
    }

    //最小转头速度
    private val minTurnSpeed: FloatValue = object : FloatValue("MinTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxTurnSpeed.get()
            if (v < newValue) set(v)
        }
    }

    //移动修复
    private val movefixValue = BoolValue("MoveFix", true)

    //视角
    private val fovValue = FloatValue("FOV", 180f, 0f, 180f)

    //距离光环显示
    private val circleValue = BoolValue("Circle", true)
    private val circleRed = IntegerValue("CircleRed", 255, 0, 255)
    private val circleGreen = IntegerValue("CircleGreen", 255, 0, 255)
    private val circleBlue = IntegerValue("CircleBlue", 255, 0, 255)
    private val circleAlpha = IntegerValue("CircleAlpha", 255, 0, 255)
    private val circleAccuracy = IntegerValue("CircleAccuracy", 15, 0, 60)

    //打人光环显示
    private val mark = BoolValue("Mark", true)

    private val attackTimer = MSTimer()
    var target: IEntityLivingBase? = null
    private var click = 0
    var blocking = false

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (movefixValue.get() && target != null) {
            RotationUtils.targetRotation.applyStrafeToPlayer(event)
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {


        mc.theWorld!!.loadedEntityList
            .asSequence()
            .filterIsInstance<IEntityLivingBase>()
            .filter { isEnemy(it) && mc.thePlayer!!.getDistanceToEntity(it) <= getRange() &&  attackTimer.hasTimePassed(randomClickDelay(minCPSValue.get(), maxCPSValue.get())) }
            .forEach {
                target = it
                attackTimer.reset()
                setRotation()
                attackEntity(it)
                startBlock()
            }

        if (target == null) {
            stopBlocking()
            click = 0
        }

    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {

        if (circleValue.get()) {
            GL11.glPushMatrix()
            GL11.glTranslated(
                mc.thePlayer!!.lastTickPosX + (mc.thePlayer!!.posX - mc.thePlayer!!.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                mc.thePlayer!!.lastTickPosY + (mc.thePlayer!!.posY - mc.thePlayer!!.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY,
                mc.thePlayer!!.lastTickPosZ + (mc.thePlayer!!.posZ - mc.thePlayer!!.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
            )
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

            GL11.glLineWidth(1F)
            GL11.glColor4f(
                circleRed.get().toFloat() / 255.0F,
                circleGreen.get().toFloat() / 255.0F,
                circleBlue.get().toFloat() / 255.0F,
                circleAlpha.get().toFloat() / 255.0F
            )
            GL11.glRotatef(90F, 1F, 0F, 0F)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            for (i in 0..360 step 61 - circleAccuracy.get()) { // You can change circle accuracy  (60 - accuracy)
                GL11.glVertex2f(
                    (cos(i * Math.PI / 180.0).toFloat() * getRange()),
                    ((sin(i * Math.PI / 180.0).toFloat() * getRange()))
                )
            }
            GL11.glVertex2f(
                (cos(360 * Math.PI / 180.0).toFloat() * getRange()),
                ((sin(360 * Math.PI / 180.0).toFloat() * getRange()))
            )

            GL11.glEnd()

            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)

            GL11.glPopMatrix()
        }

    }

    private fun getRange(): Float {
        return if(mc.thePlayer!!.isAirBorne) airRangeValue.get() else groudRangeValue.get()
    }

    // CPS
    private fun randomClickDelay(minCPS: Int, maxCPS: Int): Long {
        return (Math.random() * (1000 / minCPS - 1000 / maxCPS + 1) + 1000 / maxCPS).toLong()
    }

    //  队伍判断以及实体类型判断
    private fun isEnemy(entity: IEntity?): Boolean {

        if(entity !is IEntityLivingBase) {
            return false
        }

        if (entity is EntityLivingBase && entity.isEntityAlive && entity.health > 0 && entity != mc.thePlayer) {
            if (!EntityUtils.targetInvisible && entity.isInvisible)
                return false

            if (EntityUtils.targetPlayer && entity is EntityPlayer) {
                if (entity.isSpectator || AntiBot.isBot(entity))
                    return false

                val teams = LiquidBounce.moduleManager[Teams::class.java] as Teams

                return !teams.state || !teams.isInYourTeam(entity)
            }

            return EntityUtils.targetMobs && EntityUtils.isMob(entity) || EntityUtils.targetAnimals &&
                    EntityUtils.isAnimal(entity)
        }

        return false
    }

    //视角
    private fun isFovInRange(entity: IEntity? = target, fov: Float = fovValue.get()): Boolean {

        var fov = fov
        fov *= 0.5.toFloat()
        val v: Double =
            ((mc.thePlayer!!.rotationYaw - getPlayerRotation(entity)) % 360.0 + 540.0) % 360.0 - 180.0
        return v > 0.0 && v < fov || -fov < v && v < 0.0
    }

    //获取玩家转头
    private fun getPlayerRotation(entity: IEntity?): Float {
        val x: Double = entity!!.posX - mc.thePlayer!!.posX
        val z: Double = entity.posZ - mc.thePlayer!!.posZ
        var yaw = atan2(x, z) * 57.2957795
        yaw = -yaw
        return yaw.toFloat()
    }

    private fun startBlock() {

        if(!autoBlockvalue.get()) {
            return
        }

        if(!isFovInRange()) {
            return
        }


        mc2.connection!!.sendPacket(
            CPacketPlayerDigging(
            CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
            BlockPos(mc2.player.posX, mc2.player.posY, mc2.player.posZ),
            EnumFacing.UP)
        )

    }


    //  攻击
    private fun attackEntity(entity: IEntityLivingBase?) {

        if(!RotationUtils.isFaced(entity, getRange().toDouble())) {
            return
        }

        // 防止连续发送攻击包
        if (mc2.player.attackingEntity != null) {
            return
        }

        if(!isFovInRange()) {
            return
        }

        // 停止防砍
        stopBlocking()

        mc2.playerController.attackEntity(mc2.player, entity!!.unwrap())

    }


    //  转头
    private fun setRotation() {

        val (_, rotation) = RotationUtils.lockView(
            target!!.entityBoundingBox,
            false,
            false,
            false,
            throughWalls.get(),
            getRange()
        )

        val limitedRotation = RotationUtils.limitAngleChange(
            RotationUtils.serverRotation,
            rotation,
            (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
        )

        // 转头逻辑
        if (silentrotationValue.get()) {
            RotationUtils.setTargetRotation(limitedRotation, 0)
            mc2.player.renderYawOffset = RotationUtils.targetRotation.yaw
        } else {
            limitedRotation.toPlayer(mc.thePlayer!!)
        }

    }

    private fun stopBlocking() {

        if (mc.thePlayer!!.isBlocking || blocking) {
            mc2.connection!!.sendPacket(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN, EnumFacing.DOWN
                )
            )
            blocking = false
        }

    }

    private fun drawEntityESP(entity: Entity?, color: Int) {

        if(!mark.get()) {
            return
        }

        GL11.glPushMatrix()
        GL11.glDisable(3553)
        GL11.glEnable(2848)
        GL11.glEnable(2832)
        GL11.glEnable(3042)
        GL11.glBlendFunc(770, 771)
        GL11.glHint(3154, 4354)
        GL11.glHint(3155, 4354)
        GL11.glHint(3153, 4354)
        GL11.glDepthMask(false)
        GL11.glEnable(2929)
        GlStateManager.alphaFunc(516, 0.0f)
        GL11.glShadeModel(7425)
        GlStateManager.disableCull()
        GL11.glBegin(5)
        val entity2 = entity!!
        val x: Double =
            entity2.lastTickPosX + (entity2.posX - entity2.lastTickPosX) * mc.timer.renderPartialTicks - Minecraft.getMinecraft().renderManager.renderPosX
        val y: Double =
            entity2.lastTickPosY + (entity2.posY - entity2.lastTickPosY) * mc.timer.renderPartialTicks - Minecraft.getMinecraft().renderManager.renderPosY + sin(
                System.currentTimeMillis() / 200.0
            ) * (entity2.height / 2.0f) + 1.0f * (entity2.height / 2.0f)
        val z: Double =
            entity2.lastTickPosZ + (entity2.posZ - entity2.lastTickPosZ) * mc.timer.renderPartialTicks - Minecraft.getMinecraft().renderManager.renderPosZ
        var i = 0.0f
        while (i < 6.283185307179586) {
            val vecX = x + 0.67 * cos(i.toDouble())
            val vecZ = z + 0.67 * sin(i.toDouble())
            RenderUtils.glColor(
                Color(
                    RenderUtils.getColor(color).red,
                    RenderUtils.getColor(color).green,
                    RenderUtils.getColor(color).blue,
                    0
                ).rgb
            )
            GL11.glVertex3d(vecX, y - cos(System.currentTimeMillis() / 200.0) * (entity2.height / 2.0f) / 2.0, vecZ)
            RenderUtils.glColor(
                Color(
                    RenderUtils.getColor(color).red,
                    RenderUtils.getColor(color).green,
                    RenderUtils.getColor(color).blue,
                    160
                ).rgb
            )
            GL11.glVertex3d(vecX, y, vecZ)
            i += 0.09817477042468103.toFloat()
        }
        GL11.glEnd()
        GL11.glShadeModel(7424)
        GL11.glDepthMask(true)
        GL11.glEnable(2929)
        GlStateManager.alphaFunc(516, 0.1f)
        GlStateManager.enableCull()
        GL11.glDisable(2848)
        GL11.glDisable(2848)
        GL11.glEnable(2832)
        GL11.glEnable(3553)
        GL11.glPopMatrix()
        GL11.glColor3f(255.0f, 255.0f, 255.0f)
    }

    override val tag: String
        get() = "${getRange()}-$click"
}
