import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.event.MouseMotionListener;
import java.lang.reflect.Array;
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
    private ArrayList<Shot> shots;
    private ArrayList<Mushroom> mushrooms;

    private final int SPIDER_INIT_X = 0;
    private final int SPIDER_INIT_Y = GROUND;
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
        drawInitCentipede();
        mushrooms = new ArrayList<>();
        drawInitMushrooms();
        spider = new Spider(SPIDER_INIT_X, SPIDER_INIT_Y);
        player = new Player();
        shots = new ArrayList<>();

        if (animator == null || !ingame){
            animator = new Thread(this);
            animator.start();
        }

    }

    public void drawInitCentipede() {
        for (int i = (BOARD_WIDTH - CENTIPEDE_WIDTH); i >= (BOARD_WIDTH - CENTIPEDE_WIDTH) - (CENTIPEDE_WIDTH * NUMBER_OF_CENTIPEDES_TO_DESTROY); i -= CENTIPEDE_WIDTH) {
            centipedes.add(new Centipede(i, 0));
        }
    }

    public void drawInitMushrooms() {
        Random rand = new Random();
        int chance = 0;
        int step_size = MUSHROOM_WIDTH;
        for (int i = MUSHROOM_START; i < GROUND; i += step_size) {
            for (int j = 0; j < (BOARD_WIDTH - MUSHROOM_WIDTH); j += step_size) {
                chance = rand.nextInt(MUSHROOM_CHANCE) + 1;
                if (chance == 1 && checkMushroom(j, i, step_size)) {
                    Mushroom _tmp = new Mushroom(j, i);
                    mushrooms.add(_tmp);
                }
            }
        }
    }

    public boolean checkMushroom(int x, int y, int step_size) {

        if (x == 0 || x >= (BOARD_WIDTH - (2 * MUSHROOM_WIDTH))) {
            return false;
        }

        for (Mushroom mushroom: mushrooms) {
            if ((mushroom.getX() + step_size == x) && (mushroom.getY() + step_size == y)) {
                return false;
            }

            if ((mushroom.getX() - step_size == x) && (mushroom.getY() + step_size == y)) {
                return false;
            }
        }
        return true;
    }

    public void drawMushrooms(Graphics g) {
        for (Mushroom mushroom: mushrooms) {
            if (mushroom.isVisible()) {
                g.drawImage(mushroom.getImage(), mushroom.getX(), mushroom.getY(), this);
            }

            if (mushroom.isDying()) {
                mushroom.die();
            }
        }
    }

    public void drawSpider(Graphics g) {
        if (spider.isVisible()) {
            g.drawImage(spider.getImage(), spider.getX(), spider.getY(), this);
        }

        if (spider.isDying()) {
            spider.die();
            spider = new Spider(SPIDER_INIT_X, SPIDER_INIT_Y);
        }
    }

    public void drawCentipedes(Graphics g) {
        for (Centipede centipede: centipedes) {
            if (centipede.isVisible()) {
                g.drawImage(centipede.getImage(), centipede.getX(), centipede.getY(), this);
            }

            if (centipede.isDying()) {
                centipede.die();
            }
        }
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
        for (Shot shot: shots){
            if (shot.isVisible()){
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
            // TODO Remove shot from array list when it's not visible
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.blue);

        if (ingame) {
            g.drawLine(0, GROUND, BOARD_WIDTH, GROUND);
            drawCentipedes(g);
            drawSpider(g);
            drawMushrooms(g);
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
        for (Shot shot: shots){
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

                for (Mushroom mushroom: mushrooms) {
                    int mushroomX = mushroom.getX();
                    int mushroomY = mushroom.getY();

                    if (mushroom.isVisible() && shot.isVisible()) {
                        if (shotX >= mushroomX && shotX <= (mushroomX + MUSHROOM_WIDTH)
                                && shotY >= mushroomY && shotY <= (mushroomY + MUSHROOM_HEIGHT)) {
                            mushroom.switchState();
                            shot.die();
                        }
                    }
                }

                if (spider.isVisible() && shot.isVisible()) {
                    if (shotX >= spider.getX() && shotX <= (spider.getX() + SPIDER_WIDTH)
                                && shotY >= spider.getY() && shotY <= (spider.getY() + SPIDER_HEIGHT)) {
                        spider.setDying(true);
                        shot.die();
                    }
                }

                int y = shot.getY();
                y -= SHOT_SPEED;

                if (y < 0) {
                    shot.die();
                } else {
                    shot.setY(y);
                }
            }
        }

        //spider
        spider.setTarget();
        spider.act();

        //centipedes
        Iterator itr_centipede = centipedes.iterator();
        while (itr_centipede.hasNext()) {
            Centipede centipede = (Centipede) itr_centipede.next();
            if (centipede.isVisible()) {
                int c_x = centipede.getX();
                int c_y = centipede.getY();

                Iterator itr_mushroom = mushrooms.iterator();
                while (itr_mushroom.hasNext()) {
                    Mushroom mushroom = (Mushroom) itr_mushroom.next();
                    if (c_y == mushroom.getY() && c_x <= (mushroom.getX() + MUSHROOM_WIDTH) && (c_x + CENTIPEDE_WIDTH) >= mushroom.getX()) {
                        centipede.setY(c_y + CENTIPEDE_HEIGHT);
                    }
//                    else if (c_y == mushroom.getY() && (c_x + CENTIPEDE_WIDTH) >= mushroom.getX() && c_x < && centipede.cur_direction == CENTIPEDE_SPEED) {
//                        centipede.setY(c_y + CENTIPEDE_HEIGHT);
//                    }
                }

                if (c_x + CENTIPEDE_WIDTH > BOARD_WIDTH) {
                    centipede.setY(c_y + CENTIPEDE_HEIGHT);
                    centipede.act(-CENTIPEDE_SPEED);
                    centipede.cur_direction = -CENTIPEDE_SPEED;
                } else if(c_x < 0) {
                    centipede.setY(c_y + CENTIPEDE_HEIGHT);
                    centipede.act(CENTIPEDE_SPEED);
                    centipede.cur_direction = CENTIPEDE_SPEED;
                }
                else{
                    centipede.act(centipede.cur_direction);
                }
            }
        }

//        Iterator it = centipedes.iterator();
//
//        while (it.hasNext()) {
//            Centipede centipede = (Centipede) it.next();
//            if (centipede.isVisible()) {
//                int y = centipede.getY();
//                if (y > GROUND - CENTIPEDE_HEIGHT) {
//                    ingame = false;
//                    message = "Invasion!";
//                }
//                centipede.act(direction);
//            }
//        }

        //player deaths
        if (spider.isVisible() && player.isVisible()) {
            if (spider.getX() >= player.getX() && spider.getX() <= (player.getX() + PLAYER_WIDTH)
                        && spider.getY() >= player.getY() && spider.getY() <= (player.getY() + PLAYER_HEIGHT)) {
                player.setDying(true);
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
                shots.add(new Shot (x, y));
            }
        }
    }
}
