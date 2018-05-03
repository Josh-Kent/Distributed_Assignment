import javax.swing.*;
import java.io.*;
import java.net.*;

public class Server implements Runnable {


    static final int PORT = 5000;
    private static final int maxClients = 2;
    private static final ServerThread[] threads = new ServerThread[maxClients];

    private ServerSocket serverSocket = null;
    private Socket socket = null;

    public static void main( String args[] ) {

        SwingUtilities.invokeLater(new Server());

    }

    public void run() {

        try {
            serverSocket = new ServerSocket(PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                socket = serverSocket.accept();

                int i = 0;
                for (i = 0; i < maxClients; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new ServerThread(socket, threads)).start();
                        break;
                    }
                }

                if (i == maxClients) {
                    socket.close();
                }


            } catch (IOException e) {
                System.out.println(e);
            }


        }

    }

    public class ServerThread extends Thread {

        protected Socket socket;
        protected ServerThread[] threads;
        //private BufferedReader inputStream = null;
        //private DataOutputStream outputStream = null;
        private ObjectOutputStream carOutputStream;
        private ObjectInputStream carInputStream;
        private int maxClientThreads;
        private int thisThread = 0;
        private int otherThread = 1;
        private Car thisCar;
        private Car otherCar;
        private int[] xArr = {50, 100};
        private int[] nArr = {1, 2};


        public ServerThread( Socket clientSocket, ServerThread[] threads) {
            this.socket = clientSocket;
            this.threads = threads;
            maxClientThreads = threads.length;
        }

        public void run() {
            int maxClientThreads = this.maxClientThreads;
            ServerThread[] threads = this.threads;

            try {
                //inputStream = new BufferedReader(
                  //      new InputStreamReader(
                    //            socket.getInputStream()));
                //outputStream = new DataOutputStream(
                  //      socket.getOutputStream());
                carInputStream = new ObjectInputStream(
                        socket.getInputStream());
                carOutputStream = new ObjectOutputStream(
                        socket.getOutputStream());

                for (int i = 0; i < maxClientThreads; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        otherThread = i;
                    } else if (threads[i] == this) {
                        thisThread = i;
                    }
                }

                thisCar = new Car(nArr[thisThread], xArr[thisThread], 350);
                otherCar = new Car(nArr[otherThread], xArr[otherThread], 350);

                threads[thisThread].carOutputStream.writeObject(thisCar);
                threads[thisThread].carOutputStream.writeObject(otherCar);


                //threads[thisThread].outputStream.writeBytes(Integer.toString(thisThread) + "\n");




            } catch (IOException e) {
                return;
            }



            try {
                while (true) {
                    Car car = (Car) carInputStream.readObject();
                    if (thisCar.gameOver == true) {
                        break;
                    } else {
                        if (threads[otherThread] != null) {
                            threads[otherThread].carOutputStream.writeObject(car);
                        }
                    }
                }


                for (int i = 0; i < threads.length; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
                carInputStream.close();
                carOutputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
