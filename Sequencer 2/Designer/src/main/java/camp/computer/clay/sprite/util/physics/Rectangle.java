package camp.computer.clay.sprite.util.physics;

public class Rectangle {
    public float x;
    public float y;
    public float width;
    public float height;
    public Rectangle (float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
//        public Rectangle fromLTRB(float left, float top, float right, float bottom) {
//            this.x = left;
//            this.y = top;
//            this.width = right - left;
//            this.height = bottom - top;
//        }
}
