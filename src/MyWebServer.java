/*

1. Jeff Wiand / 2-6-19
2. Java 1.8
3. Compilation Instructions:
    > javac MyWebServer.java

4. Run Instructions
    // > java MyWebServer

   List of files needed for running the program
    - MyWebServer.java
    - http-streams.txt
    - serverlog.txt
    - checklist-mywebserver.html
    - addnums.html

5. My Notes
    - could not get a "return to home directory" hyperlink to print on the browser screen when I was in a non-directory file (ie. dog.txt / cat.html / MyWebServer.java)
*/


import java.io.*;                                           //Pull in the Java Input - Output libraries for MyWebServer.java use
import java.net.*;                                          //Pull in the Java networking libraries for MyWebServer.java use


class Worker extends Thread {                               // Class declaration for Worker which will be a subclass of Thread class
    Socket sock;                                            // local Worker definition for sock of type Socket

    Worker(Socket s) {
        sock = s;                                           // constructor to accept the incoming sockets and set to local Socket definition called "sock"
    }

    public void run() {                                                                             // method launched with the .start() call in MyWebServer class

        try {
            PrintStream in = new PrintStream(sock.getOutputStream());                                                  // starter code from "Writing http headers" prout Elliot MyWebServer Tips
            BufferedReader prout = new BufferedReader(new InputStreamReader(sock.getInputStream()));                  // launched new objects to obtain our input

            String fromBrowserString = prout.readLine();                                                                 // local definition of socketData of type String

            if(fromBrowserString==null || fromBrowserString.length()==0) System.out.println("null found");         // check request secondary readlines for zero string length . Fixes index errors

            else {                                                                                                                                  // if this read prout data isn't null, print it to the console of MyWebServer

                String fileName = fromBrowserString.substring(4, fromBrowserString.length() - 9);                                                   // parse the fileName string for the "fromBrowserString" above use .substring method

                if (fileName.equals("/favicon.ico")); //System.out.println("favicon detected and ignored");                                            // favicon detector, catch and do nothing but print statement to console

                else {
                    if (fileName.endsWith(".txt") || fileName.endsWith(".java")) {                                          // send text/plain content type HTTP header for .txt and .java files
                        System.out.println(fileName);
                        String contentType = "text/plain";
                        fileToBrowser(fileName, contentType, in);
                    }
                    else if (fileName.endsWith(".html")){                                                                       // send text/html content type HTTP header for .html files
                        System.out.println(fileName);
                        String contentType = "text/html";
                        fileToBrowser(fileName, contentType, in);
                    }
                    else if (fileName.contains("cgi")){                                                                          // if the request from the browser contains the CGI designation, call the addNums function
                        System.out.println(fileName);
                        String contentType = "text/html";
                        myAddNums(fileName, contentType, in);                                                                    // print the addNums result to the browser
                    }
                    else if (fileName.endsWith("/")){
                        System.out.println(fileName);
                        String contentType = "text/html";                                                                   // send text/html content type HTTP header for .html files containing directories
                        dirToBrowser(fileName, contentType, in);
                    }
                    else if(!fileName.endsWith("/")){                                                                       // send text/plain HTTP header for the remaining cases that filter down through this conditional block
                        System.out.println(fileName);
                        String contentType = "text/plain";
                        fileToBrowser(fileName, contentType, in);
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


    public void dirToBrowser(String fileName, String contentType, PrintStream in){
        in.print("HTTP/1.1 200 OK");                                                                                                    // send html HTTP header to print directory and header
        in.print("Content-Length: " + 100000);                                                                                          // fake our content length to 10000 to ensure all contents are printed to browser
        in.print("Content-type: " + contentType + "\r\n\r\n");                                                                          // send text/html content type to print html header


        //ReadFiles.java given code and MyWebServer Tips
        File f1 = new File( "./"+fileName + "/");                                                                              // declare File f1 to be used in directory generation
        File[] fileDirectoryArr = f1.listFiles();                                                                                           // File array to hold all files listed

        if(fileDirectoryArr!=null) {
            //generate dynamic html containing directory and file contents if the array of files is not null
            for (int i = 0; i < fileDirectoryArr.length; i++) {
                if (fileDirectoryArr[i].isDirectory()) {
                    in.print("<a href=\"" + fileDirectoryArr[i].getName()  + "/\">/" + fileDirectoryArr[i].getName() + "/</a><br>");
                } else if (fileDirectoryArr[i].isFile()) {
                    in.print("<a href=\"" + fileDirectoryArr[i].getName() + "\">" + fileDirectoryArr[i].getName() + "</a> (" + fileDirectoryArr[i].length() + ")<br>");
                }
            }
        }

        in.print("<h3><a href=" + "\"http://localhost:2540\"" + ">" + "Return to Root of MyWebServer" + "</a></h3>");                           //prints a link to the home directory on every page

    }



    public void fileToBrowser(String fileName, String contentType, PrintStream in) {
        File f0 = new File( fileName);                                                                                       // declare File f1 to be used in directory generation

        if (!fileName.equals("/") && !f0.isFile()) {                                                                          //if the fileName doesn't end with a / and it's a file, enter the conditional
            try{
                InputStream readBrowserInput = new FileInputStream(fileName.substring(1, fileName.length()));                                // remove leading slash
                File browserFile = new File(fileName.substring(1, fileName.length()));                                                       //remove leading slash

                in.print("HTTP/1.1 200 OK");                                                                        // send HTTP header with correct length and content type to browser
                in.print("Content-Length: " + browserFile.length());
                in.print("Content-type: "+ contentType + "\r\n\r\n");

                byte[] buffer = new byte[10000];                                                                    // create buffer to hold contents of fileName
                int bufferBytesRead = readBrowserInput.read(buffer);                                                // read in all of fileName contents
                in.write(buffer, 0, bufferBytesRead);                                                               // write the buffer to the browser
                in.flush();                                                                                         // clear the buffer
                readBrowserInput.close();                                                                           // close the InputStream

            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.out.println("Connection closed, rebooting and listening again...");                  // handles the IOException's and displays the error to the console
            }
        }
    }

    public void myAddNums(String fileName, String contentType, PrintStream in){                                                                 // custom addNums function that returns the sum of two input numbers to browser

        String fileNameTrimmed = fileName.substring(22,fileName.length());                                  // parse the initial 21 characters off the fileName string
        String [] nameNums = fileNameTrimmed.split("[=&]");                                                 // split person, yourName, num1, input of num1, num2, input of num2 into String array

        String name = nameNums[1];                                                                          // local definition of name, set name input to nameNum[1] index of type String
        String num1 = nameNums[3];                                                                          // local definition of num1, set num1 input to nameNum[3] index of type String
        String num2 = nameNums[5];                                                                          // local definition of num2, set num2 input to nameNum[5] index of type String
        int numSum = Integer.parseInt(num1) + Integer.parseInt(num2);                                       // local definition of numSum, convert num1 and num2 Strings to Integers and add them

        String replytoBrowser = "Dear "+name+", the sum of "+num1+" and "+num2+" is "+numSum;               // local definition of replyToBrowser of type String
        int replyToBrowserLen = replytoBrowser.length();

        in.print("HTTP/1.1 200 OK");
        in.print("Content-Length: " + replyToBrowserLen);
        in.print("Content-type: " + contentType + "\r\n\r\n");
        in.print("<p>"+replytoBrowser+"</p>");                                                 // print the replyToBrowser to the browser as formatted HTML
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