import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Objects;

public class Server implements Runnable {

    // set up server variables
    private static final int PORT = 5000;
    private static final int maxClients = 2;
    private static final ServerThread[] threads = new ServerThread[maxClients];
    private ServerSocket serverSocket = null;

    // entry point for server
    public static void main( String args[] ) {

        // start run method
        SwingUtilities.invokeLater(new Server());

    }

    // execute main code for server
    public void run() {
        // try creating a new serverSocket with designated port
        try {
            serverSocket = new ServerSocket(PORT);

        // handle exceptions
        } catch (IOException e) {
            e.printStackTrace();
        }

        // keep server running while waiting for connections
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Socket socket = serverSocket.accept();

                // when connection attempted start new thread and execute server thread code
                int i;
                for (i = 0; i < maxClients; i++) {
                    if (threads[i] == null) {
                        // save new server thread in array for future reference
                        (threads[i] = new ServerThread(socket, threads)).start();
                        break;
                    }
                }

                // close connection if too many connections attempted
                if (i == maxClients) {
                    socket.close();
                }

            // handle exceptions
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // server thread class declaration
    public class ServerThread extends Thread {

        // instantiate variables
        Socket socket;
        ServerThread[] threads;
        private BufferedReader inputStream = null;
        private DataOutputStream outputStream = null;
        private int maxClientThreads;
        private int thisThread = 0;
        private int otherThread = 1;
        private int[] xArr = {50, 100};
        private int[] nArr = {1, 2};

        // Constructor
        ServerThread(Socket clientSocket, ServerThread[] threads) {
            this.socket = clientSocket;
            this.threads = threads;
            maxClientThreads = threads.length;
        }

        // execute main code
        public void run() {
            // create local variables
            int maxClientThreads = this.maxClientThreads;
            ServerThread[] threads = this.threads;

            // set up connection info
            try {
                inputStream = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
                outputStream = new DataOutputStream(
                        socket.getOutputStream());

                // loop over current threads, check if one exists and isnt current thread
                for (int i = 0; i < maxClientThreads; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        // if thread exists save index for future reference
                        otherThread = i;
                    } else if (threads[i] == this) {
                        // save this threads index for future reference
                        thisThread = i;
                    }
                }

                // create car info, pass to clients, cars passed depend on which thread
                // this thread is
                String car1 = nArr[thisThread] + "::" + xArr[thisThread] + "::350\n";
                String car2 = nArr[otherThread] + "::" + xArr[otherThread] + "::350\n";
                threads[thisThread].outputStream.writeBytes(car1);
                threads[thisThread].outputStream.writeBytes(car2);

            // handle exceptions
            } catch (IOException e) {
                return;
            }


            // listen for incoming data streams
            try {
                while (true) {
                    String line;

                    // check incoming data stream exists
                    if ((line = inputStream.readLine()) != null) {
                        // check if another thread exists
                        if (threads[otherThread] != null) {
                            // send incoming data stream to the other client
                            threads[otherThread].outputStream.writeBytes(line + "\n");
                        }
                    }
                    // check if incoming data stream GAME OVER
                    if (Objects.equals(line, "GAME OVER")) {
                        // send GAME OVER message to other client, then break
                        threads[otherThread].outputStream.writeBytes(line + "\n");
                        break;
                    }
                }

                // set this thread to null in threads array
                for (int i = 0; i < threads.length; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }

                // close streams
                inputStream.close();
                outputStream.close();
                socket.close();

            // handle exceptions
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
