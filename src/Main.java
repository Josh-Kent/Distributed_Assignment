import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Main implements Runnable, ActionListener {

    private RaceJPanel animation;
    private JFrame window;
    private JMenuBar menuBar;
    private JMenu trackMenu;
    private JMenuItem openMenuItem;
    private Car[] cars = new Car[2];
    private int thisCar;
    private String track;
    private Socket clientSocket = null;
    private DataOutputStream stringOutputStream = null;
    private BufferedReader stringInputStream = null;
    //private ObjectInputStream carInputStream;
    //private ObjectOutputStream carOutputStream;
    private String request = null;
    private String responseLine = null;
    private String host = "localhost";



    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Main());

    }

    public void run() {

        try {
            clientSocket = new Socket( "localhost", 5000);
            stringOutputStream = new DataOutputStream(
                    clientSocket.getOutputStream());
            //carOutputStream = new ObjectOutputStream(
              //      clientSocket.getOutputStream());

            stringInputStream = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            //carInputStream = new ObjectInputStream(
              //      clientSocket.getInputStream());

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host );
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + host);
        }


        // ############
        // TESTING CODE
        // ############

       if ( clientSocket != null &&
               stringInputStream != null &&
               stringOutputStream != null ) {
                //carOutputStream != null &&
                //carInputStream != null ) {
            try {

                //cars[0] = new Car(1, 50, 350);
                //cars[1] = new Car(2, 100, 350);

                //while((cars[0] = (Car) carInputStream.readObject()) == null);
                String carData;
                while ((carData = stringInputStream.readLine()) == null);

                String[] carDataArray = carData.split("::");
                int[] carDataIntArr = new int[carDataArray.length];
                for (int i = 0; i < carDataArray.length; i++) {
                    carDataIntArr[i] = Integer.parseInt(carDataArray[i]);
                }

                cars[0] = new Car(carDataIntArr[0], carDataIntArr[1], carDataIntArr[2]);


                /*

                request = "Attempting Connection\n";
                stringOutputStream.writeBytes( request );
                System.out.println("CLIENT: " + request);

                thisCar = Integer.parseInt(stringInputStream.readLine().trim());


                while ((responseLine = stringInputStream.readLine()) == null);

                if (( responseLine = stringInputStream.readLine()) != null ) {
                    System.out.println("SERVER: " + responseLine );
                }

                stringOutputStream.writeBytes("Connection Established\n");

                //String[] threadResponse = stringInputStream.readLine().split("\n");
                //thisCar = Integer.parseInt(threadResponse[0]);*/



            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                //System.err.println("IOException: " + e);
                e.printStackTrace();
            } /*catch (ClassNotFoundException e) {
                System.err.println("Could not find class: " + e);
            }*/
        }


        // ################
        // END TESTING CODE
        // ################





        try {
            //cars[1] = (Car) carInputStream.readObject();

            String carData;
            if ((carData = stringInputStream.readLine()) != null) {

                String[] carDataArray = carData.split("::");
                int[] carDataIntArr = new int[carDataArray.length];
                for (int i = 0; i < carDataArray.length; i++) {
                    carDataIntArr[i] = Integer.parseInt(carDataArray[i]);
                }

                cars[1] = new Car(carDataIntArr[0], carDataIntArr[1], carDataIntArr[2]);
            }


        } catch (IOException ioe) {
            ioe.printStackTrace();
        } /*catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
*/

        /*String otherCarData = "::350::0::0\n";
        int otherCar = 0;
        if ( thisCar == 0) {
            otherCar = 1;
        }
        otherCarData = Integer.toString(cars[otherCar].getX()) + otherCarData;
        try {
            stringOutputStream.writeBytes(otherCarData);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        track = "Square";

        animation = new RaceJPanel(cars[0], cars[1], thisCar, track, stringInputStream, stringOutputStream);
        window = new JFrame("Race Track");
        menuBar = new JMenuBar();

        trackMenu = new JMenu("File");
        openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(this);
        trackMenu.add(openMenuItem);

        menuBar.add(trackMenu);

        window.setJMenuBar(menuBar);


        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                try {
                    stringOutputStream.close();
                    stringInputStream.close();
                    clientSocket.close();
                } catch (UnknownHostException uhe) {
                    System.err.println("Trying to connect to unknown host: " + uhe);
                } catch (IOException ioe) {
                    System.err.println( "IOException: " + ioe);
                } catch (NullPointerException npe) {
                    System.err.println("Could not find object to close: " + npe);
                }
            }
        });


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

