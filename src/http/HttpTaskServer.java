package http;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.net.InetSocketAddress;

public class HttpTaskServer {

    static TaskManager manager;

    public static void main(String[] args) throws Exception {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.start();
        manager = Managers.getDefault();

        httpServer.createContext("/path1", new TaskHandler(manager));
        httpServer.createContext("/path2", new SubtaskHandler(manager));
        httpServer.createContext("/path3", new EpicHandler(manager));
        httpServer.createContext("/path4", new HistoryHandler(manager));
        httpServer.createContext("/path5", new PriorityHandler(manager));

        httpServer.stop(5);
    }
}

