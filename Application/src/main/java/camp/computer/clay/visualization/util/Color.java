package camp.computer.clay.visualization.util;

import java.util.HashMap;
import java.util.Random;

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/visualization/util/Color.java
import camp.computer.clay.visualization.images.PortImage;
=======
import camp.computer.clay.viz.arch.Image;
import camp.computer.clay.viz.img.old_PortImage;
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/viz/util/Color.java

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
    private static HashMap<Image, Integer> colorMap = new HashMap<>();

    public static int getUniqueColor(Image image) {

        if (colorMap.containsKey(image)) {
            return colorMap.get(image);
        }

        for (int i = 0; i < PATH_COLOR_PALETTE.length; i++) {
            if (!colorMap.containsValue(PATH_COLOR_PALETTE[i])) {
                colorMap.put(image, PATH_COLOR_PALETTE[i]);
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
                colorMap.put(image, randomColor);
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
}
