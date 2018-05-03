import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {

        Socket clientSocket = null;

        DataOutputStream os = null;
        String request;

        BufferedReader is = null;
        String responseLine;

        try {
            clientSocket = new Socket("localhost", 5000);
            os = new DataOutputStream(
                    clientSocket.getOutputStream());

            is = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: hostname" );
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname");
        }

        if ( clientSocket != null && os != null && is != null ) {
            try {
                request = "Hello server\n";
                os.writeBytes( request );
                System.out.println("CLIENT: " + request);

                if ((responseLine = is.readLine()) != null ) {
                    System.out.println("SERVER: " + responseLine);
                }

                os.close();
                is.close();
                clientSocket.close();
            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                System.err.println("IOException: " + e);
            }
        }


    }

}
