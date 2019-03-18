package com.example.servers.blocking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author barala
 *
 */
public class BlockingServer {
    private static int PORT = 8989;
    private static final String REQUEST_EXIT = "exit";

    public static void main(String[] args) throws IOException {
        BlockingServer blockingServer = new BlockingServer();
        blockingServer.run();
    }

    public BlockingServer(int port) {
        this.PORT = port;
    }

    public BlockingServer() {
        // use default port 8989
    }

    public void run() throws IOException {
        try(ServerSocket socket = new ServerSocket(PORT)){
            //keeps running
            while(true) {
                //wait until it makes connection
                Socket establishedConnection = socket.accept();

                // assign established connection to particular thread.
                // In blocking model, one particular thread will be assigned to the established connection and
                // will be responsible for all the actions through out the life cycle of this established connection.
                new Thread(getRunnable(establishedConnection)).start();
            }
        }
    }

    private static Runnable getRunnable(Socket establishedConnection) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    PrintWriter out = new PrintWriter(establishedConnection.getOutputStream(), true);
                    InputStreamReader in = new InputStreamReader(establishedConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(in);

                    //read request and send response back
                    String request, response;
                    while((request = reader.readLine())!=null) {
                        response = handleRequest(request);
                        out.println(response);
                        if(REQUEST_EXIT.equals(request)){
                            break;
                        }
                    }
                    //close the established connection
                    establishedConnection.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

            private String handleRequest(String request) {
                System.out.println("processing request :: " + request);
                return "response to :: " + request;
            }
        };
    }
}