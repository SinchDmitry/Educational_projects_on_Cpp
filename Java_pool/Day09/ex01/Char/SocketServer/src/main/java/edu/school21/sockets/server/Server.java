package edu.school21.sockets.server;

import edu.school21.sockets.config.SocketsApplicationConfig;
import edu.school21.sockets.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


import edu.school21.sockets.app.Main;
@Component
public class Server extends Thread {
    private final UsersService service;
    private Socket socket;
    String username = null;
    String password = null;
    private final BufferedReader input;
    private final PrintWriter output;

    @Autowired
    public Server(Socket port) throws IOException {
        socket = port;
        InputStreamReader stream = new InputStreamReader(socket.getInputStream());
        input = new BufferedReader(stream);
        output = new PrintWriter(socket.getOutputStream(), true);
        ApplicationContext context =
                new AnnotationConfigApplicationContext(SocketsApplicationConfig.class);
        service = context.getBean(UsersService.class);
        serverStart();
    }

    public void run() {
        if (socket == null) {
            return;
        }
        String cmd;
        while (true) {
            try {
                if ((cmd = input.readLine()) != null) {
                    if (cmd.equals("Exit")) {
                        this.closeConnections();
                        break;
                    } else {
                        for (Server server : Main.servers) {
                            server.output.println(cmd);
                        }
                        service.saveMessage(username, cmd);
                    }
                }
            } catch (IOException e) {
                this.closeConnections();
                throw new RuntimeException(e);
            }
        }
    }
    public void serverStart() throws IOException {
        output.println("Hello from Server!");
        String tmp = input.readLine();
        switch (tmp) {
            case "signUp": signUp(); break;
            case "signIp": signIn(); break;
            case "exit": break;
            default: System.err.println("Error : wrong command"); break;
        }
    }

    public void signUp() throws IOException {
        output.println("Enter username:");
        if ((username = input.readLine()) != null) {
            output.println("Enter password:");
            if ((password = input.readLine()) != null) {
                if (service.signUp(username, password)) {
                    output.println("Successful!");
                } else {
                    output.println("Error : failed to sign up");
                }
            }
        }
    }

    public void signIn() throws IOException {
        output.println("Enter username:");
        output.flush();
        if ((username = input.readLine()) != null) {
            output.println("Enter password:");
            output.flush();
            if ((password = input.readLine()) != null) {
                if (service.signIn(username, password)) {
                    output.println("Successful!");
                    output.flush();
                } else {
                    output.println("Error : failed to sign up");
                }
            }
        }
    }

    private void closeConnections() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                input.close();
                output.close();
                for (Server server : Main.servers) {
                    if (server.equals(this)) {
                        server.interrupt();
                        Main.servers.remove(this);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
