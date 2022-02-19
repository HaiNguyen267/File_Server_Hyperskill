package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

//class Main2 {
//    //static String DATA_DIR = System.getProperty("user.dir") + File.separator + "File Server" + File.separator + "task" + File.separator + "src" + File.separator + "client" + File.separator + "data" + File.separator;
//    static String DATA_DIR = "C:\\Users\\NKcomputer\\IdeaProjects\\File Server\\File Server\\task\\src\\client\\data\\";
//    public static void main(String[] args) {
//        final String SERVER_ADDRESS = "127.0.0.1";
//        final int PORT = 23456;
//        Scanner sc = new Scanner(System.in);
//
//
//            System.out.println("Client started!");
//
//            try (
//                    Socket socket = new Socket(SERVER_ADDRESS, PORT);
//                    DataInputStream input = new DataInputStream(socket.getInputStream());
//                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
//            ) {
//                System.out.println("Enter action (1 - get a file, 2 - save a file, 3 - delete a file):");
//                StringBuilder request = new StringBuilder();
//                String userInput = sc.nextLine();
//
//                if (userInput.equals("exit")) {
//                    output.writeUTF("exit");
//                    System.out.println("The request was sent.");
//                } else {
//                    String action = userInput.equals("1") ? "get" : userInput.equals("2") ? "save" : "delete";
//                    request.append(action);
//                    String statusCode = "";
//                    if (action.equals("get")) {
//                        statusCode = sendGetRequest(input, output);
//                    } else if (action.equals("save")) {
//                        statusCode = sendSaveRequest(input, output);
//                    } else {
//                        statusCode = sendDeleteRequest(input, output);
//                    }
//
//
//                    // parse message from status code received from server
//                    String messageToPrint = parseMessage(statusCode, action);
//                    System.out.println(messageToPrint);
//                }
//
//            } catch (IOException e) {
//                System.out.println("client terminated");
//                e.printStackTrace();
//            }
//
//
//    }
//
//    private static String sendGetRequest(DataInputStream input, DataOutputStream output) throws IOException {
//        Scanner sc = new Scanner(System.in);
//
//        // preparing the request string
//        StringBuilder request = new StringBuilder();
//        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id): ");
//
//        if (sc.nextLine().equals("1")) {
//            System.out.println("Enter name:");
//            request.append("get").append(",name,").append(sc.nextLine()); // first, second and third parameters separated by ","
//        } else {
//            System.out.println("Enter id:");
//            request.append("get").append(",id,").append(sc.nextLine()); // first, second and third parameters separated by ","
//        }
//
//        // send request to server
//        output.writeUTF(request.toString());
//        System.out.println("The request was sent.");
//
//        // receive response from server
//        String statusCode = input.readUTF();
//
//        if (statusCode.equals("200")) {
//            // read the byte array of the file
//            int length = input.readInt();
//            byte[] byteArray = new byte[length];
//
//            input.readFully(byteArray,0, byteArray.length);
//            System.out.println("The file was downloaded! Specify a name for it:");
//            String fileName = sc.nextLine();
//
//            // saving the file
//            Files.write(Path.of(DATA_DIR + fileName), byteArray);
//
//            return "200";
//        } else {
//            return statusCode; // status code is 404 if the file not found by the server
//        }
//
//    }
//
//    private static String sendSaveRequest(DataInputStream input, DataOutputStream output) throws IOException {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Enter name of the file:");
//        String fileName = sc.nextLine();
//        // if the file not exist yet, ask user to enter the content, create it before sending at to the server
//
//        if (!Files.exists(Paths.get(DATA_DIR + fileName))) {
//            System.out.println("Enter file content:");
//            String content = sc.nextLine();
//            System.out.println(DATA_DIR + fileName);
//            Files.write(Paths.get(DATA_DIR + fileName), content.getBytes(StandardCharsets.UTF_8));
//        }
//
//        System.out.println("Enter name of the file to be saved on server:");
//        String fileToSave = sc.nextLine();
//
//        StringBuilder request = new StringBuilder();
//
//        // add parameters separated by "," to the request string
//        // third parameter
//        if (fileToSave.length() == 0) {
//            System.out.println("no name was specified");
//            request.append("save"); // if user doesn't want to specify the name and just press enter, then add "empty" as the third parameter to the request string
//        } else {
//            request.append("save").append(",").append(fileToSave); // if user specify the name, then add the name to the request string
//            System.out.println("you specified the name: " + request.toString());
//        }
//
//        // send the request string
//        output.writeUTF(request.toString());
//
//        // send the file
//        byte[] byteArray = Files.readAllBytes(Paths.get(DATA_DIR + fileName)); // convert the file user want to send to server to byte array
//        output.writeInt((byteArray.length)); // send the length of the byte array first
//        output.write(byteArray); // send the byte array
//
//        String response = input.readUTF();// the server will send back the status code 200 followed an id if success, or status code 404 if file not found in the server
//        return  response;
//
//    }
//
//    private static String sendDeleteRequest(DataInputStream input, DataOutputStream output) throws IOException {
//        Scanner sc = new Scanner(System.in);
//        StringBuilder request = new StringBuilder();
//
//        // preparing the request string
//
//        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id): ");
//        if (sc.nextLine().equals("1")) {
//            System.out.println("Enter name:");
//            request.append("delete").append(",name,").append(sc.nextLine());// first, second and third parameters separated by ","
//        } else {
//            System.out.println("Enter id:");
//            request.append("delete").append(",id,").append(sc.nextLine()); // first, second and third parameters separated by ","
//        }
//
//        // send the request string
//        output.writeUTF(request.toString());
//
//        // receive the response from the server
//        String statusCode = input.readUTF();
//        return statusCode; // 200 if successfully deleted the file, 404 if the file was not found by the server
//    }
//
//    private static String parseMessage(String serverStatusCode, String action) {
//        int statusCode = Integer.parseInt(serverStatusCode.substring(0, 3));
//        String message = "";
//
//        if (statusCode == 200) {
//            if (action.equals("get")) {
//                message = "File saved on the hard drive!";
//            } else if (action.equals("save")) {
//                // if the file was successfully saved, the server sent a status code followed by an id for that file
//                String id = serverStatusCode.substring(4); // index [0-2] is the status code, index 3 is space, the id starts from index 4
//                message = String.format("Response says that file is saved! ID = %s", id);
//            } else if (action.equals("delete")) {
//                message = "The response says that this file was deleted successfully!";
//            }
//        } else if (statusCode == 403) {
//            message = "The response says that creating the file was forbidden!";
//        } else if (statusCode == 404) {
//            message = "The response says that this file is not found!";
//        }
//
//        return message;
//    }
//}

public class Main{
  static String CLIENT_DATA_DIR = "C:\\Users\\NKcomputer\\IdeaProjects\\File Server\\File Server\\task\\src\\client\\data\\";

    public static void main(String[] args) {
        final String SERVER_ADDRESS = "127.0.0.1";
        final int PORT = 23456;

        try (
                Socket socket = new Socket(SERVER_ADDRESS, PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter action (1 - get a file, 2 - save a file, 3 - delete a file):");
            String option = sc.nextLine();

            if (option.equals("exit")) {
                output.writeUTF("exit");
            } else {
                String action =  option.equals("1") ? "get" : option.equals("2") ? "save" : "delete";
                if (action.equals("get")) {
                    sendGetRequest(output);
                } else if (action.equals("save")) {
                    sendSaveRequest(output);
                } else {
                    sendDeleteRequest(output);
                }

                String message = receiveServerResponse(action, input);
                output.writeUTF("OK");
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Error in client");
            e.printStackTrace();
        }
    }

    private static String receiveServerResponse(String action, DataInputStream input) throws IOException {

        String statusCode = input.readUTF();
        // if the request was successfully processed
        if (statusCode.equals("200")) {
            if (action.equals("get")) {
                Scanner sc = new Scanner(System.in);
                int length = Integer.parseInt(input.readUTF()); // read the length of the byteArray sent by the server
                byte[] byteArray = new byte[length];
                input.readFully(byteArray, 0, length); // read the byteArray sent by the server

                System.out.println("The file was downloaded! Specify a name for it:");
                String fileNameToSave = sc.nextLine();
                createFile(fileNameToSave, byteArray);
                return "File saved on the hard drive!"; // the message to display

            } else if (action.equals("save")) {
                String id = input.readUTF(); // read the id sent by the server
                return String.format("Response says that file is saved! ID = %s", id);// the message to display
            } else {
                return "The response says that this file was deleted successfully!";// the message to display
            }
        } else if (statusCode.equals("403")) {
            return "The response says that creating the file was forbidden!"; // if the action is save, and the filename is already existed on server;
        } else {
            return "The response says that this file is not found!"; // statusCode == 404, if the file name cannot be found on the server
        }
    }

    private static boolean checkFileExists(String fileName) {
        return Files.exists(Path.of(CLIENT_DATA_DIR + fileName));
    }

    private static void createFile(String fileName, byte[] bytes) throws IOException {
        Files.write(Path.of(CLIENT_DATA_DIR + fileName), bytes);
    }

    private static void sendGetRequest(DataOutputStream output) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id):");
        String type = sc.nextLine().equals("1") ? "name" : "id";

        System.out.println(String.format("Enter %s:", type));
        String identifier = sc.nextLine(); // identifier can be a name or id

        String getRequest = String.format("get,%s,%s", type, identifier);
        output.writeUTF(getRequest);
        System.out.println("The request was sent.");
    }

    private static void sendSaveRequest(DataOutputStream output) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter name of the file:");
        String fileName = sc.nextLine();

        // if the file doesn't exist in the client data folder, ask user to enter the content and create the file
        if (!checkFileExists(fileName)) {
            System.out.println("Enter file content: ");
            String content = sc.nextLine();
            createFile(fileName, content.getBytes(StandardCharsets.UTF_8));
        }

        // ask the user to enter the name that the file will be saved on server with
        System.out.println("Enter name of the file to be saved on server: ");
        String fileNameToSave = sc.nextLine();

        // send the saveRequest to server
        String saveRequest = String.format("save,%s", fileNameToSave);
        output.writeUTF(saveRequest);

        // send the file to server
        byte[] byteArray = Files.readAllBytes(Paths.get(CLIENT_DATA_DIR + fileName));
        output.writeUTF(String.format("%s", byteArray.length));
        output.write(byteArray);

        System.out.println("The request was sent.");
    }

    private static void sendDeleteRequest(DataOutputStream output) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Do you want to delete the file by name or by id (1 - name, 2 - id):");
        String type = sc.nextLine().equals("1") ? "name" : "id";

        System.out.println(String.format("Enter %s:", type));
        String identifier = sc.nextLine();

        String deleteRequest = String.format("delete,%s,%s", type, identifier);
        output.writeUTF(deleteRequest);
        System.out.println("The request was sent.");
    }
}