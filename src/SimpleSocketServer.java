import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simple socket server that receives messages via a socket on an input stream and echoes the
 * same message back on an output stream.  The socket port is specified by the user from
 * the command line.
 * Extends the Thread class.
 */
public class SimpleSocketServer extends Thread {
	/**
	 * Socket for communication between server and client
	 */
    private ServerSocket serverSocket;
    
    /**
     * Port for communication between server and client
     */
    private int port;
    
    /**
     * Status variable
     */
    private boolean running = false;

    /**
     * Constructor
     * @param port
     */
    public SimpleSocketServer(int port) {
        this.port = port;
    }

    /**
     * Start the server using the specified port
     */
    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            this.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the server
     */
    public void stopServer() {
        running = false;
        this.interrupt();
    }

    /**
     * Wait for a client connection to the socket and handle that connection when it occurs. 
     * Overriden Thread.run() method.  Automatically runs when the thread is started.
     */
    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                System.out.println("Listening for a connection");

                // Wait for client connection to socket server and create socket when that occurs
                Socket socket = serverSocket.accept();

                // Pass the socket to the RequestHandler thread for processing
                RequestHandler requestHandler = new RequestHandler(socket);
                requestHandler.start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Main function.
     * Get port from command line argument and create and start the socket server.
     * @param args - port for communication
     */
    public static void main(String[] args)
    {
        if(args.length == 0) {
            System.out.println("Usage: SimpleSocketServer <port>");
            System.exit(0);
        }
        
        int port = Integer.parseInt(args[0]);
        System.out.println("Start server on port: " + port);

        SimpleSocketServer server = new SimpleSocketServer(port);
        server.startServer();

        // Automatically shutdown in 1 minute
        try {
            Thread.sleep(60000);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        server.stopServer();
    }
}

/**
 * Process messages received from a client via a socket by echoing the messages back to the client.
 * Processing stops when a blank message is received.
 * Extends the Thread class.
 */
class RequestHandler extends Thread
{
	/**
	 * Socket for communication between server and client
	 */
    private Socket socket;

    /**
     * Constructor
     * @param socket
     */
    RequestHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Process messages received from a client by echoing them back using input and output
     * data streams connected to the socket. 
     * Overriden Thread.run() method.  Automatically runs when the thread is started.
     */
    @Override
    public void run() {
        try  {
            System.out.println("Received a connection");

            // Get input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            // Write out header to the client
            out.println("Echo Server 1.0");
            out.flush();

            // Echo messages back to the client until the client closes the connection
            // or an empty line is received
            String line = in.readLine();
            while((line != null) && (line.length()) > 0) {
            	System.out.println("Received: " + line);
                out.println("Echo: " + line);
                out.flush();
                line = in.readLine();
            }

            // Close the connection
            in.close();
            out.close();
            socket.close();
            System.out.println("Connection closed");
        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }
}