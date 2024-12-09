package SpaceInvader;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SpaceInvaders implements GLEventListener, KeyListener {
    private Player player;
    private EnemyManager enemyManager;
    private BulletManager bulletManager;

    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean shooting = false;
    private long lastShootTime = 0;

    private enum GameState {
        START, PLAYING, GAMEOVER, END
    }

    private GameState currentState;
    private final GLUT glut = new GLUT();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Invaders");
        GLCanvas canvas = new GLCanvas();

        SpaceInvaders game = new SpaceInvaders();
        canvas.addGLEventListener(game);
        canvas.addKeyListener(game);

        frame.add(canvas);
        frame.setSize(Setting.screenWidth, Setting.screenHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        canvas.requestFocus();

        FPSAnimator animator = new FPSAnimator(canvas, 60, true);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        this.startGame();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        switch (currentState) {
            case START:
                drawStartPage(gl);
                break;
            case PLAYING:
                drawPlayingPage(gl);
                break;
            case GAMEOVER:
                drawGameOverPage(gl);
                break;
            case END:
                drawEndPage(gl);
                break;
        }
    }

    private void drawStartPage(GL2 gl) {
        String mainText = "Press ENTER to Start";
        int mainTextWidth = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_18, mainText);
        float mainX = (Setting.screenWidth - mainTextWidth) / 2.0f;
        float mainY = Setting.screenHeight / 2.0f + 50;

        String[] instructions = {
                "Left / Right : Move",
                "Space : Attack"
        };

        gl.glColor3f(1f, 1f, 1f);
        gl.glRasterPos2f(mainX, mainY);
        glutPrint(mainText);

        // Dessiner chaque ligne d'instructions
        float instructionY = mainY - 30;
        for (String line : instructions) {
            int lineWidth = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_18, line);
            float lineX = (Setting.screenWidth - lineWidth) / 2.0f;
            gl.glRasterPos2f(lineX, instructionY);
            glutPrint(line);
            // Espacement entre les lignes
            instructionY -= 30;
        }
    }

    private void drawPlayingPage(GL2 gl) {
        if (moveLeft) player.move(-Setting.playerSpeed);
        if (moveRight) player.move(Setting.playerSpeed);

        long currentTime = System.currentTimeMillis();
        if (shooting && currentTime - lastShootTime > Setting.shootCooldown) {
            Position bulletPosition = new Position(player.getPosition().getX(), player.getPosition().getY() + player.getHeight());
            bulletManager.shoot(bulletPosition);
            lastShootTime = currentTime;
        }

        player.draw(gl);
        enemyManager.draw(gl);
        bulletManager.draw(gl);

        enemyManager.update();
        bulletManager.update();
        bulletManager.checkCollisions(enemyManager);

        // Vérifiez si tous les ennemis sont morts
        if (enemyManager.areAllEnemiesDead()) {
            currentState = GameState.END;
        }

        // Vérifiez si les ennemis ont atteint le bas de l'écran
        if (enemyManager.isGameOver()) {
            currentState = GameState.GAMEOVER;
        }
    }

    private void drawGameOverPage(GL2 gl) {
        String text = "Game Over! You Lost.";
        int textWidth = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_18, text);
        float x = (Setting.screenWidth - textWidth) / 2.0f;
        float y = Setting.screenHeight / 2.0f;

        gl.glColor3f(1f, 0f, 0f);
        gl.glRasterPos2f(x, y);
        glutPrint(text);
    }

    private void drawEndPage(GL2 gl) {
        String text = "Congratulations! You Won!";
        int textWidth = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_18, text);
        float x = (Setting.screenWidth - textWidth) / 2.0f;
        float y = Setting.screenHeight / 2.0f;

        gl.glColor3f(0f, 1f, 0f);
        gl.glRasterPos2f(x, y);
        glutPrint(text);
    }

    private void glutPrint(String text) {
        for (char c : text.toCharArray()) {
            glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, c);
        }
    }

    private void startGame() {
        currentState = GameState.START;
        player = new Player(new Position(Setting.screenWidth/2, 50));
        enemyManager = new EnemyManager(Setting.enemyRow, Setting.enemyCol);
        bulletManager = new BulletManager();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, Setting.screenWidth, 0, Setting.screenHeight, -1, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (currentState == GameState.START && e.getKeyCode() == KeyEvent.VK_ENTER) {
            currentState = GameState.PLAYING;
        } else if (currentState == GameState.PLAYING) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                moveLeft = true;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                moveRight = true;
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                shooting = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            moveLeft = false;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            moveRight = false;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            shooting = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}