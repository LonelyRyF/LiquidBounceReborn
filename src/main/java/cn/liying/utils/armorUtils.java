package cn.liying.utils;

import net.minecraft.item.ItemStack;

import java.awt.*;

public class armorUtils {
    public ItemStack Armor;
    public int Damage;
    public Color color;
    public Color color2;
    public armorUtils(ItemStack armor, int damage, Color color,Color color2){
        this.Armor=armor;
        this.Damage=damage;
        this.color=color;
        this.color2=color2;
    }
}
