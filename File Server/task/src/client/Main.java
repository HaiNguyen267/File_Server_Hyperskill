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
