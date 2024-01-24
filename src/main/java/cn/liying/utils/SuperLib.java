package cn.liying.utils;

import java.awt.*;

public class SuperLib {
    public static String removeColorCode(String text) {
        String finalText = text;
        if (text.contains("§")) {
            for (int i = 0; i < finalText.length(); ++i) {
                if (Character.toString(finalText.charAt(i)).equals("§")) {
                    try {
                        String part1 = finalText.substring(0, i);
                        String part2 = finalText.substring(Math.min(i + 2, finalText.length()));
                        finalText = part1 + part2;
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        return finalText;
    }

    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569F * (float) c.getRed();
        float g = 0.003921569F * (float) c.getGreen();
        float b = 0.003921569F * (float) c.getBlue();
        return (new Color(r, g, b, alpha)).getRGB();
    }
}
