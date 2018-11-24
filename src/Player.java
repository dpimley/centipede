import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite implements Constraints {

    private final int START_Y = 600;
    private final int START_X = 350;


    private final String playerImg = "images\\player.png";
    private int width;

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        ImageIcon ii = new ImageIcon(playerImg);

        width = ii.getImage().getWidth(null);

        setImage(ii.getImage());
        setX(START_X);
        setY(START_Y);
    }

    public void act() {
        x += dx;
        y += dy;

        if (x <= 0) {
            x = 0;
        }

        if (x >= BOARD_WIDTH - PLAYER_WIDTH){
            x = BOARD_WIDTH - PLAYER_WIDTH;
        }

        if (y <= 0){
            y = 0;
        }

        if (y >= BOARD_HEIGHT - PLAYER_HEIGHT){
            y = BOARD_HEIGHT - PLAYER_HEIGHT;
        }

        dx = 0;
        dy = 0;
    }

    public void mouseMoved(MouseEvent e) {
        dx = e.getX() - x;
        dy = e.getY() - y;
    }
}
