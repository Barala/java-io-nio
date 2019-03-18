package com.example.servers.non.blocking;

import java.io.IOException;
import java.net.Socket;

/**
 * 
 * @author barala
 *
 */
public class NonBlockingServerClient {
    // make sure non blocking server is running on port 8990
    public static void main(String[] args) throws IOException, InterruptedException {
        // thousand requests
        for(int i=0;i<10000;i++){
            Socket socket = new Socket("127.0.0.1", 8990);
        }

        while(true){
            Thread.sleep(1000);
        }
    }
}
