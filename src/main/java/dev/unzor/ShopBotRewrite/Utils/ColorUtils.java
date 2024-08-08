package dev.unzor.ShopBotRewrite.Utils;

import java.awt.*;
import java.util.Random;

public class ColorUtils {
    public static Color getRandomColor(){
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return new Color(r, g, b);
    }

    public static String getRandomStringColor(){
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return String.valueOf(r+g+b);
    }
}
