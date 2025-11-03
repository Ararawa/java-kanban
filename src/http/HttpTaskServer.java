package http;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {

    TaskManager manager;
    static String filename = "TestFBTM";
    static String suffix = ".csv";
    static HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public static void main(String[] args) throws Exception {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);

        httpServer.start();

//        preset();
//
//        httpServer.createContext("/tasks", new TaskHandler(manager));
//        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
//        httpServer.createContext("/epics", new EpicHandler(manager));
//        httpServer.createContext("/history", new HistoryHandler(manager));
//        httpServer.createContext("/prioritized", new PriorityHandler(manager));
    }

    void preset() {
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

    void presetTest() {
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

    public void setManager(String use) throws IOException {
        if (use.equals("file")) {
            manager = Managers.getFileManipulator(File.createTempFile(filename, suffix));
        } else if (use.equals("memory")) {
            manager = Managers.getDefault();
        }
    }

    public void setManagerTest(TaskManager managerTest) {
        manager = managerTest;
    }

    public void stopServer() {
        httpServer.stop(1);
    }

    public void startServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.start();
    }

    public void handlers() {
        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PriorityHandler(manager));
    }
}

