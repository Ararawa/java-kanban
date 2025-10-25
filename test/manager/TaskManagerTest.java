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
import java.util.List;

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
        List<Subtask> test3 = manager.getSubtasksByEpicID(5);
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
        manager.deleteAllTasks();
        startTime = startTime.plus(duration).plus(duration);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task1);
        startTime = startTime.plus(duration).plus(duration);
        Task task2 = new Task("name2", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task2);
        assertEquals(manager.getByID(8), manager.getHistory().getLast());
        assertEquals(manager.getByID(9), manager.getHistory().getLast());
    }

    @Test
    void calculateEpicStart() {
        Duration duration1 = Duration.ofMinutes(400);
        for (Task i : manager.getAllTasks()) {
            manager.getByID(i.id);
        }
        LocalDateTime startTime1 = manager.getByID(4).getStartTime().plus(duration1);
        Subtask subtask3 = new Subtask("name4", "calcEpic", TaskStatus.NEW,
                3, startTime1, duration);
        subtask3.id = 4;
        manager.update(subtask3);
        assertEquals(startTime1, manager.getByID(4).getStartTime());
        assertEquals(startTime1, manager.getByID(3).getStartTime());
        for (Task i : manager.getAllTasks()) {
            manager.getByID(i.id);
        }
    }

    @Test
    void calculateEpicEnd() {
        LocalDateTime startTime1 = manager.getByID(7).getEndTime().plus(duration).plus(duration);
        Subtask subtask4 = new Subtask("name8", "1", TaskStatus.NEW,
                5, startTime1, duration);
        subtask4.id = 6;
        manager.update(subtask4);
        assertEquals(startTime1.plus(duration), manager.getByID(5).getEndTime());
        assertEquals(startTime1.plus(duration), manager.getByID(6).getEndTime());
        for (Task i : manager.getAllTasks()) {
            manager.getByID(i.id);
        }
    }

    @Test
    void calculateEpicDuration() {
        Duration duration1 = Duration.ofMinutes(-800);
        LocalDateTime st1 = manager.getByID(5).getStartTime();
        LocalDateTime end1 = manager.getByID(5).getEndTime();
        LocalDateTime startTime1 = manager.getByID(7).getStartTime().plus(duration1).plus(duration1);
        Subtask subtask3 = new Subtask("name7", "description1", TaskStatus.NEW,
                5, startTime1, duration);
        subtask3.id = 7;
        manager.update(subtask3);
        assertEquals(startTime1, manager.getByID(7).getStartTime());
        assertEquals(startTime1, manager.getByID(5).getStartTime());
        Duration d1 = Duration.between(startTime1, end1).minus(duration).minus(duration);
        assertEquals(d1, manager.getByID(5).duration);
    }

    @Test
    void getPrioritizedTasks() {
        InMemoryTaskManager man;
        man = (InMemoryTaskManager) manager;
        Duration duration1 = Duration.ofMinutes(400);
        LocalDateTime startTime1 = manager.getByID(4).getStartTime().plus(duration1);
        Subtask subtask3 = new Subtask("name4", "calcEpic", TaskStatus.NEW,
                3, startTime1, duration);
        subtask3.id = 4;
        manager.update(subtask3);
        ArrayList<Task> test1 = (ArrayList<Task>) man.getPrioritizedTasks();
        test1.forEach(System.out::println);
        assertTrue(test1.get(1).startTime.isAfter(test1.get(0).getEndTime()));
    }

    @Test
    void setPriority() {
        manager.deleteAllTasks();
        InMemoryTaskManager man;
        man = (InMemoryTaskManager) manager;
        assertTrue(man.prioritizedTasks.isEmpty());
        Duration duration1 = Duration.ofMinutes(400);
        LocalDateTime startTime1 = startTime.plus(duration1);
        Task task = new Task("n", "d", TaskStatus.NEW, startTime1, duration1);
        manager.create(task);
        assertFalse(man.prioritizedTasks.isEmpty());
    }

    @Test
    void scheduleConflict() {
        InMemoryTaskManager man;
        man = (InMemoryTaskManager) manager;
        Duration duration1 = Duration.ofMinutes(4000);
        LocalDateTime startTime1 = startTime.plus(duration1);
        Task task = new Task("n", "d", TaskStatus.DONE, startTime1, duration1);
        assertFalse(man.scheduleConflict(task));
        manager.create(task);
        assertTrue(man.scheduleConflict(task));
    }

}
