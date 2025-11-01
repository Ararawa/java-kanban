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

        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/priority", new PriorityHandler(manager));

        httpServer.stop(5);
    }
}

