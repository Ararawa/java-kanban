package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

abstract class TaskManagerTest<T extends TaskManager> {

    TaskManager manager;
    static LocalDateTime startTime = LocalDateTime
            .parse("2022/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
    static Duration duration = Duration.ofMinutes(5);

    abstract T createManager();

    @BeforeEach
    void setUp() {
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task1);
        startTime = startTime.plus(duration).plus(duration);
        Task task2 = new Task("name2", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task2);
        startTime = startTime.plus(duration).plus(duration);
        Epic epic1 = new Epic("name3", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(epic1);
        startTime = startTime.plus(duration).plus(duration);
        Subtask subtask1 = new Subtask("name4", "description1", TaskStatus.DONE, 3,
                startTime, duration);
        manager.create(subtask1);
        startTime = startTime.plus(duration).plus(duration);
        Epic epic2 = new Epic("name5", "description1", TaskStatus.NEW, startTime, duration);
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

    @AfterEach
    void cleanUp() {
        manager.deleteAllTasks();
    }

    @Test
    void calculateStatus() {
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(5).status);
        startTime = startTime.plus(duration).plus(duration);
        Subtask test1 = new Subtask("name6", "description1", TaskStatus.DONE, 5,
                startTime, duration);
        test1.setId(6);
        manager.update(test1);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(5).status);
        startTime = startTime.plus(duration).plus(duration);
        Subtask test2 = new Subtask("name7", "description1", TaskStatus.IN_PROGRESS, 5,
                startTime, duration);
        test2.setId(7);
        manager.update(test2);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(5).status);
        startTime = startTime.plus(duration).plus(duration);
        Subtask test3 = new Subtask("name7", "description1", TaskStatus.DONE, 5,
                startTime, duration);
        test3.setId(7);
        manager.update(test3);
        assertEquals(TaskStatus.DONE, manager.getByID(5).status);
        startTime = startTime.plus(duration).plus(duration);
        Subtask test4 = new Subtask("name6", "description1", TaskStatus.NEW, 5,
                startTime, duration);
        test4.setId(6);
        manager.update(test4);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(5).status);
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
        startTime = startTime.plus(duration).plus(duration);
        Task test1 = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
        test1.id = 1;
        Task test2 = manager.getByID(1);
        assertEquals(test1, test2);
    }

    @Test
    void create() {
        startTime = startTime.plus(duration).plus(duration);
        Task test1 = new Task("test1", "test1desc", TaskStatus.DONE, startTime, duration);
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
        startTime = startTime.plus(duration).plus(duration);
        Subtask subtask4 = new Subtask("n7", "destion1", TaskStatus.DONE,
                5, startTime, duration);
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
    void epicInsideEpic() {
        startTime = startTime.plus(duration).plus(duration);
        Task test1 = new Epic("yammy", "description1", TaskStatus.NEW, startTime, duration);
        String testname1 = test1.name;
        manager.create(test1);
        ArrayList<Task> test2 = (ArrayList<Task>) manager.getAllTasks();
        for (Task task : test2) {
            if (task.name.equals(testname1)) {
                assertFalse(task instanceof Subtask);
                assertInstanceOf(Epic.class, task);
            }
        }
        startTime = startTime.plus(duration).plus(duration);
        Task test3 =  new Epic("wrong", "description1", TaskStatus.NEW, startTime, duration);
        String testname2 = test3.name;
        test3.setId(6);
        manager.create(test3);
        ArrayList<Task> test4 = (ArrayList<Task>) manager.getAllTasks();
        for (Task task : test4) {
            if (task.name.equals(testname2)) {
                assertFalse(task instanceof Subtask);
                assertInstanceOf(Epic.class, task);
            }
        }
    }

    @Test
    void subtaskToEpic() {
        Task test1 = manager.getByID(4);
        test1.setId(3);
        assertInstanceOf(Subtask.class, test1);
        assertFalse(test1 instanceof Epic);
        System.out.println(manager.getByID(3));
    }

    @Test
    void getHistory() {
        assertEquals(manager.getByID(2), manager.getHistory().getLast());
        ArrayList<Task> test2 = (ArrayList<Task>) manager.getHistory();
        for (Task task : test2) {
            System.out.println(task);
        }
        System.out.println("---\n---\n---");
        startTime = startTime.plus(duration).plus(duration);
        Task test1 = new Task("name", "descr777", TaskStatus.NEW, startTime, duration);
        String testdescr1 = test1.description;
        test1.setId(20);
        manager.create(test1);
        ArrayList<Task> arara = (ArrayList<Task>) manager.getAllTasks();
        int testnumber = 0;
        for (Task task : arara) {
            if (task.description.equals(testdescr1))
                testnumber = task.id;
        }
        startTime = startTime.plus(duration).plus(duration);
        Task test3 = new Task("ololo", "trololo", TaskStatus.DONE, startTime, duration);
        test3.setId(testnumber);
        manager.update(test3);
        assertEquals(manager.getByID(testnumber), manager.getHistory().getLast());
        ArrayList<Task> test4 = (ArrayList<Task>) manager.getHistory();
        for (Task task : test4) {
            System.out.println(task);
        }
        System.out.println("---\n---\n---");
    }

}
