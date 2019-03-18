package com.example.servers.non.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * 
 * @author barala
 *
 */
public class NonBlockingServer {
    private static int PORT = 8990;
    private Selector selector;

    public static void main(String[] args) throws IOException {
        NonBlockingServer nonBlockingServer = new NonBlockingServer();
        nonBlockingServer.runServer();
    }

    public NonBlockingServer() {
        //use default port
    }

    public NonBlockingServer(int port) {
        this.PORT = port;
    }

    public void runServer() throws IOException{
        this.selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(getSocketAddress());
        //register channel to selector
        serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        //unlike IO blocking server we won't spawn new thread for each coming request
        while(true) {
            int totalIncomingRequsts = this.selector.select();
            if(totalIncomingRequsts == 0) {
                //no event to process
                continue;
            }

            // Process existing keys in selector
            Set<SelectionKey> existingKeys = this.selector.selectedKeys();
            for(SelectionKey existingKey : existingKeys) {
                //check validity of the key
                if(existingKey.isValid()){
                    //check for all type of the tickets
                    if(existingKey.isAcceptable()) {
                        this.acceptConnection(existingKey);
                    } else if(existingKey.isReadable()) {
                        this.readFromConnection(existingKey);
                    } // there are other types too
                }
                existingKeys.remove(existingKey);
            }
        }
    }

    private SocketAddress getSocketAddress() {
        return new InetSocketAddress("127.0.0.1", PORT);
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        Socket socket = socketChannel.socket();

        System.out.println("connected to: " + socket.getRemoteSocketAddress());

        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    private void readFromConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        int totalReads = socketChannel.read(buffer);
        if(totalReads == -1) {
            Socket socket = socketChannel.socket();
            System.out.println("connection closed by :: " + socket.getRemoteSocketAddress());
            socketChannel.close();
            key.cancel();
            return;
        }

        byte[] request = new byte[totalReads];
        System.arraycopy(buffer.array(), 0, request, 0, totalReads);
        System.out.println("got request :: " + new String(request));

        writeBackToConnection(key);
    }

    private void writeBackToConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buff = ByteBuffer.allocate(1024);
        String response = "Pong Pong \n";
        buff.put(response.getBytes());
        buff.flip();
        socketChannel.write(buff);
    }
}