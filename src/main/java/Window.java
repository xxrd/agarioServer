public class Window {
    private float wCenterX = CONSTANT.DEFAULT_WINDOW_WIDTH / 2;
    private float wCenterY = CONSTANT.DEFAULT_WINDOW_HEIGHT / 2;

    public Window(float width, float height) {
        wCenterX = width;
        wCenterY = height;
    }

    public float getWCenterX() { return wCenterX; }

    public float getWCenterY() {
        return wCenterY;
    }

    public void setWCenterX(float x) {
        this.wCenterX = x;
    }

    public void setWCenterY(float y) {
        this.wCenterY = y;
    }

}