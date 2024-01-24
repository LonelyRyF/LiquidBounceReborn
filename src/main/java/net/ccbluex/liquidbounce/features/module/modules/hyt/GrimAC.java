package net.ccbluex.liquidbounce.features.module.modules.hyt;

// 魔改了派蒙神的东西

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.WorldEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;

import java.text.DecimalFormat;

@ModuleInfo(name = "GrimAC",description = "langya",category = ModuleCategory.HYT)
public class GrimAC
        extends Module {
    public BoolValue reachValue = new BoolValue("Reach", true);
    public BoolValue noslowAValue = new BoolValue("NoSlowA", true);
    public BoolValue velocityValue = new BoolValue("Velocity", true);
    public static final DecimalFormat DF_1 = new DecimalFormat("0.000000");
    int vl;
    static Minecraft mc = mc2;


    @Override
    public void onEnable() {
        vl = 0;
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        vl = 0;
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if(mc.player == null) return;

        if (mc.player.ticksExisted % 6 == 0) {
            SPacketEntityStatus s19;
            if (event.getPacket() instanceof SPacketEntityStatus && reachValue.get() && (s19 = (SPacketEntityStatus)event.getPacket()).getOpCode() == 2) {
                new Thread(() -> checkCombatHurt(s19.getEntity(mc.world))).start();
            }
            if (event.getPacket() instanceof SPacketEntity && noslowAValue.get()) {
                SPacketEntity packet = (SPacketEntity)event.getPacket();
                Entity entity = packet.getEntity(mc.world);
                    if (!(entity instanceof EntityPlayer && entity.getName().equals(mc.player.getName()))) {
                    return;
                }
                new Thread(() -> checkPlayer((EntityPlayer)entity)).start();
            }
        }
    }


    private void checkCombatHurt(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }
        if(entity == mc.player) {
            return;
        }
        EntityPlayer attacker = null;
        int attackerCount = 0;
        for (Entity worldEntity : mc.world.getLoadedEntityList()) {
            if (!(worldEntity instanceof EntityPlayer) || worldEntity.getDistance(entity) > 7.0f || (worldEntity).equals(entity)) continue;
            ++attackerCount;
            attacker = (EntityPlayer)worldEntity;
        }
        if (attacker == null || attacker.equals(entity)) {
            return;
        }
        if(entity.getName().equals(mc.player.getName())) {
            return;
        }
        double reach = attacker.getDistance(entity);
        String prefix = TextFormatting.GRAY + "[" + TextFormatting.AQUA + "GrimAC" + TextFormatting.GRAY + "] " + TextFormatting.RESET + TextFormatting.GRAY + attacker.getName() + TextFormatting.WHITE + " failed ";
        if (reach > 3.0 && ((EntityLivingBase) entity).getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword) {
            ClientUtils.displayChatMessage(prefix + TextFormatting.AQUA + "Reach" + TextFormatting.WHITE + " (vl:" + attackerCount + ".0)" + TextFormatting.GRAY + ": " + DF_1.format(reach) + " blocks");
        }

    }

    private void checkPlayer(EntityPlayer player) {
        if (player.equals(mc.player)) {
            return;
        }
        String prefix = TextFormatting.GRAY + "[" + TextFormatting.AQUA + "GrimAC" + TextFormatting.GRAY + "] " + TextFormatting.RESET + TextFormatting.GRAY + player.getName() + TextFormatting.WHITE + " failed ";
        if (player.isHandActive() && (player.posX - player.lastTickPosX > 0.2 || player.posZ - player.lastTickPosZ > 0.2)) {
            ClientUtils.displayChatMessage(prefix + TextFormatting.AQUA + "NoSlowA (Prediction)" + TextFormatting.WHITE + " (vl:" + this.vl + ".0)");
            ++vl;

        }

        if(player.motionY >= 0 && player.hurtTime > 0 && velocityValue.get()) {
            ClientUtils.displayChatMessage(prefix + TextFormatting.AQUA + "VelocityA" + TextFormatting.WHITE + " (vl:" + this.vl + ".0)");
        }

        if (!mc.world.loadedEntityList.contains(player) || !player.isEntityAlive()) {
            vl = 0;
        }
    }
}
