package com.example.servers.blocking;

import java.io.IOException;
import java.net.Socket;

/**
 * 
 * @author barala
 *
 */
public class BlockingServerClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        // thousand requests
        for(int i=0;i<10000;i++){
            Socket socket = new Socket("127.0.0.1", 8989);
        }

        while(true){
            Thread.sleep(1000);
        }
    }
}
