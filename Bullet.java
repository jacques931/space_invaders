package SpaceInvader;

import com.jogamp.opengl.GL2;

class Bullet {
    private Position position;
    private final int speed;

    public Bullet(Position position) {
        this.position = position;
        this.speed = Setting.bulletSpeed;
    }

    public void draw(GL2 gl) {
        float x = position.getX();
        float y = position.getY();

        gl.glColor3f(1f, 1f, 1f);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2f(x, y);
        gl.glVertex2f(x, y + 10);
        gl.glEnd();
    }

    public boolean move() {
        position.setY(position.getY() + speed);
        return position.getY() > Setting.screenHeight;
    }

    public Position getPosition(){
        return this.position;
    }
}