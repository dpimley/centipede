import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Board extends JPanel implements Runnable, Constraints {

    private Dimension d;
    private ArrayList<Centipede> centipedes;
    private Spider spider;
    private Player player;
    private Shot shot;

    private final int SPIDER_INIT_X = 150;
    private final int SPIDER_INIT_Y = 5;
    private final int CENTIPEDE_INIT_X = 150;
    private final int CENTIPEDE_INIT_Y = 5;
    private int direction = -1;
    private int deaths = 0;

    private boolean ingame = true;
    //private final String explImg = "../images/explosion.png";

    private String message = "Game Over";

    private Thread animator;

    public Board(){
        initBoard();
    }

    private void initBoard() {
        TAdapter t_a = new TAdapter();
        addMouseListener(t_a);
        addMouseMotionListener(t_a);
        setFocusable(true);
        d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
        setBackground(Color.BLACK);
        gameInit();
        setDoubleBuffered(true);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        gameInit();
    }

    public void gameInit() {
        centipedes = new ArrayList<>();
        //spider = new Spider();
        player = new Player();
        shot = new Shot();

        if (animator == null || !ingame){
            animator = new Thread(this);
            animator.start();
        }

    }

    public void drawCentipedes(Graphics g) {

    }

    public void drawPlayer(Graphics g) {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {
            player.die();
            ingame = false;
        }
    }

    public void drawShot(Graphics g) {
        if (shot.isVisible()){
            g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.green);

        if (ingame) {
            g.drawLine(0, GROUND, BOARD_WIDTH, GROUND);
            drawCentipedes(g);
            drawPlayer(g);
            drawShot(g);
        }

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    public void gameOver() {
        Graphics g = this.getGraphics();

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - metr.stringWidth(message)) / 2, BOARD_WIDTH / 2);
    }

    public void animationCycle() {
        if (deaths == NUMBER_OF_CENTIPEDES_TO_DESTROY) {
            ingame = false;
            message = "Game won!";
        }

        //player
        player.act();

        //shot
        if (shot.isVisible()) {
            int shotX = shot.getX();
            int shotY = shot.getY();

            for (Centipede centipede: centipedes) {
                int centipedeX = centipede.getX();
                int centipedeY = centipede.getY();

                if (centipede.isVisible() && shot.isVisible()) {
                    if (shotX >= centipedeX && shotX <= (centipedeX + CENTIPEDE_WIDTH)
                        && shotY >= centipedeY && shotY <= (centipedeY + CENTIPEDE_HEIGHT)) {
                        //ImageIcon ii = new ImageIcon(explImg);
                        //centipede.setImage(ii.getImage());
                        centipede.setDying(true);
                        deaths++;
                        shot.die();
                    }
                }
            }

            int y = shot.getY();
            y -= 4;

            if (y < 0) {
                shot.die();
            } else {
                shot.setY(y);
            }
        }

        //centipedes
        for (Centipede centipede: centipedes) {
            int x = centipede.getX();
            if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
                direction = 1;
                Iterator i1 = centipedes.iterator();
                while (i1.hasNext()) {
                    Centipede c = (Centipede) i1.next();
                    c.setY(c.getY() + GO_DOWN);
                }
            }

            if (x <= BORDER_LEFT && direction != -1) {
                direction = 1;
                Iterator i2 = centipedes.iterator();
                while (i2.hasNext()) {
                    Centipede c = (Centipede) i2.next();
                    c.setY(c.getY() + GO_DOWN);
                }
            }
        }

        Iterator it = centipedes.iterator();

        while (it.hasNext()) {
            Centipede centipede = (Centipede) it.next();
            if (centipede.isVisible()) {
                int y = centipede.getY();
                if (y > GROUND - CENTIPEDE_HEIGHT) {
                    ingame = false;
                    message = "Invasion!";
                }
                centipede.act(direction);
            }
        }
    }

    @Override
    public void run() {
        long beforeTime, timeDiff, sleep;
        beforeTime = System.currentTimeMillis();
        while (ingame) {
            repaint();
            animationCycle();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if (sleep < 0) {
                sleep = 2;
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }

            beforeTime = System.currentTimeMillis();
        }
        gameOver();
    }

    private class TAdapter extends MouseAdapter implements MouseMotionListener {
        @Override
        public void mouseMoved(MouseEvent e) {
            player.mouseMoved(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int x = player.getX();
            int y = player.getY();

            if (ingame) {
                if (!shot.isVisible()) {
                    shot = new Shot (x, y);
                }
            }
        }
    }
}
