import javax.swing.ImageIcon;

public class Centipede extends Sprite implements Constraints {
    private int lives;
    private final String centipedeImg = "images\\centipede.png";
    public int cur_direction = -CENTIPEDE_SPEED;

    public Centipede(int x, int y) {
        initCentipede(x, y);
    }

    private void initCentipede(int x, int y) {
        this.x = x;
        this.y = y;

        ImageIcon ii = new ImageIcon(centipedeImg);
        setImage(ii.getImage());
    }

    public void act(int direction) {
        this.x += direction;
    }
}
