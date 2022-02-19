package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import static server.Main.map;
//public class Session implements Runnable {
//    private ExecutorService executor;
//    private ServerSocket server;
//    private Socket socket;
//
//    public Session(ExecutorService executor, ServerSocket server, Socket socket) {
//        this.executor = executor;
//        this.server = server;
//        this.socket = socket;
//    }
//    @Override
//    public void run() {
//
//        try (
//                DataInputStream input = new DataInputStream(this.socket.getInputStream());
//                DataOutputStream output = new DataOutputStream(this.socket.getOutputStream());
//        ) {
//            String request = input.readUTF();
//            if (request.equals("exit")) {
//                this.server.close();
//                this.executor.shutdown();
//
//            } else {
//                String[] para = request.split(",");
//                String type = para[0];
//
//                if (type.equals("get")) {
//                    handleGetRequest(para, input, output);
//                } if (type.equals("save")) {
//                    handleSaveRequest(para, input, output);
//                } else {
//                    handleDeleteRequest(para, input, output);
//                }
//            }
//
//        } catch (IOException e) {
//            System.out.println("session terminated");
//            e.printStackTrace();
//        }
//    }
//    private void handleGetRequest(String[] para, DataInputStream input, DataOutputStream output) throws IOException {
//        String typeOfFileName = para[1];
//        String fileName;
//        System.out.println("The entire request is: " + Arrays.toString(para));
//        // check the file exist or not
//        if (typeOfFileName.equals("name")) {
//            fileName = para[2];
//            System.out.println("the name you want to get is: " + fileName);
//            if (!Files.exists(Paths.get(DATA_DIR + fileName))) {
//                System.out.println("server not found: " + fileName);
//                output.writeUTF("404");
//                return;
//            }
//        } else {
//            String id = para[2];
//            if (!Main.map.containsKey(id)) {
//                output.writeUTF("404");
//                return;
//            } else {
//                fileName = Main.map.get(id);
//                if (!Files.exists(Paths.get(DATA_DIR + fileName))) {
//                    output.writeUTF("404");
//                    return;
//                }
//            }
//
//        }
//        // send the file to client
//
//        byte[] byteArray = Files.readAllBytes(Paths.get(DATA_DIR + fileName));
//        output.writeUTF("200"); // send the status code
//        output.writeInt(byteArray.length); // send the length of byte array
//        output.write(byteArray); // send the byte array
//
//        String s = input.readUTF();
//
//
//
//    }
//
//    private void handleSaveRequest(String[] para, DataInputStream input, DataOutputStream output) throws IOException {
//        Main.write.lock();
//
//        String fileNameToSave;
//
//        if (para.length == 2) {
//            // if user specify the name for the file to save
//            fileNameToSave = para[1];// para[0] is "save", para[1] is the name to save the file with
//            if (Files.exists(Paths.get(DATA_DIR + fileNameToSave))) {
//                output.writeUTF("403"); // if the name already exists
//                Main.write.unlock();
//                return;
//            }
//        } else {
//            fileNameToSave = generateNewUniqueName();
//        }
//
//        // receive the file from user
//        int length = input.readInt();
//        byte[] byteArray = new byte[length];
//        input.readFully(byteArray, 0, length);
//
//        Files.write(Paths.get(DATA_DIR + fileNameToSave), byteArray);
//
//
//        System.out.println("done the file creation, check the data folder");
//        Main.write.unlock();
//
//        String id = generateNewUniqueID();
//        Main.map.put(id, fileNameToSave);
//        output.writeUTF(String.format("200 %s", id));
//
//    }
//
//    private void handleDeleteRequest(String[] para, DataInputStream input, DataOutputStream output) throws IOException {
//        Main.write.lock();
//
//        String fileName;
//
//        // checking if there are any file having that id or name
//        if (para[1].equals("name")) {
//            fileName = para[2];
//
//            if (!Files.exists(Path.of(DATA_DIR + fileName))) {
//                output.writeUTF("404");
//                Main.write.unlock();
//                return;
//            }
//
//            removeFromMap(Main.map, fileName);
//        } else {
//            String id = para[2];
//
//            if (!Main.map.containsKey(id)) {
//                output.writeUTF("404");
//                Main.write.unlock();
//                return;
//            } else {
//                fileName = Main.map.get(id);
//                if (Files.exists(Path.of(DATA_DIR + fileName))) {
//                    Main.map.remove(id);
//                } else {
//                    output.writeUTF("404");
//                    Main.write.unlock();
//                    return;
//                }
//
//            }
//        }
//
//
//        Files.deleteIfExists(Paths.get(DATA_DIR + fileName));
//        output.writeUTF("200");
//
//        Main.write.unlock();
//
//    }
//
//    private void removeFromMap(Map<String, String> map, String fileName) {
//
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            if (entry.getValue().equals(fileName)) {
//                map.remove(entry.getKey());
//                return;
//            }
//        }
//    }
//
//    private String generateNewUniqueName() {
//        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//
//        StringBuilder randomName = new StringBuilder();
//
//        for (int i = 0; i < 12; i++) {
//            int randomIndex = (int) (Math.random() * alphabet.length());
//            randomName.append(alphabet.charAt(randomIndex));
//        }
//        randomName.append(".txt");
//        // if the random name exists already, recreate it
//        while (Main.map.containsValue(randomName.toString())) {
//            randomName = new StringBuilder();
//
//            for (int i = 0; i < 12; i++) {
//                int randomIndex = (int) (Math.random() * alphabet.length());
//                randomName.append(alphabet.charAt(randomIndex));
//            }
//            randomName.append(".txt");
//        }
//
//        return randomName.toString();
//    }
//
//    private String generateNewUniqueID() {
//        int range = 100;
//
//        // if there are more than 100 id existing in the map already, increase the range
//        if (Main.map.size() >= 100) {
//            range = Main.map.size() * 2;
//        }
//
//        int randomNum = (int) (Math.random() * range);
//        String randomId = Integer.toString(randomNum);
//
//        // if the id was used, recreate new id
//        if (Main.map.containsKey(randomId)) {
//            randomNum = (int) (Math.random() * range);
//            randomId = Integer.toString(randomNum);
//        }
//
//        return randomId;
//
//    }
//
//}
public class Session implements Runnable {
    private ServerSocket server;
    private Socket socket;
    private ExecutorService executor;

    public Session(ServerSocket server, Socket socket,ExecutorService executor) {
        this.server = server;
        this.socket = socket;
        this.executor = executor;
    }

    @Override
    public void run() {
        try (
                DataInputStream input = new DataInputStream(this.socket.getInputStream());
                DataOutputStream output = new DataOutputStream(this.socket.getOutputStream());
        ) {
            String request = input.readUTF();
            if (request.equals("exit")) {
                this.server.close(); // this line will cause an exception in the main of server/Main, which leads to the shutdown of server
                this.executor.shutdown();
            } else {
                String[] para = request.split(",");// split the parameters of the request by , symbol
                if (request.startsWith("get")) {
                    handleGetRequest(para, output); // handle the get request and send the result to the client
                } else if (request.startsWith("save")) {
                    handleSaveRequest(para, input, output);// handle the save request and send the result to the client
                } else {
                    handleDeleteRequest(para, output);// handle the delete request and send the result to the client
                }
                String responseOfClient = input.readUTF(); // after the client receive response of server, it sends the message "OK";
            }
        } catch (IOException e) {
            System.out.println("Error in Session");
            e.printStackTrace();
        }
    }

    private void handleDeleteRequest(String[] para, DataOutputStream output) throws IOException {

        String fileNameToDelete;

        if (para[1].equals("name")) {
            fileNameToDelete = para[2];
        } else {
            String id = para[2];
            fileNameToDelete = Main.map.getOrDefault(id, "file not found");
        }

        if (checkFileExists(fileNameToDelete)) {
            output.writeUTF("200"); // if the file exists on the server, then send the status code 200 to client
            String idToDelete = findIdOfFileName(fileNameToDelete); // find the id of the file name user wants to delete

            deleteFile(fileNameToDelete);
            Main.map.remove(idToDelete); // remove the id-filename of the file user wants to delete
        } else {
            output.writeUTF("404"); // file not found
        }
    }

    private void deleteFile(String fileNameToDelete) throws IOException {
        Files.deleteIfExists(Path.of(Main.SERVER_DATA_DIR + fileNameToDelete));
    }

    private String findIdOfFileName(String fileName) {
        for (Map.Entry<String, String> entry : Main.map.entrySet()) {
            if (entry.getValue().equals(fileName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void handleGetRequest(String[] para, DataOutputStream output) throws IOException {
        String fileName;

        if (para[1].equals("name")) {
            fileName = para[2];
        } else {
            String id = para[2];
            fileName = Main.map.getOrDefault(id, "file not found");
        }

        if (checkFileExists(fileName)) {
            output.writeUTF("200"); // if the file not exists in the server, then send the status code 200

            byte[] byteArray = readFile(fileName);
            output.writeUTF(String.format("%s", byteArray.length)); // send the length of the array
            output.write(byteArray);
        } else {
            output.writeUTF("404"); // if the file not exists in the server
        }
    }

    private void handleSaveRequest(String[] para, DataInputStream input, DataOutputStream output) throws IOException {
        String fileNameToSave;

        if (para.length == 2) {
            // if the user specify the fileName to save on server
            fileNameToSave = para[1];
        } else {
            fileNameToSave = generateUniqueName();
        }

        // if the file already exists on the server, return "403" status code;
        if (checkFileExists(fileNameToSave)) {
            output.writeUTF("403");
            return;
        }

        int length = Integer.parseInt(input.readUTF());// read the length of the byteArray
        byte[] byteArray = new byte[length];
        input.readFully(byteArray, 0, length);// read all the file

        createFile(fileNameToSave, byteArray); // create new file in the server

        String id = generateNewId();
        Main.map.put(id, fileNameToSave); // add the id and fileName to the map
        output.writeUTF("200"); // send the status code 200 to client
        output.writeUTF(id);// send the id of the saved file to client

    }

    private String generateNewId() {
        int range = 100;

        // if there are >= 100 files in the server
        if (Main.map.size() >= 100) {
            range = Main.map.size() * 2;
        }

        int randomId = (int) (Math.random() * range);

        // make sure the generated Id is unique
        while (Main.map.containsKey(Integer.toString(randomId))) {
            randomId = (int) (Math.random() * range);
        }

        return Integer.toString(randomId);
    }

    private String generateUniqueName() {

        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int range = 100;
        // if there are >= 100 files in the server
        if (Main.map.size() >= 100) {
            range = Main.map.size() * 2;
        }

        StringBuilder randomName = new StringBuilder();

        // generate the randomName
        for (int i = 0; i < 12; i++) {
            int randomIndex = (int) (Math.random() * alphabet.length());
            randomName.append(alphabet.charAt(randomIndex));
        }
        randomName.append(".txt");

        // make sure the generated name is unique
        while (Main.map.containsValue(randomName.toString())) {
            randomName = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                int randomIndex = (int) (Math.random() * alphabet.length());
                randomName.append(alphabet.charAt(randomIndex));
            }

        }
        return randomName.toString();
    }

    private byte[] readFile(String fileName) throws IOException {
        return Files.readAllBytes(Path.of(Main.SERVER_DATA_DIR + fileName));
    }

    private void createFile(String fileNameToSave, byte[] byteArray) throws IOException {
        Files.write(Path.of(Main.SERVER_DATA_DIR + fileNameToSave), byteArray);
    }

    private static boolean checkFileExists(String fileName) {
        return Files.exists(Path.of(Main.SERVER_DATA_DIR + fileName));
    }



}