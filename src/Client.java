import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client implements Runnable, ActionListener {

    // instantiate variables
    private RaceJPanel animation;
    private JFrame window;
    private Car[] cars = new Car[2];
    private String track;
    private Socket clientSocket = null;
    private DataOutputStream stringOutputStream = null;
    private BufferedReader stringInputStream = null;

    // entry point for client
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Client());

    }

    // execute main code for client
    public void run() {

        // create connection information
        String host = "localhost";
        try {
            clientSocket = new Socket(host, 5000);
            stringOutputStream = new DataOutputStream(
                    clientSocket.getOutputStream());

            stringInputStream = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));

        // handle exceptions
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + host);
        }

        // check socket and input/output streams are not null
       if ( clientSocket != null &&
               stringInputStream != null &&
               stringOutputStream != null ) {

            try {
                String carData;
                // wait for second connection
                //noinspection StatementWithEmptyBody
                while ((carData = stringInputStream.readLine()) == null);

                // handle car input from server
                String[] carDataArray = carData.split("::");
                int[] carDataIntArr = new int[carDataArray.length];
                for (int i = 0; i < carDataArray.length; i++) {
                    carDataIntArr[i] = Integer.parseInt(carDataArray[i]);
                }

                // save clients car to position 0 in array
                cars[0] = new Car(carDataIntArr[0], carDataIntArr[1], carDataIntArr[2]);

            // handle exceptions
            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // accept other clients car info
            String carData;
            assert stringInputStream != null;
            if ((carData = stringInputStream.readLine()) != null) {

                String[] carDataArray = carData.split("::");
                int[] carDataIntArr = new int[carDataArray.length];
                for (int i = 0; i < carDataArray.length; i++) {
                    carDataIntArr[i] = Integer.parseInt(carDataArray[i]);
                }

                // save other clients car info in position 1 in array
                cars[1] = new Car(carDataIntArr[0], carDataIntArr[1], carDataIntArr[2]);
            }

        // handle exceptions
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        // set default track to square
        track = "Square";

        // run constructor for race JPanel, create JFrame and Menubar
        animation = new RaceJPanel(cars[0], cars[1], track, stringInputStream, stringOutputStream);
        window = new JFrame("Race Track");
        JMenuBar menuBar = new JMenuBar();

        // setup menu to allow for track switching
        JMenu trackMenu = new JMenu("File");
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(this);
        trackMenu.add(openMenuItem);
        menuBar.add(trackMenu);
        window.setJMenuBar(menuBar);

        // set default behaviour on close
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // add window listener to close streams when window closes
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                try {
                    stringOutputStream.close();
                    stringInputStream.close();
                    clientSocket.close();
                } catch (UnknownHostException uhe) {
                    System.err.println("Trying to access unknown host: " + uhe);
                } catch (IOException ioe) {
                    System.err.println( "IOException: " + ioe);
                } catch (NullPointerException npe) {
                    System.err.println("Could not find object to close: " + npe);
                }
            }
        });

        // run load animation method
        loadAnimation();

    }

    // code to run when action is performed
    public void actionPerformed(ActionEvent ae) {
        TrackDialog dialog = new TrackDialog();
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private void loadAnimation() {
        // add JPanel to JFrame
        window.add(animation);

        // add key listeners and set up JPanel
        animation.addKeyListener(animation);
        animation.setFocusable(true);

        // set JFrame sizes and visibility etc
        window.setPreferredSize(new Dimension(850, 750));
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // start animation timer
        animation.startAnimation();
    }

    // Dialog class declaration
    private class TrackDialog extends JDialog implements ActionListener {
        // create OK button
        private JButton okButton = new JButton("OK");

        // create radio button to choose track type
        private JRadioButton squareButton = new JRadioButton("Square");
        private JRadioButton figure8Button = new JRadioButton("Figure of 8");

        // group radio buttons variable
        private ButtonGroup group;

        // constructor
        private TrackDialog() {
            super(window, "Track Dialog", true);
            // define new JPanel for dialog
            JPanel panel = new JPanel(new FlowLayout());

            // set enter to ok button and
            okButton.setMnemonic(KeyEvent.VK_ENTER);

            // set 'A' to square option and set as selected by default
            squareButton.setMnemonic(KeyEvent.VK_A);
            squareButton.setActionCommand("Square");
            squareButton.setSelected(true);

            // set 'B' to figure of 8 option
            figure8Button.setMnemonic(KeyEvent.VK_B);
            figure8Button.setActionCommand("Figure8");

            // group radio buttons
            group = new ButtonGroup();
            group.add(squareButton);
            group.add(figure8Button);

            // add action listener to buttons
            squareButton.addActionListener(this);
            figure8Button.addActionListener(this);
            okButton.addActionListener(this);

            // create new JPanel for radio buttons (allows for vertical display)
            JPanel radioPanel = new JPanel(new GridLayout(0, 1));
            radioPanel.add(squareButton);
            radioPanel.add(figure8Button);

            // create JPanel for ok button
            JPanel confirmPanel = new JPanel( new GridLayout(0,1));
            confirmPanel.add(okButton);

            // adds JPanels to the dialog JPanel
            panel.add(radioPanel);
            panel.add(confirmPanel);
            getContentPane().add(panel);

            // set dialog size
            setPreferredSize(new Dimension(300, 200));
            pack();
            setLocationRelativeTo(window);
        }

        // action listener
        public void actionPerformed(ActionEvent ae) {

            // if ok button pressed, get action command for drawing correct track
            if ( ae.getSource() == okButton ) {
                track = group.getSelection().getActionCommand();

                // update drawing
                animation.update(track);

                // close dialog
                setVisible(false);
            // set square as selected if clicked
            } else if ( ae.getSource() == squareButton ) {
                squareButton.setSelected(true);
            // set figure 8 as selected if clicked
            } else if ( ae.getSource() == figure8Button ) {
                figure8Button.setSelected(true);
            }
        }
    }
}

