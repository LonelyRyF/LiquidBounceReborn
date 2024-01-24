package cn.liying.utils

import net.ccbluex.liquidbounce.api.minecraft.client.network.INetworkPlayerInfo
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.entity.player.EntityPlayer

object LiYingUtilK : MinecraftInstance() {
    fun getPing(entityPlayer: EntityPlayer?): Int {
        if (entityPlayer == null) return 0
        val networkPlayerInfo: INetworkPlayerInfo? = mc.netHandler.getPlayerInfo(entityPlayer.uniqueID)
        return networkPlayerInfo?.responseTime ?: 0
    }
}