package manager;

import org.junit.jupiter.api.*;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {

    @Override
    TaskManager createManager() {
        return Managers.getFileManipulator(file);
    }

    @BeforeEach @Override
    void setUp() {
        manager = createManager();
        super.setUp();
    }

    static String filename = "TestFBTM";
    static String suffix = ".csv";
    static Path path;
    static File file;

    @BeforeAll
    static void fileCreate() {
        try {
            path = Paths.get(filename + suffix);
            if (Files.notExists(path)) {
                file = File.createTempFile(filename, suffix);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Save test IOExсeption", e);
        }
    }

    @AfterAll
    static void fileDelete() {
        try {
            Files.delete(Paths.get(file.getName()));
        } catch (IOException e) {
            throw new ManagerSaveException("Save test IOExсeption", e);
        }
    }

    @Test
    void save() {
        ArrayList<Task> test1 = (ArrayList<Task>) manager.getAllTasks();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(Paths.get(file.getName()).toFile()), StandardCharsets.UTF_8))) {
            br.readLine();
            for (Task task: test1) {
                String line = br.readLine();
                String type;
                String epicID = "";
                String startTime = task.startTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));;
                String duration = String.valueOf(task.duration.toMinutes());
                if (task instanceof Subtask) {
                    epicID += ((Subtask) task).epicID;
                    type = String.valueOf(TaskType.SUBTASK);
                } else if (task instanceof Epic) {
                    type = String.valueOf(TaskType.EPIC);
                } else {
                    type = String.valueOf(TaskType.TASK);
                }
                String t1 = String.format("%s,%s,%s,%s,%s,%s,%s,%s", task.id, type, task.name, task.status,
                        task.description, startTime, duration, epicID);
                assertEquals(t1, line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Save test IOExсeption", e);
        }

    }

    @Test
    void taskToString() {
        manager.deleteAllTasks();
        startTime = startTime.plus(duration).plus(duration);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task1);
        Task task = manager.getByID(8);
        String epicID = "";
        String startTime = task.startTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        String duration = String.valueOf(task.duration.toMinutes());
        String type = String.valueOf(TaskType.TASK);
        String t1 = String.format("%s,%s,%s,%s,%s,%s,%s,%s", task.id, type, task.name, task.status,
                task.description, startTime, duration, epicID);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(Paths.get(file.getName()).toFile()), StandardCharsets.UTF_8))) {
            br.readLine();
            String line = br.readLine();
            assertEquals(t1, line);
        } catch (IOException e) {
            throw new ManagerSaveException("Save test IOExсeption", e);
        }
    }

    @Test
    void loadFromFile() {
        ArrayList<Task> test1 = (ArrayList<Task>) manager.getAllTasks();
        test1.forEach(System.out::println);
        manager = FileBackedTaskManager.loadFromFile(Paths.get(file.getName()).toFile());
        ArrayList<Task> test2 = (ArrayList<Task>) manager.getAllTasks();
        for (int i = 0; i < test1.size(); i++) {
            assertEquals(test1.get(i), test2.get(i));
            assertEquals(test1.get(i).toString(), test2.get(i).toString());
        }
    }

    @Test
    void fromString() {
        manager.deleteAllTasks();
        startTime = startTime.plus(duration).plus(duration);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task1);
        Task test1 = manager.getByID(8);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(Paths.get(file.getName()).toFile()), StandardCharsets.UTF_8))) {
            br.readLine();
            String line = br.readLine();
            Task test2 = FileBackedTaskManager.fromString(line);
            assertEquals(test1, test2);
            assertEquals(test1.toString(), test2.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Save test IOExсeption", e);
        }
    }
}