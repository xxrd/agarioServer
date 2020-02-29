import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

public class Player {
    protected float x;
    protected float y;
    protected float mass = CONSTANT.DEFAULT_PLAYER_MASS;
    protected float maxMass = CONSTANT.MAX_PLAYER_MASS;
    protected float minMass = CONSTANT.MIN_PLAYER_MASS;
    protected float speed = CONSTANT.DEFAULT_SPEED;
    protected float maxSpeed = CONSTANT.MAX_PLAYER_SPEED;
    protected float minSpeed = CONSTANT.MIN_PLAYER_SPEED;
    protected Window window = new Window(CONSTANT.DEFAULT_WINDOW_WIDTH/2, CONSTANT.DEFAULT_WINDOW_HEIGHT / 2);
    protected Mouse mouse = new Mouse();
    protected String name = "";
    protected int id = 0;

    private void setRandomPosition() {
        Map<String, Float> m = Util.randomPosition(Util.massToRadius(mass));
        x = m.get("x");
        y = m.get("y");
    }

    public Player() {
        setRandomPosition();
        mouse.setX(x);
        mouse.setY(y);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRadius() {
        return Util.massToRadius(mass);
    }

    public float getX() {
        return x;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public float getY() { return y; }

    public float getMass() {
        return mass;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public void update(proto.ClientMessageOuterClass.ClientMessage cm) {
        if(cm.getClientInformation().getMousePosition().hasX()) mouse.setX(cm.getClientInformation().getMousePosition().getX());
        if(cm.getClientInformation().getMousePosition().hasY()) mouse.setY(cm.getClientInformation().getMousePosition().getY());
        if(cm.getClientInformation().getWindowCenterPosition().hasX()) window.setWCenterX(cm.getClientInformation().getWindowCenterPosition().getX());
        if(cm.getClientInformation().getWindowCenterPosition().hasY()) window.setWCenterY(cm.getClientInformation().getWindowCenterPosition().getY());
        updatePlayerCoords();
    }

    public void setWindowCenter(float x, float y) {
        window.setWCenterX(x);
        window.setWCenterY(y);
    }

    public void setMouseCoords(float x, float y) {
        mouse.setX(x);
        mouse.setY(y);
    }

    public boolean intersect(final Food food) {
        float intersectionDistance = getRadius() + food.getRadius();
        float distance = Util.vectorLength(food.getX() - getX(), food.getY() - getY());
        return distance <= intersectionDistance;
    }

    public boolean intersect(final Player player) {
        float intersectionDistance = getRadius() + player.getRadius();
        float distance = Util.vectorLength(player.getX() - getX(), player.getY() - getY());
        return distance <= intersectionDistance;
    }

    public void eatFood(FoodList fl) {
        ArrayList<Food> foodList = fl.getList();
        ListIterator<Food> iter = foodList.listIterator(0);

        while(iter.hasNext()) {
            Food f = iter.next();
            if(f.isEaten()) continue;
            if(intersect(f)) {
                changeMass(1);
                f.setEaten(true);
                fl.changeNotEatenFoodCount(-1);
            }
        }
    }

    public void updatePlayerCoords() {
        float centerWindowX = window.getWCenterX();
        float centerWindowY = window.getWCenterY();

        Vector2D v = new Vector2D(mouse.getX() - centerWindowX, centerWindowY - mouse.getY());
        float length = v.getLength();
        v.normalize(1);

        float moveX;
        float moveY;

        if (length - getRadius() < 0) {
            moveX = v.x * getSpeed() / getRadius() * length;
            moveY = v.y * getSpeed() / getRadius() * length;
        }
        else {
            moveX = v.x * getSpeed();
            moveY = v.y * getSpeed();
        }

        float a1 = getX() - getRadius();
        float b1 = getX() + getRadius();

        float a2 = getY() - getRadius();
        float b2 = getY() + getRadius();

        if (a1 < 0) {
            if (moveX > 0) {
                x += moveX;
            }
        }
        else if (b1 > CONSTANT.GAME_WIDTH) {
            if (moveX < 0) {
                x += moveX;
            }
        }
        else if (a1 + moveX > 0 && b1 + moveX < CONSTANT.GAME_WIDTH) {
            x += moveX;
        }

        if (a2 < 0) {
            if (moveY < 0) {
                y += -moveY;
            }
        }
        else if (b2 > CONSTANT.GAME_HEIGHT) {
            if (moveY > 0) {
                y += -moveY;
            }
        }
        else if (a2 - moveY > 0 && b2 - moveY < CONSTANT.GAME_WIDTH) {
            y += -moveY;
        }

    }

    public void changeMass(float n) {
        if (mass + n > maxMass || mass + n < minMass) return;
        mass = mass + n;
        if (mass > minSpeed && mass < maxMass) speed = speed - (maxSpeed / maxMass * n);
    }

    public float getSpeed() {
        return speed;
    }

}