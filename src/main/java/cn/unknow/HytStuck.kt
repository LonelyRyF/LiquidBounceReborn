package cn.unknow;

import net.ccbluex.liquidbounce.event.EventTarget import net.ccbluex.liquidbounce.event.PacketEvent import net.ccbluex.liquidbounce.event.UpdateEvent import net.ccbluex.liquidbounce.features.module.Module import net.ccbluex.liquidbounce.features.module.ModuleCategory import net.ccbluex.liquidbounce.features.module.ModuleInfo import net.ccbluex.liquidbounce.injection.backend.unwrap import net.minecraft.network.play.client.CPacketPlayer import net.minecraft.network.play.client.CPacketPlayerTryUseItem import net.minecraft.util.EnumHand import javax.vecmath.Tuple2f import javax.vecmath.Vector2f

@ModuleInfo(name = "HytStuck", description = "CPlayerXInXInPacket", category = ModuleCategory.HYT)
class HytStuck : Module() {
        var x = 0.0
        var y = 0.0
        var z = 0.0
        var rotation = Vector2f()
        var motionX = 0.0
        var motionY = 0.0
        var motionZ = 0.0
        var onGround = false

        @EventTarget
        override fun onEnable() {
                if (mc.thePlayer == null) {
                        return
                }
                x = mc.thePlayer!!.posX
                y = mc.thePlayer!!.posY
                z = mc.thePlayer!!.posZ
                motionX = mc.thePlayer!!.motionX
                motionY = mc.thePlayer!!.motionY
                motionZ = mc.thePlayer!!.motionZ
                rotation = Vector2f(mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch)
                onGround = mc.thePlayer!!.onGround
        }

        @EventTarget
        fun onPacket(event: PacketEvent) {
                val packet = event.packet.unwrap()
                val connection = mc.unwrap().connection ?: return
                if (packet is CPacketPlayerTryUseItem) {
                        val current = Vector2f(mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch)
                        val f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f
                        val gcd = f * f * f * 1.2f
                        val vector2f: Vector2f = current
                        vector2f.x -= current.x % gcd
                        val vector2f2: Vector2f = current
                        vector2f2.y -= current.y % gcd
                        if (this.rotation.equals(current as Tuple2f)) {
                                return
                        }
                        this.rotation = current
                        event.cancelEvent()
                        connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.OFF_HAND))
                        connection.sendPacket(CPacketPlayer.Rotation(current.x, current.y, this.onGround))
                }

                if (packet is CPacketPlayer) {
                        event.cancelEvent()
                }
        }

        @EventTarget
        fun onUpdate(event: UpdateEvent) {
                mc.thePlayer!!.motionX = 0.0
                mc.thePlayer!!.motionY = 0.0
                mc.thePlayer!!.motionZ = 0.0
                mc.thePlayer!!.setPosition(this.x, this.y, this.z)
        }
}

