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
               String  message = reader.nextLine().strip();
                for (Socket s :user.keySet()){
                    if (s != socket){
                        sendResponse(user.get(socket) + ": " + message, getWriter(s));
                    }
                } if (isEmptyMsg(message)){
                    sendResponse("you can't write the empty message!!!", getWriter(socket));
                } else if (isQuitMsg(message)){
                    System.out.printf("the user %s",user.get(socket) + " left\n");
                    leftOfUser(socket);
                    break;

                }
            }
        } catch (NoSuchElementException e) {
            System.out.printf("the user %s",user.get(socket) + " left\n");
            leftOfUser(socket);
        } catch (IOException ioException) {
            throw new RuntimeException();
        }
    }


    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        return new PrintWriter(outputStream);
    }

    private  Scanner getReader(Socket socket) throws IOException{
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new Scanner(inputStreamReader);
    }

    private boolean isQuitMsg(String message){
        return "bye".equals(message);
    }

    private  boolean isEmptyMsg(String message){
        return message == null || message.isBlank();
    }

    private void sendResponse(String response,Writer writer) throws IOException{
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }

    private String generateRandomUser() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder user = new StringBuilder();
        Random random = new Random();
        int userNameLength = random.nextInt(3) + 4;
        for (int i = 0; i < userNameLength; i++) {
            user.append(alphabet.charAt(random.nextInt(26)));
        } return user.toString().toUpperCase().strip();
    }

    private void leftOfUser(Socket socket){
        try {
            for (Socket s : user.keySet()) {
                if (s != socket ) {
                    sendResponse("the user  " + user.get(socket) + "  left", getWriter(s));
                }
            } user.remove(socket);
        }catch (IOException e){
            throw new RuntimeException();
        }
    }

}
