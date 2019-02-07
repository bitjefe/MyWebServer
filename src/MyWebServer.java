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
import java.util.*;


class Worker extends Thread {                               // Class declaration for Worker which will be a subclass of Thread class
    Socket sock;                                            // local Worker definition for sock of type Socket

    Worker(Socket s) {
        sock = s;                                           // constructor to accept the incoming sockets and set to local Socket definition called "sock"
    }

    public void run() {                                                                             // method launched with the .start() call in MyWebServer class

        BufferedReader in = null;                                                                   // sets our input to null

        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));                  // launched new objects to obtain our input

            OutputStream out = new BufferedOutputStream(sock.getOutputStream());                     //code from "Writing http headers" in Elliot MyWebServer Tips
            PrintStream prout = new PrintStream(out, true);                                 // code from "Writing http headers" in Elliot MyWebServer Tips

            String socketDataString;                                                                 // local definition of socketData of type String

            socketDataString = in.readLine();                                                       // repeatedly read in data from socket into socketDataString
            System.out.println(socketDataString);

            while (true) {

                String zeroLengthStringChecker = in.readLine();

                if(zeroLengthStringChecker==null || zeroLengthStringChecker.length()==0) break;                     // check request secondary readlines for zero string length and break out of loop if found. Fixes index errors

                else if (socketDataString != null || socketDataString.length() != 0) {                                                       // if this read in data isn't null, print it to the console of MyWebServer

                    String contentParser = socketDataString.substring(4, socketDataString.length() - 9);

                    if (contentParser.equals("/favicon.ico")) break;

                    else {

                        //ReadFiles.java given code
                        String filedir;
                        File f1 = new File( "./src");
                        File[] strFilesDirs = f1.listFiles();

                        prout.println("HTTP/1.1 200 OK");
                        prout.println("Content-Length: " + 1000);
                        prout.println("Content-type: " + "text/plain" + "\r\n\r\n");

                        for(int i=0; i<strFilesDirs.length;i++){
                            if(strFilesDirs[i].isDirectory()){
                                prout.println("Directory: " + strFilesDirs[i]);
                            }else if(strFilesDirs[i].isFile()){
                                prout.println("File: " + strFilesDirs[i] + " ("+strFilesDirs[i].length()+ ")");
                            }
                        }

                    /*
                        prout.println("<pre> " +
                                    "<h1> Index of /MyWebServer/src </h1>" +
                                    "<a href= \"dog.txt\">dog.txt</a> <br>" +
                                    "<a href= \"cat.html\">cat.html</a> <br>" +
                                    "<a href= \"MyWebServer.java\">MyWebServer.java</a> <br>" +
                                    "</pre>");
                    */

                        if (contentParser.endsWith(".txt")) {

                            prout.println("HTTP/1.1 200 OK");
                            prout.println("Content-Length: " + socketDataString.length());
                            prout.println("Content-type: " + "text/plain" + "\r\n\r\n");                     // add custom function for parsing text/html vs plain/text

                            prout.println("Dog text file");

                        } else if (contentParser.endsWith(".html")){
                            prout.println("HTTP/1.1 200 OK");
                            prout.println("Content-Length: " + socketDataString.length());
                            prout.println("Content-type: " + "text/html" + "\r\n\r\n");                     // add custom function for parsing text/html vs plain/text

                            prout.println("<h2>Cat html file</h2>");
                        }
                    }
                }
                System.out.flush();                                                                     // clear the out buffer
                sock.close();                                                                           // close this current connection
            }
        } catch (IOException ioe) {
            System.out.println("Connection closed, rebooting and listening again...");                  // handles the IOException's and displays the error to the console
        }
    }
}

public class MyWebServer {

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