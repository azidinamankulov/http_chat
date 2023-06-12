import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {


    private final int port;

    private final ExecutorService pool = Executors.newCachedThreadPool();

    private EchoServer(int port){
        this.port = port;

    }

    public static EchoServer bindToPort(int port){
        return new EchoServer(port);
    }

    public void run(){
        Server serverOf = new Server();
        try(var server = new ServerSocket(port)) {
            while (!server.isClosed()){
                Socket clientSocket = server.accept();
                pool.submit(() -> {
                    try {
                        serverOf.handle(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            System.out.printf("Probably port %s busy %n",port);
            throw new RuntimeException(e);
        }
    }


}

