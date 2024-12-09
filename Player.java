package SpaceInvader;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.io.IOException;

public class Player {
    private Position position;
    private int width;
    private int height;
    private Texture texture;

    public Player(Position position) {
        this.position = position;
        this.width = Setting.playerWidth;
        this.height = Setting.playerHeight;
        loadTexture();
    }

    public void loadTexture() {
        try {
            this.texture = TextureIO.newTexture(new File("src/main/java/SpaceInvader/asset/player.png"), true);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la texture : " + e.getMessage());
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

    public void move(int dx) {
        int newX = position.getX() + dx;
        if (newX < width / 2) newX = width / 2;
        if (newX > Setting.screenWidth - width / 2) newX = Setting.screenWidth - width / 2;
        position.setX(newX);
    }

    public Position getPosition(){
        return this.position;
    }

    public int getHeight(){
        return this.height;
    }
}
