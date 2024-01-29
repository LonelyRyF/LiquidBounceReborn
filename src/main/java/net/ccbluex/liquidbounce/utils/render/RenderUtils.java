package net.ccbluex.liquidbounce.utils.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.api.enums.WDefaultVertexFormats;
import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock;
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity;
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase;
import net.ccbluex.liquidbounce.api.minecraft.client.entity.player.IEntityPlayer;
import net.ccbluex.liquidbounce.api.minecraft.client.render.ITessellator;
import net.ccbluex.liquidbounce.api.minecraft.client.render.IWorldRenderer;
import net.ccbluex.liquidbounce.api.minecraft.renderer.entity.IRenderManager;
import net.ccbluex.liquidbounce.api.minecraft.util.*;
import net.ccbluex.liquidbounce.features.module.modules.render.CustomColor;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.injection.backend.Backend;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public final class RenderUtils extends MinecraftInstance {
    private static final Map<Integer, Boolean> glCapMap = new HashMap<>();
    private static final int[] DISPLAY_LISTS_2D = new int[4];
    public static int deltaTime;


    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc2.displayWidth, mc2.displayHeight, depth);
        }
        return framebuffer;
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc2.displayWidth || framebuffer.framebufferHeight != mc2.displayHeight;
    }

    public static Color getColor(final int color) {
        final int f = color >> 24 & 0xFF;
        final int f2 = color >> 16 & 0xFF;
        final int f3 = color >> 8 & 0xFF;
        final int f4 = color & 0xFF;
        return new Color(f2, f3, f4, f);
    }
    public static void drawSmoothRect(double left, double top, double right, double bottom, int color) {
        GlStateManager.resetColor();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        drawRect(left, top, right, bottom, color);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        drawRect(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color);
        drawRect(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color);
        drawRect(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }
    public static void enable(final int glTarget) {
        GL11.glEnable(glTarget);
    }

    public static void disable(final int glTarget) {
        GL11.glDisable(glTarget);
    }
    public static void start() {
        enable(3042);
        GL11.glBlendFunc(770, 771);
        disable(3553);
        disable(2884);
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void stop() {
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        enable(2884);
        enable(3553);
        disable(3042);
        color(Color.white);
    }

    public static void startSmooth() {
        enable(2881);
        enable(2848);
        enable(2832);
    }

    public static void endSmooth() {
        disable(2832);
        disable(2848);
        disable(2881);
    }
    public static void begin(int glMode) {
        GL11.glBegin(glMode);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void vertex(double x, double y) {
        GL11.glVertex2d(x, y);
    }

    public static void translate(double x, double y) {
        GL11.glTranslated(x, y, 0.0);
    }

    public static void scale(double x, double y) {
        GL11.glScaled(x, y, 1.0);
    }

    public static void rotate(double x, double y, double z, double angle) {
        GL11.glRotated(angle, x, y, z);
    }
    public static void gradientSideways(double x, double y, double width, double height, boolean filled, Color color1, Color color2) {
        start();
        GL11.glShadeModel(7425);
        GlStateManager.disableAlpha();
        if (color1 != null) {
            color(color1);
        }

        begin(filled ? 6 : 1);
        vertex(x, y);
        vertex(x, y + height);
        if (color2 != null) {
            color(color2);
        }

        vertex(x + width, y + height);
        vertex(x + width, y);
        end();
        GlStateManager.enableAlpha();
        GL11.glShadeModel(7424);
        stop();
    }
    public static void gradientSideways(double x, double y, double width, double height, Color color1, Color color2) {
        gradientSideways(x, y, width, height, true, color1, color2);
    }
    public static double getAnimationState2(double animation, double finalState, double speed) {
        float add = (float) (0.01 * speed);
        if (animation < finalState) {
            if (animation + add < finalState)
                animation += add;
            else
                animation = finalState;
        } else {
            if (animation - add > finalState)
                animation -= add;
            else
                animation = finalState;
        }
        return animation;
    }
    public static void drawRoundRect(float d, float e, float g, float h, int color)
    {
        drawRect(d+1, e, g, h, color);
        drawRect(d, e+0.75, d, h, color);
        drawRect(d, e+1, d+1, h-0.5, color);
        drawRect(d-0.75, e+1.5, d, h-1.25, color);
    }
    public static void ArrayListBGGradient(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 230.0F;
        float f1 = (col1 >> 16 & 0xFF) / 230.0F;
        float f2 = (col1 >> 8 & 0xFF) / 230.0F;
        float f3 = (col1 & 0xFF) / 230.0F;
        float f4 = (col2 >> 24 & 0xFF) / 230.0F;
        float f5 = (col2 >> 16 & 0xFF) / 230.0F;
        float f6 = (col2 >> 8 & 0xFF) / 230.0F;
        float f7 = (col2 & 0xFF) / 230.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }
    public static Framebuffer bloomFramebuffer = new Framebuffer(1, 1, false);
    public static void doGlScissor(int x, int y, float width, float height) {
        int scaleFactor = 1;
        float sc = 2;

        while (scaleFactor < sc && mc.getDisplayWidth() / (scaleFactor + 1) >= 320  && mc.getDisplayHeight() / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }

        GL11.glScissor((int) (x * scaleFactor), (int) (mc.getDisplayHeight() - (y + height) * scaleFactor), (int) (width * scaleFactor), (int) (height * scaleFactor));
    }
    public static void setColor(int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }
    //搭路视觉
    public static void drawBlockBox2(final WBlockPos blockPos, final Color color, final boolean outline, final boolean box, final float outlineWidth) {
        final IRenderManager renderManager = mc.getRenderManager();
        final ITimer timer = mc.getTimer();

        final double x = blockPos.getX() - renderManager.getRenderPosX();
        final double y = blockPos.getY() - renderManager.getRenderPosY();
        final double z = blockPos.getZ() - renderManager.getRenderPosZ();

        IAxisAlignedBB axisAlignedBB = classProvider.createAxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        final IBlock block = BlockUtils.getBlock(blockPos);

        if (Backend.MINECRAFT_VERSION_MINOR < 12) {
            block.setBlockBoundsBasedOnState(mc.getTheWorld(), blockPos);
        }

        if (block != null) {
            final IEntityPlayer player = mc.getThePlayer();

            final double posX = player.getLastTickPosX() + (player.getPosX() - player.getLastTickPosX()) * (double) timer.getRenderPartialTicks();
            final double posY = player.getLastTickPosY() + (player.getPosY() - player.getLastTickPosY()) * (double) timer.getRenderPartialTicks();
            final double posZ = player.getLastTickPosZ() + (player.getPosZ() - player.getLastTickPosZ()) * (double) timer.getRenderPartialTicks();

            axisAlignedBB = block.getSelectedBoundingBox(mc.getTheWorld(), mc.getTheWorld().getBlockState(blockPos), blockPos)
                    .expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D)
                    .offset(-posX, -posY, -posZ);
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        enableGlCap(GL_BLEND);
        disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST);
        glDepthMask(false);

        if(box) {
            glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() != 255 ? color.getAlpha() : outline ? 26 : 35);
            drawFilledBox(axisAlignedBB);
        }

        if (outline) {
            glLineWidth(outlineWidth);
            enableGlCap(GL_LINE_SMOOTH);
            glColor(color);

            drawSelectionBoundingBox(axisAlignedBB);
        }

        GlStateManager.resetColor();
        glDepthMask(true);
        resetCaps();
    }
    public static void startDrawing() {
        GL11.glEnable(3042);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        Minecraft.getMinecraft().entityRenderer.setupCameraTransform(Minecraft.getMinecraft().timer.renderPartialTicks, 0);
    }
    public static void stopDrawing() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }
    public static int reAlpha(int color, float alpha) {
        java.awt.Color c = new java.awt.Color(color);
        float r = ((float) 1 / 255) * c.getRed();
        float g = ((float) 1 / 255) * c.getGreen();
        float b = ((float) 1 / 255) * c.getBlue();
        return new java.awt.Color(r, g, b, alpha).getRGB();

    }
    public static void newDrawRect(float left, float top, float right, float bottom, int color)
    {
        if (left < right)
        {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)left, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)top, 0.0D).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static void quickRenderCircle(double x, double y, double start, double end, double w, double h) {
        if (start > end) {
            double temp = end;
            end = start;
            start = temp;
        }

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2d(x, y);
        for(double i = end; i >= start; i-=4) {
            double ldx = Math.cos(i * Math.PI / 180.0) * w;
            double ldy = Math.sin(i * Math.PI / 180.0) * h;
            GL11.glVertex2d(x + ldx, y + ldy);
        }
        GL11.glVertex2d(x, y);
        GL11.glEnd();
    }
    public static void drawCircleRect(float x, float y, float x1, float y1, float radius, int color){
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        glColor(color);

        // Բ��
        quickRenderCircle(x1-radius,y1-radius,0,90,radius,radius);
        quickRenderCircle(x+radius,y1-radius,90,180,radius,radius);
        quickRenderCircle(x+radius,y+radius,180,270,radius,radius);
        quickRenderCircle(x1-radius,y+radius,270,360,radius,radius);

        // ����
        quickDrawRect(x+radius,y+radius,x1-radius,y1-radius);
        quickDrawRect(x,y+radius,x+radius,y1-radius);
        quickDrawRect(x1-radius,y+radius,x1,y1-radius);
        quickDrawRect(x+radius,y,x1-radius,y+radius);
        quickDrawRect(x+radius,y1-radius,x1-radius,y1);

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static int SkyRainbow(int var2, float st, float bright) {
        double v1 = Math.ceil(System.currentTimeMillis() + (long) (var2 * 109)) / 5;
        return Color.getHSBColor((double) ((float) ((v1 %= 360.0) / 360.0)) < 0.5 ? -((float) (v1 / 360.0)) : (float) (v1 / 360.0), st, bright).getRGB();
    }
    public static int getRainbowOpaque(int seconds, float saturation, float brightness, int index) {
        float hue = ((System.currentTimeMillis() + index) % (int) (seconds * 1000)) / (float) (seconds * 1000);
        int color = Color.HSBtoRGB(hue, saturation, brightness);
        return color;
    }
    public static Color skyRainbow(int var2, float bright, float st, double speed) {
        double v1 = Math.ceil((System.currentTimeMillis()/speed) + (var2 * 109L)) / 5;
        return Color.getHSBColor((double) ((float) ((v1 %= 360.0) / 360.0)) < 0.5 ? -((float) (v1 / 360.0)) : (float) (v1 / 360.0), st, bright);
    }

    public static void glColor1(int color) {

    }
    public static Color skyRainbow(int var2, float st, float bright) {
        double v1 = Math.ceil(System.currentTimeMillis() + (long) (var2 * 109)) / 5;
        return Color.getHSBColor((double) ((float) ((v1 %= 360.0) / 360.0)) < 0.5 ? -((float) (v1 / 360.0)) : (float) (v1 / 360.0), st, bright);
    }
    public static void fastRoundedRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, float radius) {
        float z = 0;
        if (paramXStart > paramXEnd) {
            z = paramXStart;
            paramXStart = paramXEnd;
            paramXEnd = z;
        }

        if (paramYStart > paramYEnd) {
            z = paramYStart;
            paramYStart = paramYEnd;
            paramYEnd = z;
        }

        double x1 = (double)(paramXStart + radius);
        double y1 = (double)(paramYStart + radius);
        double x2 = (double)(paramXEnd - radius);
        double y2 = (double)(paramYEnd - radius);

        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);

        glBegin(GL_POLYGON);

        double degree = Math.PI / 180;
        for (double i = 0; i <= 90; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        for (double i = 90; i <= 180; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 180; i <= 270; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 270; i <= 360; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        glEnd();
        glDisable(GL_LINE_SMOOTH);
    }

    public static void autoExhibition(double x, double y, double x1, double y1, double size) {
        rectangleBordered(x, y, x1 + size, y1 + size, 0.5, net.ccbluex.liquidbounce.utils.render.Colors.getColor(90), net.ccbluex.liquidbounce.utils.render.Colors.getColor(0));
        rectangleBordered(x + 1.0, y + 1.0, x1 + size - 1.0, y1 + size - 1.0, 1.0, net.ccbluex.liquidbounce.utils.render.Colors.getColor(90), net.ccbluex.liquidbounce.utils.render.Colors.getColor(61));
        rectangleBordered(x + 2.5, y + 2.5, x1 + size - 2.5, y1 + size - 2.5, 0.5, net.ccbluex.liquidbounce.utils.render.Colors.getColor(61), Colors.getColor(0));
    }
    public static void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor,
                                         int borderColor) {
        RenderUtils.rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        RenderUtils.rectangle(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        RenderUtils.rectangle(x, y, x + width, y1, borderColor);
        GlStateManager.color((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        RenderUtils.rectangle(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        RenderUtils.rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
    }
    public static int Astolfo(int var2, float bright, float st, int index, int offset, float client) {
        double rainbowDelay = Math.ceil(System.currentTimeMillis() + (long) (var2 * index)) / offset;
        return Color.getHSBColor((double) ((float) ((rainbowDelay %= client) / client)) < 0.5 ? -((float) (rainbowDelay / client)) : (float) (rainbowDelay / client), st, bright).getRGB();
    }
    public static int getRainbow(int index, int offset, float bright, float st) {
        float hue = (System.currentTimeMillis() + ((long) offset * index)) % 2000L;
        return Color.getHSBColor((float) (hue /= 2000L), st, bright).getRGB();
    }
    public static void drawGradientSidewaysH(double left, double top, double right, double bottom, int col1, int col2) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);

        quickDrawGradientSidewaysH(left, top, right, bottom, col1, col2);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glShadeModel(GL_FLAT);
    }

    public static void quickDrawGradientSidewaysH(double left, double top, double right, double bottom, int col1, int col2) {
        glBegin(GL_QUADS);

        glColor(col1);
        glVertex2d(left, top);
        glVertex2d(left, bottom);
        glColor(col2);
        glVertex2d(right, bottom);
        glVertex2d(right, top);

        glEnd();
    }
    public static void drawArc(float n, float n2, double n3, final int n4, final int n5, final double n6, final int n7) {
        n3 *= 2.0;
        n *= 2.0f;
        n2 *= 2.0f;
        final float n8 = (n4 >> 24 & 0xFF) / 255.0f;
        final float n9 = (n4 >> 16 & 0xFF) / 255.0f;
        final float n10 = (n4 >> 8 & 0xFF) / 255.0f;
        final float n11 = (n4 & 0xFF) / 255.0f;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        GL11.glLineWidth((float)n7);
        GL11.glEnable(2848);
        GL11.glColor4f(n9, n10, n11, n8);
        GL11.glBegin(3);
        int n12 = n5;
        while (n12 <= n6) {
            GL11.glVertex2d(n + Math.sin(n12 * 3.141592653589793 / 180.0) * n3, n2 + Math.cos(n12 * 3.141592653589793 / 180.0) * n3);
            ++n12;
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
    static {
        for (int i = 0; i < DISPLAY_LISTS_2D.length; i++) {
            DISPLAY_LISTS_2D[i] = glGenLists(1);
        }

        glNewList(DISPLAY_LISTS_2D[0], GL_COMPILE);

        quickDrawRect(-7F, 2F, -4F, 3F);
        quickDrawRect(4F, 2F, 7F, 3F);
        quickDrawRect(-7F, 0.5F, -6F, 3F);
        quickDrawRect(6F, 0.5F, 7F, 3F);

        glEndList();

        glNewList(DISPLAY_LISTS_2D[1], GL_COMPILE);

        quickDrawRect(-7F, 3F, -4F, 3.3F);
        quickDrawRect(4F, 3F, 7F, 3.3F);
        quickDrawRect(-7.3F, 0.5F, -7F, 3.3F);
        quickDrawRect(7F, 0.5F, 7.3F, 3.3F);

        glEndList();

        glNewList(DISPLAY_LISTS_2D[2], GL_COMPILE);

        quickDrawRect(4F, -20F, 7F, -19F);
        quickDrawRect(-7F, -20F, -4F, -19F);
        quickDrawRect(6F, -20F, 7F, -17.5F);
        quickDrawRect(-7F, -20F, -6F, -17.5F);

        glEndList();

        glNewList(DISPLAY_LISTS_2D[3], GL_COMPILE);

        quickDrawRect(7F, -20F, 7.3F, -17.5F);
        quickDrawRect(-7.3F, -20F, -7F, -17.5F);
        quickDrawRect(4F, -20.3F, 7.3F, -20F);
        quickDrawRect(-7.3F, -20.3F, -4F, -20F);

        glEndList();
    }
    public static void drawCircleESP(final IEntity entity, final double rad, final int color, final boolean shade) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        final double x = entity.getLastTickPosX()+ (entity.getPosX() - entity.getLastTickPosX()) * mc.getTimer().getRenderPartialTicks() - (mc.getRenderManager()).getRenderPosX();
        final double y = (entity.getLastTickPosY() + (entity.getPosY() - entity.getLastTickPosY()) * mc.getTimer().getRenderPartialTicks() - (mc.getRenderManager()).getRenderPosY()) + Math.sin(System.currentTimeMillis() / 2E+2) + 1;
        final double z = entity.getLastTickPosZ() + (entity.getPosZ() - entity.getLastTickPosZ()) * mc.getTimer().getRenderPartialTicks() - (mc.getRenderManager()).getRenderPosZ();

        java.awt.Color c;

        for (float i = 0; i < Math.PI * 2; i += Math.PI * 2 / 64.F) {
            final double vecX = x + rad * Math.cos(i);
            final double vecZ = z + rad * Math.sin(i);

            c = ColorUtils.INSTANCE.rainbow();
            //c = new Color(255, 75, 75);
            if (shade) {
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0
                );
                GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0.85F
                );
            }
            GL11.glVertex3d(vecX, y, vecZ);
        }

        GL11.glEnd();
        if (shade) GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor3f(255, 255, 255);
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static void rectangle(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            double var5 = top;
            top = bottom;
            bottom = var5;
        }
        float var11 = (color >> 24 & 0xFF) / 255.0F;
        float var6 = (color >> 16 & 0xFF) / 255.0F;
        float var7 = (color >> 8 & 0xFF) / 255.0F;
        float var8 = (color & 0xFF) / 255.0F;
        ITessellator tessellator = classProvider.getTessellatorInstance();
        IWorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        worldRenderer.begin(7, classProvider.getVertexFormatEnum(WDefaultVertexFormats.POSITION));
        worldRenderer.pos(left, bottom, 0.0D).endVertex();
        worldRenderer.pos(right, bottom, 0.0D).endVertex();
        worldRenderer.pos(right, top, 0.0D).endVertex();
        worldRenderer.pos(left, top, 0.0D).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
    }
    public static int loadGlTexture(BufferedImage bufferedImage){
        int textureId = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, bufferedImage.getWidth(), bufferedImage.getHeight(),
                0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, ImageUtils.readImageToBuffer(bufferedImage));

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        return textureId;
    }
    public static Color getGradientOffset2(Color color1, Color color2, double gident) {
        if (gident > 1.0) {
            double f1 = gident % 1.0;
            int f2 = (int) gident;
            gident = f2 % 2 == 0 ? f1 : 1.0 - f1;
        }
        double f3 = 1.0 - gident;
        int f4 = (int) ((double) color1.getRed() * f3 + (double) color2.getRed() * gident);
        int f5 = (int) ((double) color1.getGreen() * f3 + (double) color2.getGreen() * gident);
        int f6 = (int) ((double) color1.getBlue() * f3 + (double) color2.getBlue() * gident);
        return new Color(f4, f5, f6);
    }

    public static boolean glEnableBlend() {
        final boolean wasEnabled = glIsEnabled(GL_BLEND);

        if (!wasEnabled) {
            glEnable(GL_BLEND);
            glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        }

        return wasEnabled;
    }

    public static void glDrawFramebuffer(final int framebufferTexture, final int width, final int height) {
        // Bind the texture of our framebuffer
        glBindTexture(GL_TEXTURE_2D, framebufferTexture);
        // Disable alpha testing so fading out outline works
        glDisable(GL_ALPHA_TEST);
        // Make sure blend is enabled
        final boolean restore = glEnableBlend();
        // Draw the frame buffer texture upside-down
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 1);
            glVertex2f(0, 0);

            glTexCoord2f(0, 0);
            glVertex2f(0, height);

            glTexCoord2f(1, 0);
            glVertex2f(width, height);

            glTexCoord2f(1, 1);
            glVertex2f(width, 0);
        }
        glEnd();
        // Restore blend
        glRestoreBlend(restore);
        // Restore alpha test
        glEnable(GL_ALPHA_TEST);
    }

    public static void glRestoreBlend(final boolean wasEnabled) {
        if (!wasEnabled) {
            glDisable(GL_BLEND);
        }
    }

    public static void drawTexturedRectWithCustomAlpha(float x, float y, float width, float height, String image, float alpha) {
        glPushMatrix();
        final boolean enableBlend = glIsEnabled(GL_BLEND);
        final boolean disableAlpha = !glIsEnabled(GL_ALPHA_TEST);
        if (!enableBlend) glEnable(GL_BLEND);
        if (!disableAlpha) glDisable(GL_ALPHA_TEST);
        GlStateManager.color(1F, 1F, 1F, alpha);
        minecraft.getTextureManager().bindTexture(new ResourceLocation("shaders/" + image + ".png"));
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        if (!enableBlend) glDisable(GL_BLEND);
        if (!disableAlpha) glEnable(GL_ALPHA_TEST);
        GlStateManager.resetColor();
        glPopMatrix();
    }

    public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        ITessellator tessellator = classProvider.getTessellatorInstance();
        IWorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, classProvider.getVertexFormatEnum(WDefaultVertexFormats.POSITION_TEX));
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + width) * f, (v + height) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + width) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void drawShadowWithCustomAlpha(float x, float y, float width, float height, float alpha) {
        drawTexturedRectWithCustomAlpha(x - 9, y - 9, 9, 9, "paneltopleft", alpha);
        drawTexturedRectWithCustomAlpha(x - 9, y + height, 9, 9, "panelbottomleft", alpha);
        drawTexturedRectWithCustomAlpha(x + width, y + height, 9, 9, "panelbottomright", alpha);
        drawTexturedRectWithCustomAlpha(x + width, y - 9, 9, 9, "paneltopright", alpha);
        drawTexturedRectWithCustomAlpha(x - 9, y, 9, height, "panelleft", alpha);
        drawTexturedRectWithCustomAlpha(x + width, y, 9, height, "panelright", alpha);
        drawTexturedRectWithCustomAlpha(x, y - 9, width, 9, "paneltop", alpha);
        drawTexturedRectWithCustomAlpha(x, y + height, width, 9, "panelbottom", alpha);
    }
    public static void drawShadow(final float x, final float y, final float width, final float height) {
        drawTexturedRect(x - 9.0f, y - 9.0f, 9.0f, 9.0f, "paneltopleft");
        drawTexturedRect(x - 9.0f, y + height, 9.0f, 9.0f, "panelbottomleft");
        drawTexturedRect(x + width, y + height, 9.0f, 9.0f, "panelbottomright");
        drawTexturedRect(x + width, y - 9.0f, 9.0f, 9.0f, "paneltopright");
        drawTexturedRect(x - 9.0f, y, 9.0f, height, "panelleft");
        drawTexturedRect(x + width, y, 9.0f, height, "panelright");
        drawTexturedRect(x, y - 9.0f, width, 9.0f, "paneltop");
        drawTexturedRect(x, y + height, width, 9.0f, "panelbottom");
    }
    public static void drawTexturedRect(final float x, final float y, final float width, final float height, final String image) {
        GL11.glPushMatrix();
        final boolean enableBlend = GL11.glIsEnabled(3042);
        final boolean disableAlpha = !GL11.glIsEnabled(3008);
        if (!enableBlend) {
            GL11.glEnable(3042);
        }
        if (!disableAlpha) {
            GL11.glDisable(3008);
        }
        minecraft.getTextureManager().bindTexture(new ResourceLocation("shaders/" + image + ".png"));
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        if (!enableBlend) {
            GL11.glDisable(3042);
        }
        if (!disableAlpha) {
            GL11.glEnable(3008);
        }
        GL11.glPopMatrix();
    }


    public static java.awt.Color getGradientOffset(java.awt.Color color1, java.awt.Color color2, double offset) {
        double inverse_percent;
        int redPart;
        if (offset > 1.0) {
            inverse_percent = offset % 1.0;
            redPart = (int) offset;
            offset = redPart % 2 == 0 ? inverse_percent : 1.0 - inverse_percent;
        }

        inverse_percent = 1.0 - offset;
        redPart = (int) ((double) color1.getRed() * inverse_percent + (double) color2.getRed() * offset);
        int greenPart = (int) ((double) color1.getGreen() * inverse_percent + (double) color2.getGreen() * offset);
        int bluePart = (int) ((double) color1.getBlue() * inverse_percent + (double) color2.getBlue() * offset);
        return new java.awt.Color(redPart, greenPart, bluePart);
    }

    public static void drawRoundedRect2(float left, float top, float right, float bottom, float radius, int points, int color) {
        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        if (left < right) left = left + right - (right = left);
        if (top < bottom) top = top + bottom - (bottom = top);

        float[][] corners = {
                {right + radius, top - radius, 270},
                {left - radius, top - radius, 360},
                {left - radius, bottom + radius, 90},
                {right + radius, bottom + radius, 180}};

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.color(f, f1, f2, f3);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder renderer = tessellator.getBuffer();
        renderer.begin(GL_POLYGON, DefaultVertexFormats.POSITION);
        for (float[] c : corners) {
            for (int i = 0; i <= points; i++) {
                double anglerad = (Math.PI * (c[2] + i * 90.0F / points) / 180.0f);
                renderer.pos(c[0] + (Math.sin(anglerad) * radius), c[1] + (Math.cos(anglerad) * radius), 0).endVertex();
            }
        }

        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }
    public static void drawRoundedRect2(float x, float y, float width, float height, float radius, int color) {
        float x1 = x + width, // @off
                y1 = y + height;
        final float f = (color >> 24 & 0xFF) / 255.0F,
                f1 = (color >> 16 & 0xFF) / 255.0F,
                f2 = (color >> 8 & 0xFF) / 255.0F,
                f3 = (color & 0xFF) / 255.0F; // @on
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);

        x *= 2;
        y *= 2;
        x1 *= 2;
        y1 *= 2;

        glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(f1, f2, f3, f);
        GlStateManager.enableBlend();
        glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glBegin(GL11.GL_POLYGON);
        final double v = Math.PI / 180;

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x + radius + MathHelper.sin((float) (i * v)) * (radius * -1), y + radius + MathHelper.cos((float) (i * v)) * (radius * -1));
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x + radius + MathHelper.sin((float) (i * v)) * (radius * -1), y1 - radius + MathHelper.cos((float) (i * v)) * (radius * -1));
        }

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x1 - radius + MathHelper.sin((float) (i * v)) * radius, y1 - radius + MathHelper.cos((float) (i * v)) * radius);
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x1 - radius + MathHelper.sin((float) (i * v)) * radius, y + radius + MathHelper.cos((float) (i * v)) * radius);
        }

        GL11.glEnd();

        glEnable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_LINE_SMOOTH);
        glEnable(GL11.GL_TEXTURE_2D);

        GL11.glScaled(2, 2, 2);

        GL11.glPopAttrib();
        GL11.glColor4f(1, 1, 1, 1);
    }
    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.getDisplayWidth() || framebuffer.framebufferHeight != mc.getDisplayHeight()) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.getDisplayWidth(), mc.getDisplayHeight(), true);
        }
        return framebuffer;
    }

    public static void drawOutlinedString(String str, int x, int y, int color, int color2) {
        mc.getFontRendererObj().drawString(str, (int) (x - 1.0F), y, color2);
        mc.getFontRendererObj().drawString(str, (int) (x + 1.0F), y, color2);
        mc.getFontRendererObj().drawString(str, x, (int) (y + 1.0F), color2);
        mc.getFontRendererObj().drawString(str, x, (int) (y - 1.0F), color2);
        mc.getFontRendererObj().drawString(str, x, y, color);
    }

    public static float animate(float target, float current, float speed) {
        boolean larger = (target > current);
        if (speed < 0.0f) speed = 0.0f;
        else if (speed > 1.0f) speed = 1.0f;
        float dif = Math.abs(current - target);
        float factor = dif * speed;
        if (larger) current += factor;
        else current -= factor;
        return current;
    }

    public static boolean isHovered(float x, float y, float w, float h, int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= (x + w) && mouseY >= y && mouseY <= (y + h));
    }

    public static void outlineRect(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        drawRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawRect(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawRect(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawRect(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawRect(double x, double y, double x2, double y2, int color) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glPushMatrix();
        RenderUtils.glColor(new Color(color));
        glBegin(GL_QUADS);
        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;

        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);

        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);

        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawBlockBox(final WBlockPos blockPos, final Color color, final boolean outline) {
        final IRenderManager renderManager = mc.getRenderManager();
        final ITimer timer = mc.getTimer();

        final double x = blockPos.getX() - renderManager.getRenderPosX();
        final double y = blockPos.getY() - renderManager.getRenderPosY();
        final double z = blockPos.getZ() - renderManager.getRenderPosZ();

        IAxisAlignedBB axisAlignedBB = classProvider.createAxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        final IBlock block = BlockUtils.getBlock(blockPos);

        if (block != null) {
            final IEntityPlayer player = mc.getThePlayer();

            final double posX = player.getLastTickPosX() + (player.getPosX() - player.getLastTickPosX()) * (double) timer.getRenderPartialTicks();
            final double posY = player.getLastTickPosY() + (player.getPosY() - player.getLastTickPosY()) * (double) timer.getRenderPartialTicks();
            final double posZ = player.getLastTickPosZ() + (player.getPosZ() - player.getLastTickPosZ()) * (double) timer.getRenderPartialTicks();

            if (Backend.MINECRAFT_VERSION_MINOR < 12) {
                block.setBlockBoundsBasedOnState(mc.getTheWorld(), blockPos);
            }

            axisAlignedBB = block.getSelectedBoundingBox(mc.getTheWorld(), mc.getTheWorld().getBlockState(blockPos), blockPos)
                    .expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D)
                    .offset(-posX, -posY, -posZ);
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        enableGlCap(GL_BLEND);
        disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST);
        glDepthMask(false);

        glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() != 255 ? color.getAlpha() : outline ? 26 : 35);
        drawFilledBox(axisAlignedBB);

        if (outline) {
            glLineWidth(1F);
            enableGlCap(GL_LINE_SMOOTH);
            glColor(color);

            drawSelectionBoundingBox(axisAlignedBB);
        }

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDepthMask(true);
        resetCaps();
    }

    public static void drawSelectionBoundingBox(IAxisAlignedBB boundingBox) {
        ITessellator tessellator = classProvider.getTessellatorInstance();
        IWorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL_LINE_STRIP, classProvider.getVertexFormatEnum(WDefaultVertexFormats.POSITION));

        // Lower Rectangle
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ()).endVertex();
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMaxZ()).endVertex();
        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMaxZ()).endVertex();
        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMinZ()).endVertex();
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ()).endVertex();

        // Upper Rectangle
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMinZ()).endVertex();
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxZ()).endVertex();
        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()).endVertex();
        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMinZ()).endVertex();
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMinZ()).endVertex();

        // Upper Rectangle
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxZ()).endVertex();
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMaxZ()).endVertex();

        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMaxZ()).endVertex();
        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()).endVertex();

        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMinZ()).endVertex();
        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMinZ()).endVertex();

        tessellator.draw();
    }

    public static void drawEntityBox(final IEntity entity, final Color color, final boolean outline) {
        final IRenderManager renderManager = mc.getRenderManager();
        final ITimer timer = mc.getTimer();

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        enableGlCap(GL_BLEND);
        disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST);
        glDepthMask(false);

        final double x = entity.getLastTickPosX() + (entity.getPosX() - entity.getLastTickPosX()) * timer.getRenderPartialTicks()
                - renderManager.getRenderPosX();
        final double y = entity.getLastTickPosY() + (entity.getPosY() - entity.getLastTickPosY()) * timer.getRenderPartialTicks()
                - renderManager.getRenderPosY();
        final double z = entity.getLastTickPosZ() + (entity.getPosZ() - entity.getLastTickPosZ()) * timer.getRenderPartialTicks()
                - renderManager.getRenderPosZ();

        final IAxisAlignedBB entityBox = entity.getEntityBoundingBox();
        final IAxisAlignedBB axisAlignedBB = classProvider.createAxisAlignedBB(
                entityBox.getMinX() - entity.getPosX() + x - 0.05D,
                entityBox.getMinY() - entity.getPosY() + y,
                entityBox.getMinZ() - entity.getPosZ() + z - 0.05D,
                entityBox.getMaxX() - entity.getPosX() + x + 0.05D,
                entityBox.getMaxY() - entity.getPosY() + y + 0.15D,
                entityBox.getMaxZ() - entity.getPosZ() + z + 0.05D
        );

        if (outline) {
            glLineWidth(1F);
            enableGlCap(GL_LINE_SMOOTH);
            glColor(color.getRed(), color.getGreen(), color.getBlue(), 95);
            drawSelectionBoundingBox(axisAlignedBB);
        }

        glColor(color.getRed(), color.getGreen(), color.getBlue(), outline ? 26 : 35);
        drawFilledBox(axisAlignedBB);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDepthMask(true);
        resetCaps();
    }

    public static void drawAxisAlignedBB(final IAxisAlignedBB axisAlignedBB, final Color color) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glLineWidth(2F);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glColor(color);
        drawFilledBox(axisAlignedBB);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);
    }

    public static void drawPlatform(final double y, final Color color, final double size) {
        final IRenderManager renderManager = mc.getRenderManager();
        final double renderY = y - renderManager.getRenderPosY();

        drawAxisAlignedBB(classProvider.createAxisAlignedBB(size, renderY + 0.02D, size, -size, renderY, -size), color);
    }

    public static void drawPlatform(final IEntity entity, final Color color) {
        final IRenderManager renderManager = mc.getRenderManager();
        final ITimer timer = mc.getTimer();

        final double x = entity.getLastTickPosX() + (entity.getPosX() - entity.getLastTickPosX()) * timer.getRenderPartialTicks()
                - renderManager.getRenderPosX();
        final double y = entity.getLastTickPosY() + (entity.getPosY() - entity.getLastTickPosY()) * timer.getRenderPartialTicks()
                - renderManager.getRenderPosY();
        final double z = entity.getLastTickPosZ() + (entity.getPosZ() - entity.getLastTickPosZ()) * timer.getRenderPartialTicks()
                - renderManager.getRenderPosZ();

        final IAxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox()
                .offset(-entity.getPosX(), -entity.getPosY(), -entity.getPosZ())
                .offset(x, y, z);

        drawAxisAlignedBB(
                classProvider.createAxisAlignedBB(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY() + 0.2, axisAlignedBB.getMinZ(), axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY() + 0.26, axisAlignedBB.getMaxZ()),
                color
        );
    }

    public static void drawFilledBox(final IAxisAlignedBB axisAlignedBB) {
        final ITessellator tessellator = classProvider.getTessellatorInstance();
        final IWorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(7, classProvider.getVertexFormatEnum(WDefaultVertexFormats.POSITION));

        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();

        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();

        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();

        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();

        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();

        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        tessellator.draw();
    }

    public static void quickDrawRect(final float x, final float y, final float x2, final float y2) {
        glBegin(GL_QUADS);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();
    }

    public static void drawRect(final float x, final float y, final float x2, final float y2, final int color) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);

        glColor(color);
        glBegin(GL_QUADS);

        glVertex2f(x2, y);
        glVertex2f(x, y);
        glVertex2f(x, y2);
        glVertex2f(x2, y2);
        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
    }
    public static void drawRoundedCornerRect(float x, float y, float x1, float y1, float radius) {
        glBegin(GL_POLYGON);

        float xRadius = (float) Math.min((x1 - x) * 0.5, radius);
        float yRadius = (float) Math.min((y1 - y) * 0.5, radius);
        quickPolygonCircle(x + xRadius,y + yRadius, xRadius, yRadius,180,270,4);
        quickPolygonCircle(x1 - xRadius,y + yRadius, xRadius, yRadius,90,180,4);
        quickPolygonCircle(x1 - xRadius,y1 - yRadius, xRadius, yRadius,0,90,4);
        quickPolygonCircle(x + xRadius,y1 - yRadius, xRadius, yRadius,270,360,4);

        glEnd();
    }
    private static void quickPolygonCircle(float x, float y, float xRadius, float yRadius, int start, int end, int split) {
        for(int i = end; i >= start; i -= split) {
            glVertex2d(x + Math.sin(i * Math.PI / 180.0D) * xRadius, y + Math.cos(i * Math.PI / 180.0D) * yRadius);
        }
    }
    public static void drawRoundedCornerRect(float x, float y, float x1, float y1, float radius, int color) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        final boolean hasCull = glIsEnabled(GL_CULL_FACE);
        glDisable(GL_CULL_FACE);

        glColor(color);
        drawRoundedCornerRect(x, y, x1, y1, radius);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        setGlState(GL_CULL_FACE, hasCull);
    }
    public static void drawRoundedRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, float radius, int color) {
        drawRoundedRect(paramXStart, paramYStart, paramXEnd, paramYEnd, radius, color, true);
    }
    public static boolean isHovering(int mouseX, int mouseY, float xLeft, float yUp, float xRight, float yBottom) {
        return mouseX > xLeft && mouseX < xRight && mouseY > yUp && mouseY < yBottom;
    }


    public static float getAnimationState(float animation, float finalState, float speed) {
        final float add = deltaTime * speed;
        if (animation < finalState) {
            if (animation + add < finalState) {
                animation += add;
            } else {
                animation = finalState;
            }
        } else if (animation - add > finalState) {
            animation -= add;
        } else {
            animation = finalState;
        }
        return animation;
    }
    public static float smoothAnimation(float ani, float finalState, float speed, float scale) {
        return RenderUtils.getAnimationState(ani, finalState, Math.max(10, (Math.abs(ani - finalState)) * speed) * scale);
    }
    public static double getAnimationStateSmooth(double target, double current, double speed) {
        boolean larger = target > current;
        if (speed < 0.0) {
            speed = 0.0;
        } else if (speed > 1.0) {
            speed = 1.0;
        }
        if (target == current) {
            return target;
        }
        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1) {
            factor = 0.1;
        }
        if (larger) {
            if (current + factor > target) {
                current = target;
            } else {
                current += factor;
            }
        } else {
            if (current - factor < target) {
                current = target;
            } else {
                current -= factor;
            }
        }
        return current;
    }

    public static void drawCircle2(float x, float y, float radius, int color) {
        glColor(color);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glPushMatrix();
        glLineWidth(1F);
        glBegin(GL_POLYGON);
        for(int i = 0; i <= 360; i++)
            glVertex2d(x + Math.sin(i * Math.PI / 180.0D) * radius, y + Math.cos(i * Math.PI / 180.0D) * radius);
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_BLEND);
        glColor4f(1F, 1F, 1F, 1F);
    }
    public static void drawRoundedRect3(float left, float top, float right, float bottom, float radius, int points, int color) {
        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        if (left < right) left = left + right - (right = left);
        if (top < bottom) top = top + bottom - (bottom = top);

        float[][] corners = {
                {right + radius, top - radius, 270},
                {left - radius, top - radius, 360},
                {left - radius, bottom + radius, 90},
                {right + radius, bottom + radius, 180}};

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.color(f, f1, f2, f3);

        ITessellator tessellator = classProvider.getTessellatorInstance();
        IWorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(GL_POLYGON, classProvider.getVertexFormatEnum(WDefaultVertexFormats.POSITION_TEX));
        for (float[] c : corners) {
            for (int i = 0; i <= points; i++) {
                double anglerad = (Math.PI * (c[2] + i * 90.0F / points) / 180.0f);
                renderer.pos(c[0] + (Math.sin(anglerad) * radius), c[1] + (Math.cos(anglerad) * radius), 0).endVertex();
            }
        }

        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }
    public static void drawRoundedRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, float radius, int color, boolean popPush) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        float z = 0;
        if (paramXStart > paramXEnd) {
            z = paramXStart;
            paramXStart = paramXEnd;
            paramXEnd = z;
        }

        if (paramYStart > paramYEnd) {
            z = paramYStart;
            paramYStart = paramYEnd;
            paramYEnd = z;
        }

        double x1 = (double)(paramXStart + radius);
        double y1 = (double)(paramYStart + radius);
        double x2 = (double)(paramXEnd - radius);
        double y2 = (double)(paramYEnd - radius);

        if (popPush) glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);

        glColor4f(red, green, blue, alpha);
        glBegin(GL_POLYGON);

        double degree = Math.PI / 180;
        for (double i = 0; i <= 90; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        for (double i = 90; i <= 180; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 180; i <= 270; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 270; i <= 360; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        if (popPush) glPopMatrix();
    }

    public static void drawRect(final int x, final int y, final int x2, final int y2, final int color) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);

        glColor(color);
        glBegin(GL_QUADS);

        glVertex2i(x2, y);
        glVertex2i(x, y);
        glVertex2i(x, y2);
        glVertex2i(x2, y2);
        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
    }

    /**
     * Like {@link #drawRect(float, float, float, float, int)}, but without setup
     */
    public static void quickDrawRect(final float x, final float y, final float x2, final float y2, final int color) {
        glColor(color);
        glBegin(GL_QUADS);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();
    }

    public static void drawRect(final float x, final float y, final float x2, final float y2, final Color color) {
        drawRect(x, y, x2, y2, color.getRGB());
    }

    public static void drawBorderedRect(final float x, final float y, final float x2, final float y2, final float width,
                                        final int color1, final int color2) {
        drawRect(x, y, x2, y2, color2);
        drawBorder(x, y, x2, y2, width, color1);
    }

    public static void drawBorder(float x, float y, float x2, float y2, float width, int color1) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);

        glColor(color1);
        glLineWidth(width);

        glBegin(GL_LINE_LOOP);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
    }

    public static void quickDrawBorderedRect(final float x, final float y, final float x2, final float y2, final float width, final int color1, final int color2) {
        quickDrawRect(x, y, x2, y2, color2);

        glColor(color1);
        glLineWidth(width);

        glBegin(GL_LINE_LOOP);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();
    }

    public static void drawLoadingCircle(float x, float y) {
        for (int i = 0; i < 4; i++) {
            int rot = (int) ((System.nanoTime() / 5000000 * i) % 360);
            drawCircle(x, y, i * 10, rot - 180, rot);
        }
    }
    public static void drawCircle(final IEntity entity, final double rad, final int color, final boolean shade) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        final double x = entity.getLastTickPosX() + (entity.getPosX() - entity.getLastTickPosX()) * mc.getTimer().getRenderPartialTicks() - (mc.getRenderManager()).getRenderPosX();
        final double y = (entity.getLastTickPosY() + (entity.getPosY() - entity.getLastTickPosY()) * mc.getTimer().getRenderPartialTicks() - (mc.getRenderManager()).getRenderPosY()) + Math.sin(System.currentTimeMillis() / 2E+2) + 1;
        final double z = entity.getLastTickPosZ() + (entity.getPosZ() - entity.getLastTickPosZ()) * mc.getTimer().getRenderPartialTicks() - (mc.getRenderManager()).getRenderPosZ();

        Color c;

        for (float i = 0; i < Math.PI * 2; i += Math.PI * 2 / 64.F) {
            final double vecX = x + rad * Math.cos(i);
            final double vecZ = z + rad * Math.sin(i);

            final HUD hud = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);

            c =  RenderUtils.getGradientOffset(
                    new Color(CustomColor.r.get(), CustomColor.g.get(), CustomColor.b.get()),
                    new Color(CustomColor.r2.get(), CustomColor.g2.get(), CustomColor.b2.get()),i
            );

            if (shade) {
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0
                );
                GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0.85F
                );
            }
            GL11.glVertex3d(vecX, y, vecZ);
        }

        GL11.glEnd();
        if (shade) GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor3f(255, 255, 255);
    }
    public static void drawCircle(float x, float y, float radius, int start, int end) {
        classProvider.getGlStateManager().enableBlend();
        classProvider.getGlStateManager().disableTexture2D();
        classProvider.getGlStateManager().tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor(Color.WHITE);

        glEnable(GL_LINE_SMOOTH);
        glLineWidth(2F);
        glBegin(GL_LINE_STRIP);
        for (float i = end; i >= start; i -= (360 / 90.0f)) {
            glVertex2f((float) (x + (cos(i * PI / 180) * (radius * 1.001F))), (float) (y + (sin(i * PI / 180) * (radius * 1.001F))));
        }
        glEnd();
        glDisable(GL_LINE_SMOOTH);

        classProvider.getGlStateManager().enableTexture2D();
        classProvider.getGlStateManager().disableBlend();
    }
    public static void enableSmoothLine(float width) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(width);
    }
    public static void disableSmoothLine() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
    public static void drawFilledCircle(final int xx, final int yy, final float radius, final Color color) {
        int sections = 50;
        double dAngle = 2 * Math.PI / sections;
        float x, y;

        glPushAttrib(GL_ENABLE_BIT);

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i < sections; i++) {
            x = (float) (radius * Math.sin((i * dAngle)));
            y = (float) (radius * Math.cos((i * dAngle)));

            glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
            glVertex2f(xx + x, yy + y);
        }

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        glEnd();

        glPopAttrib();
    }

    public static void drawImage(IResourceLocation image, int x, int y, int width, int height) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(image);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }
    public static void drawImage4(ResourceLocation image, int x, int y, int width, int height) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture2(image);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    /**
     * Draws a textured rectangle at z = 0. Args: x, y, u, v, width, height, textureWidth, textureHeight
     */
    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        ITessellator tessellator = classProvider.getTessellatorInstance();
        IWorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, classProvider.getVertexFormatEnum(WDefaultVertexFormats.POSITION_TEX));
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) width) * f, (v + (float) height) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) width) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void glColor(final int red, final int green, final int blue, final int alpha) {
        GL11.glColor4f(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }

    public static void glColor(final Color color) {
        glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public  static void glColor(final int hex) {
        glColor(hex >> 16 & 0xFF, hex >> 8 & 0xFF, hex & 0xFF, hex >> 24 & 0xFF);
    }

    public static void draw2D(final IEntityLivingBase entity, final double posX, final double posY, final double posZ, final int color, final int backgroundColor) {
        GL11.glPushMatrix();
        GL11.glTranslated(posX, posY, posZ);
        GL11.glRotated(-mc.getRenderManager().getPlayerViewY(), 0F, 1F, 0F);
        GL11.glScaled(-0.1D, -0.1D, 0.1D);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDepthMask(true);

        glColor(color);

        glCallList(DISPLAY_LISTS_2D[0]);

        glColor(backgroundColor);

        glCallList(DISPLAY_LISTS_2D[1]);

        GL11.glTranslated(0, 21 + -(entity.getEntityBoundingBox().getMaxY() - entity.getEntityBoundingBox().getMinY()) * 12, 0);

        glColor(color);
        glCallList(DISPLAY_LISTS_2D[2]);

        glColor(backgroundColor);
        glCallList(DISPLAY_LISTS_2D[3]);

        // Stop render
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        GL11.glPopMatrix();
    }

    public static void draw2D(final WBlockPos blockPos, final int color, final int backgroundColor) {
        final IRenderManager renderManager = mc.getRenderManager();

        final double posX = (blockPos.getX() + 0.5) - renderManager.getRenderPosX();
        final double posY = blockPos.getY() - renderManager.getRenderPosY();
        final double posZ = (blockPos.getZ() + 0.5) - renderManager.getRenderPosZ();

        GL11.glPushMatrix();
        GL11.glTranslated(posX, posY, posZ);
        GL11.glRotated(-mc.getRenderManager().getPlayerViewY(), 0F, 1F, 0F);
        GL11.glScaled(-0.1D, -0.1D, 0.1D);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDepthMask(true);

        glColor(color);

        glCallList(DISPLAY_LISTS_2D[0]);

        glColor(backgroundColor);

        glCallList(DISPLAY_LISTS_2D[1]);

        GL11.glTranslated(0, 9, 0);

        glColor(color);

        glCallList(DISPLAY_LISTS_2D[2]);

        glColor(backgroundColor);

        glCallList(DISPLAY_LISTS_2D[3]);

        // Stop render
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        GL11.glPopMatrix();
    }

    public static void renderNameTag(final String string, final double x, final double y, final double z) {
        final IRenderManager renderManager = mc.getRenderManager();

        glPushMatrix();
        glTranslated(x - renderManager.getRenderPosX(), y - renderManager.getRenderPosY(), z - renderManager.getRenderPosZ());
        glNormal3f(0F, 1F, 0F);
        glRotatef(-mc.getRenderManager().getPlayerViewY(), 0F, 1F, 0F);
        glRotatef(mc.getRenderManager().getPlayerViewX(), 1F, 0F, 0F);
        glScalef(-0.05F, -0.05F, 0.05F);
        setGlCap(GL_LIGHTING, false);
        setGlCap(GL_DEPTH_TEST, false);
        setGlCap(GL_BLEND, true);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        final int width = Fonts.roboto35.getStringWidth(string) / 2;

        drawRect(-width - 1, -1, width + 1, Fonts.roboto35.getFontHeight(), Integer.MIN_VALUE);
        Fonts.roboto35.drawString(string, -width, 1.5F, Color.WHITE.getRGB(), true);

        resetCaps();
        glColor4f(1F, 1F, 1F, 1F);
        glPopMatrix();
    }

    public static void drawLine(final double x, final double y, final double x1, final double y1, final float width) {
        glDisable(GL_TEXTURE_2D);
        glLineWidth(width);
        glBegin(GL_LINES);
        glVertex2d(x, y);
        glVertex2d(x1, y1);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void makeScissorBox(final float x, final float y, final float x2, final float y2) {
        final IScaledResolution scaledResolution = classProvider.createScaledResolution(mc);
        final int factor = scaledResolution.getScaleFactor();
        glScissor((int) (x * factor), (int) ((scaledResolution.getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor), (int) ((y2 - y) * factor));
    }

    /**
     * GL CAP MANAGER
     * <p>
     * TODO: Remove gl cap manager and replace by something better
     */

    public static void resetCaps() {
        glCapMap.forEach(RenderUtils::setGlState);
    }

    public static void enableGlCap(final int cap) {
        setGlCap(cap, true);
    }

    public static void enableGlCap(final int... caps) {
        for (final int cap : caps)
            setGlCap(cap, true);
    }

    public static void disableGlCap(final int cap) {
        setGlCap(cap, true);
    }

    public static void disableGlCap(final int... caps) {
        for (final int cap : caps)
            setGlCap(cap, false);
    }

    public static void setGlCap(final int cap, final boolean state) {
        glCapMap.put(cap, glGetBoolean(cap));
        setGlState(cap, state);
    }

    public static void setGlState(final int cap, final boolean state) {
        if (state)
            glEnable(cap);
        else
            glDisable(cap);
    }

    public static void drawCircle(double x, double y, double radius, float startAngle, float endAngle, int color, float lineWidth) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(3);
        for (int i = (int) ((double) startAngle / 360.0 * 100.0); i <= (int) ((double) endAngle / 360.0 * 100.0); ++i) {
            double angle = Math.PI * 2 * (double) i / 100.0 + Math.toRadians(180.0);
            color(color);
            GL11.glVertex2d(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius);
        }
        GL11.glEnd();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GL11.glDisable(2848);
        GlStateManager.popMatrix();
        GlStateManager.resetColor();
    }

    public static void color(float red, float green, float blue, float alpha) {
        GL11.glColor4f(red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f);
    }

    public static void color(Color color) {
        color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void color(int color) {
        color(color, (float) (color >> 24 & 255) / 255.0F);
    }

    public static void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }


    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        ITessellator tessellator = classProvider.getTessellatorInstance();
        IWorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, classProvider.getVertexFormatEnum(WDefaultVertexFormats.POSITION_TEX));
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) uWidth) * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) uWidth) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void drawShadowWithCustomAlpha(double d, double d1, double d2, double d3, double alpha) {
        drawShadowWithCustomAlpha((float) d, (float) d1, (float) d2, (float) d3, (float) alpha);
    }
}