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

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;

public class Board extends JPanel implements Runnable, Constraints {

    private Dimension d;
    private ArrayList<Centipede> centipedes;
    private Spider spider;
    private Player player;
    private ArrayList<Shot> shots;
    private ArrayList<Mushroom> mushrooms;

    private final int SPIDER_INIT_X = 0;
    private final int SPIDER_INIT_Y = GROUND;
    private int deaths = 0;

    public int MUSHROOM_CHANCE = 0;

    private int game_lives = 3;
    private int game_score = 0;

    private boolean ingame = true;

    private boolean restart_round = false;

    private String message = "Game Over";

    private Thread animator;

    public Board(int m_c){
        MUSHROOM_CHANCE = m_c;
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
        for (int i = (BOARD_WIDTH - CENTIPEDE_WIDTH); i > (BOARD_WIDTH - CENTIPEDE_WIDTH) - (CENTIPEDE_WIDTH * NUMBER_OF_CENTIPEDES_TO_DESTROY); i -= CENTIPEDE_WIDTH) {
            centipedes.add(new Centipede(i, 32));
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

    public void drawScore(Graphics g) {
        if (ingame) {
            Font small = new Font("Helvetica", Font.BOLD, 14);
            FontMetrics metr = this.getFontMetrics(small);
            g.setColor(Color.white);
            g.setFont(small);
            g.drawString("Score: " + game_score + " Lives: " + game_lives, 2, 16);
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.blue);
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);
        g.setColor(Color.white);
        g.setFont(small);
        g.drawString("Score: " + game_score + " Lives: " + game_lives, 2, 16);

        if (ingame) {
            drawCentipedes(g);
            drawSpider(g);
            drawMushrooms(g);
            drawPlayer(g);
            drawShot(g);
            drawScore(g);
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
        g.drawString("Game Over! Score: " + game_score, (BOARD_WIDTH - metr.stringWidth(message)) / 2, BOARD_WIDTH / 2);
    }

    public void getRestorePoints() {
        for (Mushroom mushroom: mushrooms) {
            if (mushroom.isVisible() && (mushroom.times_hit == 1 || mushroom.times_hit == 2)) {
                game_score += 10;
            }
        }
    }

    // makes a new game based on whether or not the last round was won or if the player died
    public void restart_game(boolean win) {
        // if the player died
        if (!win) {
            // restore mushrooms
            for (Mushroom mushroom: mushrooms) {
                mushroom.setInitialImage();
                mushroom.setDying(false);
            }

            // reset centipede
            int i = (BOARD_WIDTH - CENTIPEDE_WIDTH);
            for (Centipede centipede: centipedes) {
                centipede.setX(i);
                centipede.setY(32);
                centipede.setDying(false);
                centipede.setInitialImage();
                centipede.cur_direction = -CENTIPEDE_SPEED;
                i -= CENTIPEDE_WIDTH;
            }

            // reset spider
            spider.setX(SPIDER_INIT_X);
            spider.setY(SPIDER_INIT_Y);
            spider.setDying(false);
            spider.setInitialImage();
            spider.target_reached = true;
            spider.setTarget();

            // reset player
            player.setX(344);
            player.setY(600);
            player.setVisible(true);
            player.setDying(false);

            // restart round set back to false
            game_lives--;
            restart_round = false;
        }
        else {
            centipedes = new ArrayList<>();
            drawInitCentipede();
            mushrooms = new ArrayList<>();
            drawInitMushrooms();
            spider = new Spider(SPIDER_INIT_X, SPIDER_INIT_Y);
            player = new Player();
            shots = new ArrayList<>();
            restart_round = false;
        }
    }

    public boolean doesOverlapPC(Player obj1, Centipede obj2, int obj1_w, int obj1_h, int obj2_w, int obj2_h) {
        if ((obj1.getX() > obj2.getX() + obj2_w) || (obj2.getX() > obj1.getX() + obj1_w)) {
            return false;
        }

        if ((obj1.getY() > obj2.getY() + obj2_h) || (obj2.getY() > obj1.getY() + obj1_h)) {
            return false;
        }

        return true;
    }

    public boolean doesOverlapPS(Player obj1, Spider obj2, int obj1_w, int obj1_h, int obj2_w, int obj2_h) {
        if ((obj1.getX() > obj2.getX() + obj2_w) || (obj2.getX() > obj1.getX() + obj1_w)) {
            return false;
        }

        if ((obj1.getY() > obj2.getY() + obj2_h) || (obj2.getY() > obj1.getY() + obj1_h)) {
            return false;
        }

        return true;
    }

    public void animationCycle() {
        if ((deaths == NUMBER_OF_CENTIPEDES_TO_DESTROY || !player.isVisible()) && !restart_round) {
            restart_round = true;
            // score for killing off a centipede
            if (deaths == NUMBER_OF_CENTIPEDES_TO_DESTROY) {
                game_score += 600;
                restart_game(true);
            }

            if (!player.isVisible()) {
                getRestorePoints();
                restart_game(false);
            }

            deaths = 0;

            if (game_lives == 0) {
                ingame = false;
            }
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
                            centipede.switchState();
                            if (centipede.isDying()) {
                                deaths++;
                                game_score += 5;
                            }
                            else {
                                game_score += 2;
                            }
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
                            if (mushroom.isDying()) {
                                game_score += 5;
                            }
                            else {
                                game_score += 1;
                            }
                            shot.die();
                        }
                    }
                }

                if (spider.isVisible() && shot.isVisible()) {
                    if (shotX >= spider.getX() && shotX <= (spider.getX() + SPIDER_WIDTH)
                                && shotY >= spider.getY() && shotY <= (spider.getY() + SPIDER_HEIGHT)) {
                        spider.switchState();
                        if (spider.isDying()) {
                            game_score += 600;
                        }
                        else {
                            game_score += 100;
                        }
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
                }

                if (c_x + CENTIPEDE_WIDTH > BOARD_WIDTH) {
                    if (c_y <= GROUND - CENTIPEDE_HEIGHT) {
                        centipede.setY(c_y + CENTIPEDE_HEIGHT);
                    }
                    centipede.act(-CENTIPEDE_SPEED);
                    centipede.cur_direction = -CENTIPEDE_SPEED;
                } else if(c_x < 0) {
                    if (c_y <= GROUND - CENTIPEDE_HEIGHT) {
                        centipede.setY(c_y + CENTIPEDE_HEIGHT);
                    }
                    centipede.act(CENTIPEDE_SPEED);
                    centipede.cur_direction = CENTIPEDE_SPEED;
                }
                else{
                    centipede.act(centipede.cur_direction);
                }
            }
        }

        //player deaths
        if (spider.isVisible() && player.isVisible()) {
            if (doesOverlapPS(player, spider, PLAYER_WIDTH, PLAYER_HEIGHT, SPIDER_WIDTH, SPIDER_HEIGHT)) {
                player.setDying(true);
            }
        }

        for (Centipede centipede: centipedes) {
            if (centipede.isVisible() && player.isVisible()){
                if (doesOverlapPC(player, centipede, PLAYER_WIDTH, PLAYER_HEIGHT, CENTIPEDE_WIDTH, CENTIPEDE_HEIGHT)) {
                    player.setDying(true);
                }
            }
        }
    }

    public void play_sound() {
        try {
            // Open an audio input stream.
            File soundFile = new File("sounds\\fire_shot.wav"); //you could also get the sound file with an URL
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        long beforeTime, timeDiff, sleep;
        beforeTime = System.currentTimeMillis();
        while (ingame) {
            if (!restart_round) {
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
                play_sound();
            }
        }
    }
}
