/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package cn.liying.utils.packet;

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class PacketUtils extends MinecraftInstance {
    public static List<IPacket> pList = new ArrayList<IPacket>();

    public static void send(IPacket pac) {
        pList.add(pac);
        mc.getNetHandler().getNetworkManager().sendPacket(pac);


    }

    public static void send(@NotNull CPacketKeepAlive cPacketKeepAlive) {

    }

    public static void send(@NotNull CPacketConfirmTransaction cPacketConfirmTransaction) {

    }
}
