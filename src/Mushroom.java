import javax.swing.ImageIcon;

public class Mushroom extends Sprite {
    private final String mushroomImg0 = "../images/mushroom0.png";
    private final String mushroomImg1 = "../images/mushroom1.png";
    private final String mushroomImg2 = "../images/mushroom2.png";

    private int times_hit;

    public void Mushroom(int x, int y) {
        initMushroom(x, y);
    }

    private void initMushroom(int x, int y) {
        this.x = x;
        this.y = y;

        ImageIcon ii = new ImageIcon(mushroomImg0);
        setImage(ii.getImage());
        times_hit = 0;
    }

    public void switchState() {

        //increment times hit
        times_hit++;

        //choose new state for mushroom
        if (times_hit == 3) {
            this.setDying(true);
            this.die();
        }
        else if (times_hit == 2) {
            ImageIcon ii = new ImageIcon(mushroomImg2);
            setImage(ii.getImage());
        }
        else if (times_hit == 1) {
            ImageIcon ii = new ImageIcon(mushroomImg1);
            setImage(ii.getImage());
        }
    }
}
