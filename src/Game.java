import java.awt.EventQueue;
import javax.swing.JFrame;

public class Game extends JFrame implements Constraints {
    public Game() {
        initUI();
    }

    private void initUI() {
        add(new Board());
        setTitle("Centipede");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(BOARD_WIDTH, BOARD_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public static void main(String [] args) {
        EventQueue.invokeLater(() -> {
           Game ex = new Game();
           ex.setVisible(true);
        });
    }
}

