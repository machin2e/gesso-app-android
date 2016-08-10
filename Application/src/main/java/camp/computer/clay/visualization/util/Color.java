package camp.computer.clay.visualization.util;

import java.util.HashMap;
import java.util.Random;

import camp.computer.clay.visualization.image.PortImage;

public abstract class Color {

    public static int[] PATH_COLOR_PALETTE = new int[] {
            android.graphics.Color.parseColor("#19B5FE"),
            android.graphics.Color.parseColor("#2ECC71"),
            android.graphics.Color.parseColor("#F22613"),
            android.graphics.Color.parseColor("#F9690E"),
            android.graphics.Color.parseColor("#9A12B3"),
            android.graphics.Color.parseColor("#F9BF3B"),
            android.graphics.Color.parseColor("#DB0A5B"),
            android.graphics.Color.parseColor("#BF55EC"),
            android.graphics.Color.parseColor("#A2DED0"),
            android.graphics.Color.parseColor("#1E8BC3"),
            android.graphics.Color.parseColor("#36D7B7"),
            android.graphics.Color.parseColor("#EC644B")
    };
    private static HashMap<PortImage, Integer> colorMap = new HashMap<PortImage, Integer>();

    public static int getUniqueColor(PortImage portSprite) {

        if (colorMap.containsKey(portSprite)) {
            return colorMap.get(portSprite);
        }

        for (int i = 0; i < PATH_COLOR_PALETTE.length; i++) {
            if (!colorMap.containsValue(PATH_COLOR_PALETTE[i])) {
                colorMap.put(portSprite, PATH_COLOR_PALETTE[i]);
                return PATH_COLOR_PALETTE[i];
            }
        }

        Random random = new Random();
        while (true) {
            int red = 30 + random.nextInt(225);
            int green = 30 + random.nextInt(225);
            int blue = 30 + random.nextInt(225);
            int randomColor = android.graphics.Color.rgb(red, green, blue);
            if (!colorMap.containsValue(randomColor)) {
                colorMap.put(portSprite, randomColor);
                return randomColor;
            }
        }
    }

    public static int setTransparency(int color, float factor) {
        int alpha = Math.round(android.graphics.Color.alpha(color) * factor);
        int red = android.graphics.Color.red(color);
        int green = android.graphics.Color.green(color);
        int blue = android.graphics.Color.blue(color);
        return android.graphics.Color.argb(alpha, red, green, blue);
    }

    public static String getHexColorString(int color) {
        return String.format("#%06X", (0xFFFFFFFF & color));
    }
}
