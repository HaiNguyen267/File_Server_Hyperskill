package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    static String SERVER_DATA_DIR =  "C:\\Users\\NKcomputer\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\data\\";
    static Map<String, String> map = new HashMap<String, String>();
    static String fileMapPath = "C:\\Users\\NKcomputer\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\map.txt";
    public static void main(String[] args) {
        final String SERVER_ADDRESS = "127.0.0.1";
        final int PORT = 23456;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(SERVER_ADDRESS))) {
            loadMap();
            while (true) {
                Session session = new Session(server, server.accept(), executor); // when user enter "exit" request, the session will cause an exception by close the server socket, and the server will catch this exception be shutting down
                executor.submit(session);
            }
        } catch (IOException e) {
            updateMap();
            System.out.println("Error in server");
            e.printStackTrace();
            System.exit(0); // the server catches the exception caused by in session by terminating
        }
    }

    private static void loadMap() {
        if (Files.exists(Path.of(fileMapPath))) {
            try (BufferedReader br = new BufferedReader(new FileReader(fileMapPath))) {
                String line = br.readLine();

                while (line != null && !line.isEmpty()) {
                    String[] parts = line.split("=");
                    map.put(parts[0], parts[1]); // parts[0] is key, parts[0] is value
                    line = br.readLine();
                }

            } catch (IOException e) {
                System.out.println("error in loading the map");
                e.printStackTrace();
            }
        }
    }

    private static void updateMap() {
        if (!map.isEmpty()) {
            try (PrintWriter pw = new PrintWriter(fileMapPath)) {
                // write all key-value pairs to the file with the format "key=value"
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    pw.println(String.format("%s=%s", entry.getKey(), entry.getValue()));
                }
            } catch (IOException e) {
                System.out.println("error in updating the map");
                e.printStackTrace();
            }
        }
    }


}


//class Main2 {
//    static String DATA_DIR = System.getProperty("user.dir") + File.separator + "File Server" + File.separator + "task" + File.separator + "src" + File.separator + "server" + File.separator + "data" + File.separator;
//    static Map<String, String> map = new HashMap<String, String>();
//    //static String DATA_DIR = "C:\\Users\\NKcomputer\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\data\\";
//    static ReadWriteLock lock = new ReentrantReadWriteLock();
//    static Lock read = lock.readLock();
//    static Lock write = lock.writeLock();
//
//    public static void main(String[] args) {
//
//        loadTheMap();
//
//        System.out.println("map: " + map.toString());
//        final String SERVER_ADDRESS = "127.0.0.1";
//        final int PORT = 23456;
//        ExecutorService executor = Executors.newFixedThreadPool(10);
//
//        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(SERVER_ADDRESS))) {
//            System.out.println("Server started!");
//
//            while (true) {
//                Session session = new Session(executor, server, server.accept());
//                executor.submit(session); // if the user send exit request, an exception occurs and the main method will stop
//            }
//
//        } catch (IOException e) {
//            updateTheMap();
//            System.out.println("server stop noiw");
//            e.printStackTrace();
////            System.exit(0);
//        }
//        updateTheMap();
//
//    }
//
//
//
//    private static void loadTheMap(){
//
//        String mapFileName = "C:\\Users\\NKcomputer\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\map.txt";
//
//        if (new File(mapFileName).exists()) {
//
//            try {
//                String allText = new String(Files.readAllBytes(Paths.get(mapFileName)));
//                String[] entries = allText.split(",");
//
//                for (int i = 0; i < entries.length - 1; i+=2) {
//                    String key = entries[i];
//                    String value = entries[i+1];
//                    map.put(key,value);
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//    }
//
//    private static void updateTheMap() {
//        String mapFileName = "C:\\Users\\NKcomputer\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\map.txt";
//
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            sb.append(entry.getKey()).append(",").append(entry.getValue()).append(",");
//        }
//
//        try (PrintWriter printer = new PrintWriter(mapFileName)) {
//            printer.println(sb.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//
//}

