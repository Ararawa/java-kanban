package http;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
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

        preset();

        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PriorityHandler(manager));

        //httpServer.stop(5);
    }

    static void preset() {
        LocalDateTime startTime = LocalDateTime
                .parse("2022/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        Duration duration = Duration.ofMinutes(5);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task1);
        startTime = startTime.plus(duration).plus(duration);
        Task task2 = new Task("name2", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task2);
        startTime = startTime.plus(duration).plus(duration);
        Epic epic1 = new Epic("name3", "description1", TaskStatus.NEW);
        manager.create(epic1);
        startTime = startTime.plus(duration).plus(duration);
        Subtask subtask1 = new Subtask("name4", "description1", TaskStatus.DONE, 3,
                startTime, duration);
        manager.create(subtask1);
        startTime = startTime.plus(duration).plus(duration);
        Epic epic2 = new Epic("name5", "description1", TaskStatus.NEW);
        manager.create(epic2);
        startTime = startTime.plus(duration).plus(duration);
        Subtask subtask2 = new Subtask("name6", "description1", TaskStatus.IN_PROGRESS,
                5, startTime, duration);
        manager.create(subtask2);
        startTime = startTime.plus(duration).plus(duration);
        Subtask subtask3 = new Subtask("name7", "description1", TaskStatus.NEW,
                5, startTime, duration);
        manager.create(subtask3);
    }
}

