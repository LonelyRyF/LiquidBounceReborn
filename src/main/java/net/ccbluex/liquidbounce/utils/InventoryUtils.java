package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.api.enums.BlockType;
import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock;
import net.ccbluex.liquidbounce.api.minecraft.item.IItem;
import net.ccbluex.liquidbounce.api.minecraft.item.IItemBlock;
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack;
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket;
import net.ccbluex.liquidbounce.event.ClickWindowEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Listenable;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.minecraft.item.Item;

import java.util.Arrays;
import java.util.List;

public final class InventoryUtils extends MinecraftInstance implements Listenable {

    public static final MSTimer CLICK_TIMER = new MSTimer();
    public static final List<IBlock> BLOCK_BLACKLIST = Arrays.asList(
            classProvider.getBlockEnum(BlockType.CHEST), classProvider.getBlockEnum(BlockType.ENDER_CHEST), classProvider.getBlockEnum(BlockType.TRAPPED_CHEST), classProvider.getBlockEnum(BlockType.ANVIL), classProvider.getBlockEnum(BlockType.SAND), classProvider.getBlockEnum(BlockType.WEB), classProvider.getBlockEnum(BlockType.TORCH),
            classProvider.getBlockEnum(BlockType.CRAFTING_TABLE), classProvider.getBlockEnum(BlockType.FURNACE), classProvider.getBlockEnum(BlockType.WATERLILY), classProvider.getBlockEnum(BlockType.DISPENSER), classProvider.getBlockEnum(BlockType.STONE_PRESSURE_PLATE), classProvider.getBlockEnum(BlockType.WODDEN_PRESSURE_PLATE),
            classProvider.getBlockEnum(BlockType.NOTEBLOCK), classProvider.getBlockEnum(BlockType.DROPPER), classProvider.getBlockEnum(BlockType.TNT), classProvider.getBlockEnum(BlockType.STANDING_BANNER), classProvider.getBlockEnum(BlockType.WALL_BANNER), classProvider.getBlockEnum(BlockType.REDSTONE_TORCH)
    );
    //搭路
    public static boolean isBlockListBlock(IItemBlock itemBlock){
        final IBlock block = itemBlock.getBlock();
        return BLOCK_BLACKLIST.contains(block) || !block.isFullCube(block.getDefaultState());
    }
    //自动金头
    public static int findItem2(final int startSlot, final int endSlot, final Item item) {
        for (int i = startSlot; i < endSlot; i++) {
            final IItemStack stack = mc.getThePlayer().getInventoryContainer().getSlot(i).getStack();

            if (stack != null && stack.getItem() == item)
                return i;
        }
        return -1;
    }
    public static int findItem(final int startSlot, final int endSlot, final IItem item) {
        for (int i = startSlot; i < endSlot; i++) {
            final IItemStack stack = mc.getThePlayer().getInventoryContainer().getSlot(i).getStack();

            if (stack != null && stack.getItem().equals(item))
                return i;
        }

        return -1;
    }

    public static boolean hasSpaceHotbar() {
        for (int i = 36; i < 45; i++) {
            final IItemStack stack = mc.getThePlayer().getInventory().getStackInSlot(i);

            if (stack == null)
                return true;
        }

        return false;
    }

    public static int findAutoBlockBlock() {
        for (int i = 36; i < 45; i++) {
            final IItemStack itemStack = mc.getThePlayer().getInventoryContainer().getSlot(i).getStack();

            if (itemStack != null && classProvider.isItemBlock(itemStack.getItem()) && itemStack.getStackSize() > 0) {
                final IItemBlock itemBlock = itemStack.getItem().asItemBlock();
                final IBlock block = itemBlock.getBlock();

                if (block.isFullCube(block.getDefaultState()) && !BLOCK_BLACKLIST.contains(block)
                        && !classProvider.isBlockBush(block))
                    return i;
            }
        }

        for (int i = 36; i < 45; i++) {
            final IItemStack itemStack = mc.getThePlayer().getInventoryContainer().getSlot(i).getStack();

            if (itemStack != null && classProvider.isItemBlock(itemStack.getItem()) && itemStack.getStackSize() > 0) {
                final IItemBlock itemBlock = itemStack.getItem().asItemBlock();
                final IBlock block = itemBlock.getBlock();

                if (!BLOCK_BLACKLIST.contains(block) && !classProvider.isBlockBush(block))
                    return i;
            }
        }

        return -1;
    }

    @EventTarget
    public void onClick(final ClickWindowEvent event) {
        CLICK_TIMER.reset();
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        final IPacket packet = event.getPacket();

        if (classProvider.isCPacketPlayerBlockPlacement(packet))
            CLICK_TIMER.reset();
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
