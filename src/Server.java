import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {
    private final Map<Socket, String> user = new HashMap<>();

    public void handle(Socket socket) throws IOException {
        String userName = generateRandomUser();

        System.out.printf("Connected new user: %s%n", userName);
        try (socket;
             var reader = getReader(socket);
             PrintWriter writer = getWriter(socket)) {
            user.put(socket, userName);
            sendResponse("Hi it's your nickname: " + userName, writer);
            while (true) {
                var message = reader.nextLine().strip();
                for (Socket s :user.keySet()){
                    if (s != socket){
                        sendResponse(user.get(socket) + ": " + message, getWriter(s));
                    }
                }
                if (isEmptyMsg(message) || isQuitMsg(message)){
                    sendResponse("YOU CAN'T WRITE EMPTY MESSAGE", getWriter(socket));
                }
            }
        } catch (NoSuchElementException e) {
            for (Socket s : user.keySet()) {
                if (s != socket) {
                    sendResponse("THE " + user.get(socket) + " HAS LEFT FROM CHAT.", getWriter(s));
                }
            }
            System.out.printf("The user %s",user.get(socket) + " left");
            user.remove(socket);
        } catch (IOException ioException) {
            throw new RuntimeException();
        }
    }


    private static PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        return new PrintWriter(outputStream);
    }

    private static Scanner getReader(Socket socket) throws IOException{
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new Scanner(inputStreamReader);
    }

    private static boolean isQuitMsg(String msg){
        return "bye".equalsIgnoreCase(msg);

    }

    public static boolean isEmptyMsg(String msg){
        return msg == null || msg.isBlank();
    }

    private static void sendResponse(String response,Writer writer) throws IOException{
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }

    static String generateRandomUser() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder user = new StringBuilder();
        Random random = new Random();
        int userNameLength = random.nextInt(3) + 4;
        for (int i = 0; i < userNameLength; i++) {
            user.append(alphabet.charAt(random.nextInt(26)));
        }
        return user.toString().toUpperCase().strip();
    }

}
