package SpaceInvader;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.io.IOException;

public class Enemy {
    private Position position;
    private int width;
    private int height;
    private Texture texture;

    public Enemy(Position position) {
        this.position = position;
        this.width = Setting.enemyWidth;
        this.height = Setting.enemyHeight;
        loadTexture();
    }

    public void loadTexture() {
        try {
            this.texture = TextureIO.newTexture(new File("src/main/java/SpaceInvader/asset/enemy.png"), true);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la texture Enemy : " + e.getMessage());
            System.exit(1);
        }
    }

    public void draw(GL2 gl) {
        // Activer la texture
        texture.enable(gl);
        texture.bind(gl);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

        // Réinitialiser la couleur pour éviter les effets de couleur
        gl.glColor3f(1f, 1f, 1f);

        float x = position.getX();
        float y = position.getY();

        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0, 0);
        gl.glVertex2f(x - width / 2, y);
        gl.glTexCoord2f(1, 0);
        gl.glVertex2f(x + width / 2, y);
        gl.glTexCoord2f(1, 1);
        gl.glVertex2f(x + width / 2, y + height);
        gl.glTexCoord2f(0, 1);
        gl.glVertex2f(x - width / 2, y + height);
        gl.glEnd();

        // Désactiver la texture après le rendu
        texture.disable(gl);
    }

    public void move(int direction) {
        position.setX(position.getX() + direction);
    }

    public void descend() {
        position.setY(position.getY() - 20);
    }

    public boolean isHit(Bullet bullet) {
        float bulletX = bullet.getPosition().getX();
        float bulletY = bullet.getPosition().getY();

        // Vérifier si le projectile touche cet ennemi
        float x = position.getX();
        float y = position.getY();
        return bulletX > x - width / 2 && bulletX < x + width / 2 &&
                bulletY > y && bulletY < y + height;
    }

    public Position getPosition(){
        return this.position;
    }

    public int getWidth(){
        return this.width;
    }
}

