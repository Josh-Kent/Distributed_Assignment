import javax.swing.JFrame;

public class CarAnimatorTask1 {
    public static void main( String args[] ) {

        int carNum = 1;
        CarAnimatorJPanel animation = new CarAnimatorJPanel(carNum);

        JFrame window = new JFrame( "Animator test" );
        window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        window.add( animation );

        window.pack();
        window.setVisible( true );

        animation.startAnimation();
    }
}
