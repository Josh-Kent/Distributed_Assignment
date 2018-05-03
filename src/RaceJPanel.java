import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import static java.lang.Math.round;

public class RaceJPanel extends JPanel implements KeyListener {


    protected ImageIcon car1Images[];
    protected ImageIcon car2Images[];
    private final static int TOTAL_IMAGES = 16;
    private int currentImage1 = 0;
    private int currentImage2 = 0;
    private final static int ANIMATION_DELAY = 50;
    private int width = 50;
    private int height = 50;
    private Car[] cars;
    private Car[] startPositions;
    //private int thisCar;
    //private int theirCar;
    private Timer animationTimer;
    private String track;
    private List<Rectangle> rectList;
    private int[] lapCounter = { 0, 0};
    private boolean[] pastStart = {false, false};
    //private ObjectInputStream inputStream;
    //private ObjectOutputStream outputStream;
    private BufferedReader inputStream;
    private DataOutputStream outputStream;


    public RaceJPanel(Car car1, Car car2, int thisCar, String track, BufferedReader inputStream, DataOutputStream outputStream) {

        this.cars = new Car[]{car1, car2};
        this.startPositions = new Car[] {car1, car2};
        //this.thisCar = thisCar;
        //this.theirCar = (thisCar == 0) ? 1 : 0;
        this.track = track;
        this.inputStream = inputStream;
        this.outputStream = outputStream;

        final String CAR1_IMAGE_NAME = "Top_Down_Car_" + cars[0].getCarNum() + "_50px_";
        final String CAR2_IMAGE_NAME = "Top_Down_Car_" + cars[1].getCarNum() + "_50px_";
        car1Images = new ImageIcon[TOTAL_IMAGES];
        car2Images = new ImageIcon[TOTAL_IMAGES];

        for (int count = 0; count < TOTAL_IMAGES; count++) {
            car1Images[count] = new ImageIcon(getClass().getResource(
                    "images/" + CAR1_IMAGE_NAME + count + ".png"));
            car2Images[count] = new ImageIcon(getClass().getResource(
                    "images/" + CAR2_IMAGE_NAME + count + ".png"));
        }
    }

    public void update(String track) {
        this.track = track;
        this.reset();
    }

    public void reset() {
        cars = startPositions;

        currentImage1 = 0;
        currentImage2 = 0;

        lapCounter = new int[]{0, 0};
        pastStart = new boolean[]{false, false};

        startAnimation();

        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // set up variables for dashed lines
        final float dash1[] = {10.0f};
        final BasicStroke dashed = new BasicStroke(
                1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f,
                dash1,
                0.0f);

        Graphics2D g2d = (Graphics2D) g.create();



            //Set colours
            Color c1 = Color.green;
            Color c2 = Color.gray;
            Color c3 = Color.red;
            Color c4 = Color.yellow;
            Color c5 = Color.white;

            //Draw outer grass
            g.setColor(c1);
            g.fillRect(0, 50, 850, 600);

            //Draw road
            g.setColor(c2);
            g.fillRect(50, 100, 750, 500); //Fill road


        if (track == "Figure8" ) {

            //Draw inner grass
            g.setColor(c1);
            g.fillRect(150, 200, 550, 300); //Inner grass
            g.fillRect(280, 100, 290, 550);


            int trackPolyX1[] = {280, 220, 570, 630};
            int trackPolyX2[] = {220, 280, 630, 570};
            int trackPolyY1[] = {100, 200, 600, 500};
            int trackPolyY2[] = {500, 600, 200, 100};
            int polyN = 4;

            Polygon trackPoly1 = new Polygon(trackPolyX1, trackPolyY1, polyN);
            Polygon trackPoly2 = new Polygon(trackPolyX2, trackPolyY2, polyN);

            g.setColor(c2);
            g.fillPolygon(trackPoly1);
            g.fillPolygon(trackPoly2);

            //Draw lanes with dashed lines
            g2d.setStroke(dashed);

            int dashPolyX[] = {100, 250, 600, 750, 750, 600, 250, 100};
            int dashPolyY[] = {150, 150, 550, 550, 150, 150, 550, 550};
            int dashN = 8;

            Polygon dashPoly = new Polygon(dashPolyX, dashPolyY, dashN);

            g2d.setColor(c4);
            //g2d.drawRect(100, 150, 650, 400); //Mid-lane marker
            g2d.drawPolygon(dashPoly);

            //Draw Road edges
            g.setColor(c3);


            int innerEdgePolyX1[] = {150, 220, 351, 220, 150};
            int innerEdgePolyY1[] = {200, 200, 350, 500, 500};
            int innerEdgePolyX2[] = {630, 700, 700, 630, 499};
            int innerEdgePolyY2[] = {200, 200, 500, 500, 350};
            int outerEdgePolyX[] = {50, 280, 425, 570, 800, 800, 570, 425, 280, 50};
            int outerEdgePolyY[] = {100, 100, 267, 100, 100, 600, 600, 433, 600, 600};
            int innerEdgeN = 5;
            int outerEdgeN = 10;

            Polygon innerEdgePoly1 = new Polygon(innerEdgePolyX1, innerEdgePolyY1, innerEdgeN);
            Polygon innerEdgePoly2 = new Polygon(innerEdgePolyX2, innerEdgePolyY2, innerEdgeN);
            Polygon outerEdgePoly = new Polygon(outerEdgePolyX, outerEdgePolyY, outerEdgeN);

            g.drawPolygon(innerEdgePoly1);
            g.drawPolygon(innerEdgePoly2);
            g.drawPolygon(outerEdgePoly);
            //g.drawRect(50, 100, 750, 500); //Outer edge
            //g.drawRect(150, 200, 550, 300); //Inner edge




            rectList = new ArrayList<Rectangle>();


            rectList.add( new Rectangle( 150, 200, 70, 300));
            rectList.add( new Rectangle( 630, 200, 70, 300));
            diagonalCollision( 150, 130, 220, 200);
            diagonalCollision( 150, 130, 500, 350);
            diagonalCollision( 150, -130, 350, 350);
            diagonalCollision( 150, -130, 630, 200);

            rectList.add( new Rectangle( 50, 100, 230, 1 ) );
            rectList.add( new Rectangle( 570, 100, 230, 1 ) );
            rectList.add( new Rectangle( 800, 100, 1, 500 ) );
            rectList.add( new Rectangle( 570, 600, 230, 1 ) );
            rectList.add( new Rectangle( 50, 600, 230, 1 ) );
            rectList.add( new Rectangle( 50, 100, 1, 500 ) );
            diagonalCollision( 167, 145, 280, 100);
            diagonalCollision( 167, -145, 570, 100);
            diagonalCollision( 167, -145, 425, 433);
            diagonalCollision( 167, 145, 425, 433);




        } else {

            g.setColor( c1 );
            g.fillRect( 150, 200, 550, 300 ); // grass

            g.setColor( c3 );
            g.drawRect(50, 100, 750, 500);  // outer edge
            g.drawRect(150, 200, 550, 300); // inner edge

            rectList = new ArrayList<Rectangle>();

            //rectList.add( new Rectangle(  50, 100, 750, 500) );
            rectList.add( new Rectangle( 150, 200, 550, 300) );
            rectList.add( new Rectangle( 800, 100, 1, 500));
            rectList.add( new Rectangle( 49, 100, 1, 500));
            rectList.add( new Rectangle( 50, 600, 750, 1));
            rectList.add( new Rectangle( 50, 99, 750, 1));


            g2d.setStroke(dashed);
            g2d.setColor( c4 );
            g2d.drawRect( 100, 150, 650, 400 ); // mid-lane marker

        }

        //Draw start line
        g.setColor(c5);
        g.drawLine(50, 350, 150, 350); //Start line
        Rectangle startRect = new Rectangle(50, 350, 100, 1);
        Rectangle pastStartRect = new Rectangle(50, 250, 100, 1);

        g.setColor(Color.black);
        g.drawString("Car 1: " + lapCounter[0], 175, 325);
        g.drawString("Car 2: " + lapCounter[1], 175, 375);

        car1Images[currentImage1].paintIcon(this, g, cars[0].getX(), cars[0].getY());
        car2Images[currentImage2].paintIcon(this, g, cars[1].getX(), cars[1].getY());

        sendCarData(cars[0]);
        receiveCarData();

        cars[0].update();
        cars[1].update();
        checkCarCollision();
        checkWallCollision(cars[0]);
        checkWallCollision(cars[1]);
        checkLaps(cars[0], startRect);
        checkLaps(cars[1], startRect);
        checkPastStart(cars[0], pastStartRect);
        checkPastStart(cars[1], pastStartRect);


        if (animationTimer.isRunning()) {
            currentImage1 = cars[0].getDirection();
            currentImage2 = cars[1].getDirection();
        }
    }

    private void sendCarData(Car car) {
        String carData;
        carData = Integer.toString(car.getX());
        carData = carData + "::" + Integer.toString(car.getY());
        carData = carData + "::" + Integer.toString(car.getDirection());
        carData = carData + "::" + Integer.toString(car.getSpeed());


        try {
            outputStream.writeBytes(carData + "\n");
            //outputStream.writeObject(cars[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveCarData() {

        try {
            String carData;
            if ( (carData = inputStream.readLine()) != null ) {
                if (!carData.isEmpty()) {

                    String[] carDataArray = carData.split("::");

                    cars[1].setX(Integer.parseInt(carDataArray[0]));
                    cars[1].setY(Integer.parseInt(carDataArray[1]));
                    cars[1].setDirection(Integer.parseInt(carDataArray[2]));
                    cars[1].setSpeed(Integer.parseInt(carDataArray[3]));

                    //cars[1] = (Car) inputStream.readObject();
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        } /*catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }*/


    }

    private void diagonalCollision(int yDiff, int xDiff, int xStart, int yStart) {
        int j;
        for ( int i = 0; i < yDiff; i++) {
            j = (int)round(((double)xDiff/(double)yDiff)*(double)i);
            rectList.add( new Rectangle( xStart + j, yStart + i, 2, 2 ) );

        }
    }

    private void checkCarCollision() {
        Rectangle car1Rect = cars[0].getBounds();
        Rectangle car2Rect = cars[1].getBounds();

        if (car1Rect.intersects(car2Rect)) {
            cars[0].setSpeed(0);
            cars[1].setSpeed(0);

           // gameOver();
        }
    }
    private void checkWallCollision(Car car) {
        Rectangle carRect = car.getBounds();

        for (Rectangle temp : rectList) {
            if (temp.intersects(carRect)) {
                car.setSpeed(0);
            }
        }
    }

    private void checkLaps(Car car, Rectangle startRect) {
        Rectangle carRect = car.getBounds();
        int carNum = car.getCarNum();

        if (lapCounter[carNum -1] > 2) {
            gameWon(car);
        }

        if (startRect.intersects(carRect) && pastStart[carNum - 1]) {
            pastStart[carNum - 1] = false;
            lapCounter[carNum - 1]++;
        }
    }

    private void checkPastStart(Car car, Rectangle pastStartRect) {
        Rectangle carRect = car.getBounds();
        int carNum = car.getCarNum();

        if (pastStartRect.intersects(carRect)) {
            pastStart[carNum - 1] = true;
        }
    }

    private void gameOver() {
        cars[0].gameOver = true;
        endGame("GAME OVER!\\nYou crashed!", "Game Over!");
    }

    private void gameWon(Car winner) {
        winner.gameOver = true;

        endGame("Car " + winner.getCarNum() + " Won!\nCongratulations!",
                "Winner");
    }

    private void endGame(String messageString, String titleString) {
        stopAnimation();
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        Object[] options = { "Exit", "Restart" };

        final JOptionPane optionPane = new JOptionPane(
                messageString,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[0]);

        final JDialog dialog = new JDialog(frame,
                titleString,
                true);

        dialog.setContentPane(optionPane);
        dialog.setLocationRelativeTo(frame);
        dialog.setDefaultCloseOperation(
                JDialog.DO_NOTHING_ON_CLOSE);
        /*dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {

            }
        });*/

        optionPane.addPropertyChangeListener(
                e -> {
                    String prop = e.getPropertyName();

                    if (dialog.isVisible()
                            && (e.getSource() == optionPane)
                            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {

                        dialog.setVisible(false);
                    }
                });
        dialog.pack();
        dialog.setVisible(true);

        String value = (String) optionPane.getValue();
        if (value == "Exit") {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        } else if (value == "Restart") {
            this.reset();
        }


    }



    public void startAnimation() {
        if (animationTimer == null) {
            currentImage1 = 0;
            currentImage2 = 0;

            animationTimer = new Timer(ANIMATION_DELAY, new TimerHandler());

            animationTimer.start();
        } else {
            if (!animationTimer.isRunning())
                animationTimer.restart();
        }
    }

    public void stopAnimation() {
        animationTimer.stop();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        handleKeyEvent(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        handleKeyEvent(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        handleKeyEvent(e);
    }

    private void handleKeyEvent(KeyEvent e) {
        int id = e.getID();

        if (id == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            switch (c) {
                case 'w':
                    cars[0].forward(cars[0].getSpeed());
                    break;
                case 'a':
                    cars[0].turnLeft(cars[0].getDirection());
                    break;
                case 's':
                    cars[0].backwards(cars[0].getSpeed());
                    break;
                case 'd':
                    cars[0].turnRight(cars[0].getDirection());
                    break;
            }

        }
    }

    private class TimerHandler implements ActionListener {
        public void actionPerformed( ActionEvent actionEvent ) {
            repaint();
        }
    }
}
