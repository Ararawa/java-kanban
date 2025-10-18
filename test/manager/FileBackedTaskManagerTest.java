package manager;

import org.junit.jupiter.api.*;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    TaskManager manager;
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

    @BeforeEach
    void setUp() {
        manager = Managers.getFileManipulator(file);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW);
        manager.create(task1);
        Task task2 = new Task("name2", "description1", TaskStatus.NEW);
        manager.create(task2);
        Epic epic1 = new Epic("name3", "description1", TaskStatus.NEW);
        manager.create(epic1);
        Subtask subtask1 = new Subtask("name4", "description1", TaskStatus.DONE, 3);
        manager.create(subtask1);
        Epic epic2 = new Epic("name5", "description1", TaskStatus.NEW);
        manager.create(epic2);
        Subtask subtask2 = new Subtask("name6", "description1", TaskStatus.IN_PROGRESS, 5);
        manager.create(subtask2);
        Subtask subtask3 = new Subtask("name7", "description1", TaskStatus.NEW, 5);
        manager.create(subtask3);
    }

    @AfterEach
    void cleanUp() {
        manager.deleteAllTasks();
    }

    @Test
    void getAllTasks() {
        ArrayList<Task> test1 = (ArrayList<Task>) manager.getAllTasks();
        for (Task task : test1) {
            assertEquals(manager.getByID(task.id), task);
        }
    }

    @Test
    void deleteAllTasks() {
        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void getByID() {
        Task test1 = new Task("name1", "description1", TaskStatus.NEW);
        test1.id = 1;
        Task test2 = manager.getByID(1);
        assertEquals(test1, test2);
    }

    @Test
    void create() {
        Task test1 = new Task("test1", "test1desc", TaskStatus.DONE);
        manager.create(test1);
        assertNotNull(manager.getByID(8));
        assertEquals(8, manager.getByID(8).id);
        assertEquals("test1", manager.getByID(8).name);
        assertEquals("test1desc", manager.getByID(8).description);
        assertEquals(TaskStatus.DONE, manager.getByID(8).status);
    }

    @Test
    void update() {
        Task test1 = manager.getByID(6);
        Subtask subtask4 = new Subtask("n7", "destion1", TaskStatus.DONE, 5);
        subtask4.setId(6);
        manager.update(subtask4);
        Task test2 = manager.getByID(6);
        assertEquals(test1, test2);
    }

    @Test
    void deleteByID() {
        Task test1 = manager.getByID(1);
        manager.deleteByID(1);
        Task test2 = manager.getByID(1);
        assertNotEquals(test1, test2);
        assertNull(test2);
    }

    @Test
    void getSubtasksByEpicID() {
        Task test1 = manager.getByID(6);
        Task test2 = manager.getByID(7);
        ArrayList<Subtask> test3 = (ArrayList<Subtask>) manager.getSubtasksByEpicID(5);
        assertEquals(test1, test3.getFirst());
        assertEquals(test2, test3.get(1));
    }

    @Test
    void getHistory() {
        assertEquals(manager.getByID(2), manager.getHistory().getLast());
        ArrayList<Task> test2 = (ArrayList<Task>) manager.getHistory();
        for (Task task : test2) {
            System.out.println(task);
        }
        System.out.println("---\n---\n---");
        Task test1 = new Task("name", "descr777", TaskStatus.NEW);
        String testdescr1 = test1.description;
        test1.setId(20);
        manager.create(test1);
        ArrayList<Task> arara = (ArrayList<Task>) manager.getAllTasks();
        int testnumber = 0;
        for (Task task : arara) {
            if (task.description.equals(testdescr1))
                testnumber = task.id;
        }
        Task test3 = new Task("ololo", "trololo", TaskStatus.DONE);
        test3.setId(testnumber);
        manager.update(test3);
        assertEquals(manager.getByID(testnumber), manager.getHistory().getLast());
        ArrayList<Task> test4 = (ArrayList<Task>) manager.getHistory();
        for (Task task : test4) {
            System.out.println(task);
        }
        System.out.println("---\n---\n---");
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
                if (task instanceof Subtask) {
                    epicID += ((Subtask) task).epicID;
                    type = String.valueOf(TaskType.SUBTASK);
                } else if (task instanceof Epic) {
                    type = String.valueOf(TaskType.EPIC);
                } else {
                    type = String.valueOf(TaskType.TASK);
                }
                String t1 = String.format("%s,%s,%s,%s,%s,%s", task.id, type, task.name, task.status, task.description, epicID);
                assertEquals(t1, line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Save test IOExсeption", e);
        }

    }

    @Test
    void taskToString() {
        manager.deleteAllTasks();
        Task task1 = new Task("name1", "description1", TaskStatus.NEW);
        manager.create(task1);
        Task task = manager.getByID(8);
        String epicID = "";
        String type = String.valueOf(TaskType.TASK);
        String t1 = String.format("%s,%s,%s,%s,%s,%s", task.id, type, task.name, task.status, task.description, epicID);
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
        Task task1 = new Task("name1", "description1", TaskStatus.NEW);
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