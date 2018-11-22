import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite implements Constraints {

    private final int START_Y = 600;
    private final int START_X = 350;

    private final int PLAYER_AREA_Y = 300;

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

        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - 2 * width){
            x = BOARD_WIDTH - 2 * width;
        }

        if (y <= PLAYER_AREA_Y){
            y = PLAYER_AREA_Y;
        }

        if (y >= BOARD_HEIGHT - 2){
            y = BOARD_HEIGHT - 2;
        }

        dx = 0;
        dy = 0;
    }

    public void mouseMoved(MouseEvent e) {
        dx = e.getX() - x;
        dy = e.getY() - y;
    }
}
