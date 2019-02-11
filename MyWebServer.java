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
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;


class Worker extends Thread {                               // Class declaration for Worker which will be a subclass of Thread class
    Socket sock;                                            // local Worker definition for sock of type Socket

    Worker(Socket s) {
        sock = s;                                           // constructor to accept the incoming sockets and set to local Socket definition called "sock"
    }

    public void run() {                                                                             // method launched with the .start() call in MyWebServer class

        try {
            PrintStream in = new PrintStream(sock.getOutputStream());                     //code from "Writing http headers" prout Elliot MyWebServer Tips
            BufferedReader prout = new BufferedReader(new InputStreamReader(sock.getInputStream()));                  // launched new objects to obtain our input


            String fromBrowserString = prout.readLine();                                                                 // local definition of socketData of type String

            if(fromBrowserString==null || fromBrowserString.length()==0) System.out.println("null found");                                            // check request secondary readlines for zero string length . Fixes index errors

            else {                                                                                                                                  // if this read prout data isn't null, print it to the console of MyWebServer

                String fileName = fromBrowserString.substring(4, fromBrowserString.length() - 9);

                if (fileName.equals("/favicon.ico")); //System.out.println("favicon found and ignored");

                else if (!fileName.contains(".ico")){
                    in.print("HTTP/1.1 200 OK");
                    in.print("Content-Length: " + 100000);
                    in.print("Content-type: " + "text/html" + "\r\n\r\n");
                    in.print("<pre><h1> Index of /MyWebServer/src </h1>");

                    //ReadFiles.java given code and MyWebServer Tips
                    File f1 = new File( "./"+fileName + "/");
                    File[] strFilesDirs = f1.listFiles();

                    if(strFilesDirs!=null) {
                        //generate dynamic html containing root directory contents
                        for (int i = 0; i < strFilesDirs.length; i++) {
                            if (strFilesDirs[i].isDirectory()) {
                                //System.out.println("get name = " + strFilesDirs[i].getName());
                                in.print("<a href=\"" + strFilesDirs[i].getName()  + "/\">/" + strFilesDirs[i].getName() + "/</a><br>");
                            } else if (strFilesDirs[i].isFile()) {
                               // System.out.println("get name = " + strFilesDirs[i].getName());
                                in.print("<a href=\"" + strFilesDirs[i].getName() + "\">" + strFilesDirs[i].getName() + "</a> (" + strFilesDirs[i].length() + ")<br>");
                            }
                        }
                    }

                    in.print("<h3><a href=" + "\"http://localhost:2540\"" + ">" + "Back to Home Directory" + "</a></h3>");


                    if (!fileName.equals("/") && f1.isFile()) {

                        InputStream readBrowserInput = new FileInputStream(fileName.substring(1,fileName.length()));        // remove leading slash
                        File browserFile = new File(fileName.substring(1,fileName.length()));                               //remove leading slash


                        if (fileName.endsWith(".txt") || fileName.endsWith(".java")) {
                            in.print("HTTP/1.1 200 OK");
                            in.print("Content-Length: " + browserFile.length());
                            in.print("Content-type: text/plain \r\n\r\n");                     // add custom function for parsing text/html vs plain/text

                            byte[] buffer = new byte[10000];
                            int bufferBytesRead = readBrowserInput.read(buffer);
                            System.out.println("number of bytes = " + bufferBytesRead);
                            in.write(buffer, 0, bufferBytesRead);
                            in.flush();
                            readBrowserInput.close();


                        } else if (fileName.endsWith(".html")){
                            in.print("HTTP/1.1 200 OK");
                            in.print("Content-Length: " + browserFile.length());
                            in.print("Content-type: " + "text/html" + "\r\n\r\n");                     // add custom function for parsing text/html vs plain/text

                            byte[] buffer = new byte[10000];
                            int bufferBytesRead = readBrowserInput.read(buffer);
                            System.out.println("number of bytes = " + bufferBytesRead);
                            in.write(buffer, 0, bufferBytesRead);
                            in.flush();
                            readBrowserInput.close();
                        }
                    }


                }
            }
            System.out.flush();                                                                     // clear the out buffer
            sock.close();                                                                           // close this current connection

        } catch (IOException ioe) {
            ioe.printStackTrace();
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