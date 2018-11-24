import javax.swing.ImageIcon;

public class Centipede extends Sprite implements Constraints {
    private int lives;
    private final String centipedeImg0 = "images\\centipede0.png";
    private final String centipedeImg1 = "images\\centipede1.png";
    public int cur_direction = -CENTIPEDE_SPEED;
    public int times_hit;

    public Centipede(int x, int y) {
        initCentipede(x, y);
    }

    private void initCentipede(int x, int y) {
        this.x = x;
        this.y = y;

        ImageIcon ii = new ImageIcon(centipedeImg0);
        setImage(ii.getImage());
        times_hit = 0;
    }

    public void act(int direction) {
        this.x += direction;
    }

    public void switchState() {
        times_hit++;

        if (times_hit == 2) {
            this.setDying(true);
        }
        else if (times_hit == 1) {
            ImageIcon ii = new ImageIcon(centipedeImg1);
            setImage(ii.getImage());
        }
    }

    public void setInitialImage() {
        ImageIcon ii = new ImageIcon(centipedeImg0);
        setImage(ii.getImage());
        times_hit = 0;
        setVisible(true);
    }
}
