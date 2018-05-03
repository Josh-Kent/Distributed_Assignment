import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main implements Runnable, ActionListener {

    private RaceJPanel animation;
    private JFrame window;
    private JMenuBar menuBar;
    private JMenu trackMenu;
    private JMenuItem openMenuItem;
    private Car car1;
    private Car car2;
    private String track;


    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Main());

    }

    public void run() {

        car1 = new Car(1, 50, 350);
        car2 = new Car(2, 100, 350);
        track = "Square";

        animation = new RaceJPanel(car1, car2, track);
        window = new JFrame("Race Track");
        menuBar = new JMenuBar();

        trackMenu = new JMenu("File");
        openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(this);
        trackMenu.add(openMenuItem);

        menuBar.add(trackMenu);

        window.setJMenuBar(menuBar);


        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loadAnimation();
    }

    public void actionPerformed(ActionEvent ae) {
        TrackDialog dialog = new TrackDialog();
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public void loadAnimation() {
        window.add(animation);

        animation.addKeyListener(animation);
        animation.setFocusable(true);
        //animation.requestFocusInWindow();

        window.setPreferredSize(new Dimension(850, 750));
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);


        animation.startAnimation();
    }

    private class TrackDialog extends JDialog implements ActionListener {
        private JButton okButton = new JButton("OK");

        private JRadioButton squareButton = new JRadioButton("Square");
        private JRadioButton figure8Button = new JRadioButton("Figure of 8");

        private ButtonGroup group;

        private TrackDialog() {
            super(window, "Track Dialog", true);
            JPanel panel = new JPanel(new FlowLayout());

            okButton.setMnemonic(KeyEvent.VK_ENTER);
            okButton.setActionCommand("Confirm");

            squareButton.setMnemonic(KeyEvent.VK_B);
            squareButton.setActionCommand("Square");
            squareButton.setSelected(true);

            figure8Button.setMnemonic(KeyEvent.VK_C);
            figure8Button.setActionCommand("Figure8");

            group = new ButtonGroup();
            group.add(squareButton);
            group.add(figure8Button);

            squareButton.addActionListener(this);
            figure8Button.addActionListener(this);

            JPanel radioPanel = new JPanel(new GridLayout(0, 1));
            radioPanel.add(squareButton);
            radioPanel.add(figure8Button);

            JPanel confirmPanel = new JPanel( new GridLayout(0,1));
            confirmPanel.add(okButton);

            panel.add(radioPanel);
            panel.add(confirmPanel);
            getContentPane().add(panel);
            okButton.addActionListener(this);
            setPreferredSize(new Dimension(300, 200));
            pack();
            setLocationRelativeTo(window);
        }

        public void actionPerformed(ActionEvent ae) {

            if ( ae.getSource() == okButton ) {
                track = group.getSelection().getActionCommand();


                animation.update(track);

                setVisible(false);
            }
            else if ( ae.getSource() == squareButton ) {
                squareButton.setSelected(true);
            }
            else if ( ae.getSource() == figure8Button ) {
                figure8Button.setSelected(true);
            }
        }


    }
}

