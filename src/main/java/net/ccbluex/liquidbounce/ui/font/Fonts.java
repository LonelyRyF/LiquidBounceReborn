package net.ccbluex.liquidbounce.ui.font;

import net.ccbluex.liquidbounce.api.minecraft.client.gui.IFontRenderer;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;

public class Fonts extends MinecraftInstance {

    @FontDetails(fontName = "Minecraft Font")
    public static final IFontRenderer minecraftFont = mc.getFontRendererObj();
    @FontDetails(fontName = "Roboto Medium", fontSize = 18)
    public static IFontRenderer roboto35;
    @FontDetails(fontName = "Roboto Medium", fontSize = 20)
    public static IFontRenderer roboto40;
    @FontDetails(fontName = "flux", fontSize = 18)
    public static IFontRenderer flux;
    @FontDetails(fontName = "flux", fontSize = 23)
    public static IFontRenderer flux45;
    @FontDetails(fontName = "Roboto Bold", fontSize = 90)
    public static IFontRenderer robotoBold180;
    @FontDetails(fontName = "Product Sans", fontSize = 18)
    public static IFontRenderer productSans35;
    @FontDetails(fontName = "Product Sans", fontSize = 20)
    public static IFontRenderer productSans40;
    @FontDetails(fontName = "Product Sans", fontSize = 37)
    public static IFontRenderer productSans70;
    @FontDetails(fontName = "Product Sans", fontSize = 40)
    public static IFontRenderer productSans80;
    @FontDetails(fontName = "Notification Icon", fontSize = 35)
    public static IFontRenderer notificationIcon70;
    @FontDetails(fontName = "Tenacity", fontSize = 40)
    public static IFontRenderer tenacity80;
    @FontDetails(fontName = "Tenacity", fontSize = 20)
    public static IFontRenderer tenacity40;
    @FontDetails(fontName = "Tenacitybold", fontSize = 15)
    public static IFontRenderer tenacitybold30;
    @FontDetails(fontName = "Tenacitybold", fontSize = 18)
    public static IFontRenderer tenacitybold35;
    @FontDetails(fontName = "Tenacitybold", fontSize = 20)
    public static IFontRenderer tenacitybold40;
    @FontDetails(fontName = "Tenacitybold", fontSize = 21)
    public static IFontRenderer tenacitybold43;
    @FontDetails(fontName = "Tenacitycheck", fontSize = 30)
    public static IFontRenderer tenacitycheck60;
    @FontDetails(fontName = "Posterama", fontSize = 15)
    public static IFontRenderer posterama30;
    @FontDetails(fontName = "Posterama", fontSize = 18)
    public static IFontRenderer posterama35;
    @FontDetails(fontName = "Posterama", fontSize = 20)
    public static IFontRenderer posterama40;
    @FontDetails(fontName = "NB", fontSize = 18)
    public static IFontRenderer nbicon18;
    @FontDetails(fontName = "NB", fontSize = 20)
    public static IFontRenderer nbicon20;

    @FontDetails(fontName = "Roboto Medium", fontSize = 13)
    public static IFontRenderer font25;
    @FontDetails(fontName = "Roboto Medium", fontSize = 15)
    public static IFontRenderer font30;
    @FontDetails(fontName = "Roboto Medium", fontSize = 18)
    public static IFontRenderer font35;
    @FontDetails(fontName = "Roboto Medium", fontSize = 20)
    public static IFontRenderer font40;

    @FontDetails(fontName = "Roboto Medium", fontSize = 22)
    public static IFontRenderer font43;
    public static void loadFonts() {
        long l = System.currentTimeMillis();

        ClientUtils.getLogger().info("Loading Fonts.");
        font25 = getFont("sfui.ttf", 13);
        font30 = getFont("sfui.ttf", 15);
        font35 = getFont("sfui.ttf", 18);
        font40 = getFont("sfui.ttf", 20);
        font43 = getFont("sfui.ttf", 22);
        roboto35 = getFont("roboto-medium.ttf", 18);
        roboto40 = getFont("roboto-medium.ttf", 20);
        robotoBold180 = getFont("roboto-bold.ttf", 90);
        productSans35 = getFont("product-sans.ttf", 18);
        productSans40 = getFont("product-sans.ttf", 20);
        productSans70 = getFont("product-sans.ttf", 37);
        productSans80 = getFont("product-sans.ttf", 40);
        notificationIcon70 = getFont("notification-icon.ttf", 35);
        posterama30 = getFont("posterama.ttf", 15);
        posterama35 = getFont("posterama.ttf", 18);
        posterama40 = getFont("posterama.ttf", 20);
        tenacity40 = getFont("tenacitybold.ttf", 40);
        tenacity80 = getFont("tenacitybold.ttf", 40);
        tenacitybold30 = getFont("tenacitybold.ttf", 15);
        tenacitybold35 = getFont("tenacitybold.ttf", 18);
        tenacitybold40 = getFont("tenacitybold.ttf", 20);
        tenacitybold43 = getFont("tenacitybold.ttf", 21);
        tenacitycheck60 = getFont("tenacitycheck.ttf", 30);
        flux = getFont("fluxicon.ttf", 18);
        flux45 = getFont("fluxicon.ttf", 23);

        nbicon18 = getFont("newicon.ttf", 18);
        nbicon20 = getFont("newicon.ttf", 23);
        ClientUtils.getLogger().info("Loaded Fonts. (" + (System.currentTimeMillis() - l) + "ms)");
    }

    public static IFontRenderer getFontRenderer(final String name, final int size) {
        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                Object o = field.get(null);

                if (o instanceof IFontRenderer) {
                    FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    if (fontDetails.fontName().equals(name) && fontDetails.fontSize() == size)
                        return (IFontRenderer) o;
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return getFont("default", 35);
    }

    public static FontInfo getFontDetails(final IFontRenderer fontRenderer) {
        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                final Object o = field.get(null);

                System.out.println(field.get(null) == null);

                if (o.equals(fontRenderer)) {
                    final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    return new FontInfo(fontDetails.fontName(), fontDetails.fontSize());
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public static List<IFontRenderer> getFonts() {
        final List<IFontRenderer> fonts = new ArrayList<>();

        for (final Field fontField : Fonts.class.getDeclaredFields()) {
            try {
                fontField.setAccessible(true);

                final Object fontObj = fontField.get(null);

                if (fontObj instanceof IFontRenderer) fonts.add((IFontRenderer) fontObj);
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return fonts;
    }

    private static IFontRenderer getFont(final String fontName, final int size) {
        Font font;
        try {
            final InputStream inputStream = minecraft.getResourceManager().getResource(new ResourceLocation("lb/font/" + fontName)).getInputStream();
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            font = awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            font = new Font("default", Font.PLAIN, size);
        }

        return classProvider.wrapFontRenderer(new GameFontRenderer(font));
    }

    public static class FontInfo {
        private final String name;
        private final int fontSize;

        public FontInfo(String name, int fontSize) {
            this.name = name;
            this.fontSize = fontSize;
        }

        public FontInfo(Font font) {
            this(font.getName(), font.getSize());
        }

        public String getName() {
            return name;
        }

        public int getFontSize() {
            return fontSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FontInfo fontInfo = (FontInfo) o;

            if (fontSize != fontInfo.fontSize) return false;
            return Objects.equals(name, fontInfo.name);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + fontSize;
            return result;
        }
    }
}