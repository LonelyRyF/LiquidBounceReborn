package cn.liying.ui;


import cn.liying.utils.animation.SmoothAnimationTimer;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.util.text.ITextComponent;

public class ChatLine extends MinecraftInstance
{
    private final int updateCounterCreated;
    private final ITextComponent lineString;

    public SmoothAnimationTimer posXTimer = new SmoothAnimationTimer(1.0f, 0.4f);
    public SmoothAnimationTimer posYTimer = new SmoothAnimationTimer(1.0f, 0.4f);
    public SmoothAnimationTimer alphaTimer = new SmoothAnimationTimer(1.0f, 0.15f);
    public float tempY = 0;
    public float y = 0;
    private final int chatLineID;
    public boolean a;

    public ChatLine(int p_i45000_1_, ITextComponent p_i45000_2_, int p_i45000_3_)
    {
        this.lineString = p_i45000_2_;
        this.updateCounterCreated = p_i45000_1_;
        this.chatLineID = p_i45000_3_;
    }

    public ITextComponent getChatComponent()
    {
        return this.lineString;
    }

    public int getUpdatedCounter()
    {
        return this.updateCounterCreated;
    }

    public int getChatLineID()
    {
        return this.chatLineID;
    }
}
