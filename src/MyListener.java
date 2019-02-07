/*

1. Jeff Wiand / 2-6-19
2. Java 1.8
3. Compilation Instructions:
    > javac MyWebServer.java

4. Run Instructions
    > java MyWebServer

   List of files needed for running the program
    - MyWebServer.java
    - http-streams.txt
    - serverlog.txt
    - checklist-mywebserver.html

5. My Notes

*/


import java.io.*;       //Pull in the Java Input - Output libraries for MyWebServer.java use
import java.net.*;      //Pull in the Java networking libraries for MyWebServer.java use

class Worker extends Thread {                               // Class declaration for Worker which will be a subclass of Thread class
    Socket sock;                                            // local Worker definition for sock of type Socket

    Worker(Socket s) {
        sock = s;                                           // constructor to accept the incoming sockets and set to local Socket definition called "sock"
    }

    public void run() {                                     // method launched with the .start() call in MyWebServer class

        PrintStream out = null;                                                                     // sets our output stream to null. PrintStream's can be flushed and don't throw IOExceptions
        BufferedReader in = null;                                                                   // sets our input to null

        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));                  // launched new objects to obtain our input
            out = new PrintStream(sock.getOutputStream());                                          // launched new object to print our output

            String socketDataString;                                                                // local definition of socketData of type String

            while(true){
                socketDataString = in.readLine();                                                   // repeatedly read in data from socket into socketDataString
                if(socketDataString !=null){                                                        // if this read in data isn't null, print it to the console of MyWebServer
                    System.out.println(socketDataString);


                    out.println("Got your request!");                                               // tell the browser the request was received

                    //insert code from "Writing http headers" in Elliot MyWebServer Tips here
                }
                System.out.flush();                                                                 // clear the out buffer
                sock.close();                                                                       // close this current connection
            }
        } catch (IOException ioe) {
            System.out.println("Connection closed, rebooting and listening again...");                  // handles the IOException's and displays the error to the console
        }
    }
}

public class MyListener {

    public static void main(String a[]) throws IOException {                        // MyWebServer main
        int q_len = 6;                                                              // the amount of requests to hold in line before not accepting more requests, set to 6
        int port = 2540;                                                            // Use port=2540 for HTTP
        Socket sock;                                                                // Local JokeServer definition "sock" of type Socket

        ServerSocket servsock = new ServerSocket(port, q_len);                      // Local MyWebServer object declaration "servsock" as type ServerSocket that will wait for requests at port 43000 with possible 6 incoming connections

        System.out.println("MyWebServer starting up, listening at port 2540. \n");
        while (true) {
            sock = servsock.accept();                                               // continuously listening to set incoming connections to feed into our worker object
            new Worker(sock).start();                                               // launches a new worker object with the incoming connection
        }
    }
}