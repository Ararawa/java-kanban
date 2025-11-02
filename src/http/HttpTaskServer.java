package http;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {

    static TaskManager manager;
    static String filename = "TestFBTM";
    static String suffix = ".csv";

    public static void main(String[] args) throws Exception {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.start();
        manager = Managers.getFileManipulator(File.createTempFile(filename, suffix));

        LocalDateTime startTime = LocalDateTime
                .parse("2022/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        Duration duration = Duration.ofMinutes(5);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task1);
        startTime = startTime.plus(duration).plus(duration);
        Task task2 = new Task("name2", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task2);

        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/priority", new PriorityHandler(manager));

        //httpServer.stop(5);
    }
}

