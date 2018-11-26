import java.awt.EventQueue;
import javax.swing.JFrame;

public class Game extends JFrame implements Constraints {
    public Game(int pass_through) {
        initUI(pass_through);
    }

    private void initUI(int pass_through) {
        add(new Board(pass_through));
        setTitle("Centipede");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(BOARD_WIDTH, BOARD_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public static void main(String [] args) {
        EventQueue.invokeLater(() -> {
            int pass_through = Integer.valueOf(args[0]);
           Game ex = new Game(pass_through);
           ex.setVisible(true);
        });
    }
}

