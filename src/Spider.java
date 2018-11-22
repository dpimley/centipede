import javax.swing.ImageIcon;

public class Spider extends Sprite {
    private int lives;
    private final String spiderImg = "../images/spider.png";

    public Spider(int x, int y) {
        initSpider(x, y);
    }

    private void initSpider(int x, int y) {
        this.x = x;
        this.y = y;

        ImageIcon ii = new ImageIcon(spiderImg);
        setImage(ii.getImage());
    }

    public void act(int dx, int dy){
        this.x += dx;
        this.y += dy;
    }
}
