import javax.swing.ImageIcon;
import java.util.Random;
import java.lang.Math;

public class Spider extends Sprite implements Constraints {
    private int lives;
    private final String spiderImg0 = "images\\spider0.png";
    private final String spiderImg1 = "images\\spider1.png";
    private int target_x;
    private int target_y;
    public boolean target_reached = true;
    public int times_hit;

    public Spider(int x, int y) {
        initSpider(x, y);
    }

    private void initSpider(int x, int y) {
        this.x = x;
        this.y = y;

        ImageIcon ii = new ImageIcon(spiderImg0);
        setImage(ii.getImage());
        times_hit = 0;
    }

    public void setTarget() {
        if (target_reached) {
            Random rand = new Random();

            /* rand_x and rand_y represent a target destination
               within the player area
            */

            target_x = rand.nextInt(BOARD_WIDTH - (2 * SPIDER_WIDTH)) + SPIDER_WIDTH;
            target_y = rand.nextInt(BOARD_HEIGHT - SPIDER_AREA - SPIDER_HEIGHT) + SPIDER_AREA;

            this.dx = 2 * Integer.signum(target_x - this.x);
            this.dy = 2 * Integer.signum(target_y - this.y);

            target_reached = false;
        }
    }

    public void act(){
        if (this.x >= target_x - 1 && this.x <= target_x + 1) {
            this.dx = 0;
        }

        if (this.y >= target_y - 1 && this.y <= target_y + 1) {
            this.dy = 0;
        }

        this.x += dx;
        this.y += dy;

        if ((this.x >= target_x - 1 && this.x <= target_x + 1) && (this.y >= target_y - 1 && this.y <= target_y + 1)) {
            target_reached = true;
        }
    }

    public void switchState() {
        times_hit++;
        if (times_hit == 2) {
            this.setDying(true);
        }
        else if (times_hit == 1) {
            ImageIcon ii = new ImageIcon(spiderImg1);
            setImage(ii.getImage());
        }
    }
}
