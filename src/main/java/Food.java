import java.util.Map;

public class Food {
    private float x;
    private float y;
    private float mass = 1;
    private boolean eaten = false;

    private void setRandomPosition() {
        Map<String, Float> m = Util.randomPosition(Util.massToRadius(mass));
        x = m.get("x");
        y = m.get("y");
    }

    public void setEaten(boolean b) {
        eaten = b;
    }

    public boolean isEaten() {
        return eaten;
    }

    public Food() {
        setRandomPosition();
    }

    public float getRadius() { return Util.massToRadius(mass); }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getMass() {
        return mass;
    }

}
