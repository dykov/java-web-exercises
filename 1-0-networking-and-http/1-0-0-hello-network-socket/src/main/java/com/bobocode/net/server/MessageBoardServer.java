package com.bobocode.net.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static com.bobocode.net.server.ServerUtil.*;

/**
 * {@link MessageBoardServer} is a server application that allows clients to connect via socket and print a message.
 * It opens a server socket on a given port and waits for a client to connect using a infinite loop. Once client is
 * connected this app reads a message from the connected socket input stream and prints it to the console.
 * <p>
 * It uses only one thread. So if multiple clients will try to connect at the same time, they will be waiting and will
 * be processed one by one.
 */
public class MessageBoardServer {
    public static final String HOST = ServerUtil.getLocalHost();
    public static final int PORT = 8899; // you can use any free port you want
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            while(true) {
                try(
                        final Socket clientSocket = serverSocket.accept();
                        final InputStreamReader reader = new InputStreamReader(clientSocket.getInputStream());
                        final BufferedReader bufferedReader = new BufferedReader(reader)
                ) {
                    final String message = bufferedReader.lines().collect(Collectors.joining());

                    printMessage(clientSocket, message);
                }
            }
        }
    }

    /**
     * Simple message that allows to print message using a nice and informative format. Apart from the text message
     * itself, it also prints the time and client address.
     *
     * @param socket  accepted socket
     * @param message a text message to print
     */
    public static void printMessage(Socket socket, String message) {
        final String time = LocalDateTime.now().format(TIME_FORMATTER);
        String clientAddress = socket.getInetAddress().getHostAddress();
        final String messageData = String.format("[%s][%s]: %s", time, clientAddress, message);
        System.out.println(messageData);
    }

}
