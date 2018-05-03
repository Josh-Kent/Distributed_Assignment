import java.awt.*;
import java.io.Serializable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import javax.swing.Action;


public class Car implements Serializable {
    private final int maxSpeed = 5;
    private final int[][] angles = {
            { 0,-2}, { 1,-2}, { 2,-2}, { 2,-1},
            { 2, 0}, { 2, 1}, { 2, 2}, { 1, 2},
            { 0, 2}, {-1, 2}, {-2, 2}, {-2, 1},
            {-2, 0}, {-2,-1}, {-2,-2}, {-1,-2}
    };
    private final int[][] collisionCorner = {
            {12, 5}, { 7, 4}, { 5, 5}, { 3, 9},
            { 5,12}, { 3, 7}, { 4, 4}, { 7, 3},
            {12, 5}, { 9, 3}, { 5, 4}, { 4, 7},
            { 5,12}, { 4, 9}, { 5, 5}, { 9, 4}
    };
    private final int[][] collisionDimensions = {
            {26,40}, {34,43}, {40,40}, {43,34},
            {40,26}, {43,34}, {41,41}, {34,43},
            {26,40}, {34,43}, {41,41}, {43,34},
            {43,26}, {43,34}, {41,41}, {34,43}
    };
    public boolean gameOver = false;
    private int carNum;
    private int x;
    private int y;

    private int direction = 0;
    private int speed = 0;

    public Car( int n, int x, int y) {
        this.carNum = n;
        this.x = x;
        this.y = y;

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCarNum() {
        return carNum;
    }

    public int getDirection() {
        return direction;
    }

    public int getSpeed() {
        return speed;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void update() {

        this.x = x + (angles[direction][0] * speed);
        this.y = y + (angles[direction][1] * speed);

    }

    public void turnLeft( int dir ) {
        if (dir <= 0) {
            this.direction = 15;
        } else if (dir < 16) {
            this.direction = dir - 1;
        }
    }

    public void turnRight( int dir ) {
        if (dir >= 15) {
            this.direction = 0;
        } else if (dir >= 0) {
            this.direction = dir + 1;
        }
    }

    public void forward(int speed) {
        if ( speed < maxSpeed ) {
            this.speed = speed + 1;
        } else if ( speed == maxSpeed ) {
            this.speed = maxSpeed;
        }
    }

    public void backwards(int speed) {
        if ( speed > (0 - maxSpeed)) {
            this.speed = speed - 1;
        } else if ( speed == ( 0 - maxSpeed)) {
            this.speed = (0 - maxSpeed);
        }

    }

    public Rectangle getBounds() {
        return new Rectangle(x + collisionCorner[direction][0], y + collisionCorner[direction][1],
                collisionDimensions[direction][0], collisionDimensions[direction][1]);
    }
}
