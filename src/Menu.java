import java.awt.event.*;
import javax.swing.*;

public class Menu {

    public static String CHOSEN_TRACK = "Square";

    public static void main(String[] args) {


        JButton b = new JButton("Track: " + CHOSEN_TRACK);

        final JPopupMenu menu = new JPopupMenu("Menu");
        menu.add( MakeMenuItem("Square",   b ) );
        menu.add( MakeMenuItem("Figure 8", b ) );

        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                menu.show(b, b.getWidth()/2, b.getHeight()/2);
            }
        });
        JOptionPane.showMessageDialog(null, b);

    }

    private static JMenuItem MakeMenuItem( String text, JButton b) {
        JMenuItem menuItem = new JMenuItem( text, KeyEvent.VK_T );
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CHOSEN_TRACK = text;


            }
        });
        return menuItem;
    }
}
