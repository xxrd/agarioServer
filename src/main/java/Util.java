import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Util {

    public static float getDistance(Food p1, Food p2) {
        return (float) Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2)) - p1.getRadius() - p2.getRadius();
    }

    public static float massToRadius(float mass) {
        return  4 + (float) Math.sqrt(mass) * 6;
    }

    public static float randomInRange(float min, float max) {
        Random rand = new Random();
        return  min + rand.nextFloat() * (max - min);
    }

    public static int randomInRange(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static float vectorLength(float x, float y) {
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public static Map<String, Float> randomPosition(float radius) {
        Map<String, Float> m = new HashMap<String, Float>();
        m.put("x", randomInRange(radius, CONSTANT.GAME_WIDTH - radius));
        m.put("y", randomInRange(radius, CONSTANT.GAME_HEIGHT - radius));
        return m;
    }

    public static Map<String, Float> normalize(float x, float y, float minLength) {
        Map<String, Float> m = new HashMap<String, Float>();
        float len = vectorLength(x, y);
        len = len < minLength ? minLength : len;
        float l = 1.0f / len;
        m.put("x", x * l);
        m.put("y", y * l);
        return m;
    }

}
