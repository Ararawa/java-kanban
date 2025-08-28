package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
class InMemoryTaskManagerTest {

    TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
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

    @Test
    void calculateStatus() {
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(5).status);
        Subtask test1 = new Subtask("name6", "description1", TaskStatus.DONE, 5);
        test1.setId(6);
        manager.update(test1);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(5).status);
        Subtask test2 = new Subtask("name7", "description1", TaskStatus.IN_PROGRESS, 5);
        test2.setId(7);
        manager.update(test2);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(5).status);
        Subtask test3 = new Subtask("name7", "description1", TaskStatus.DONE, 5);
        test3.setId(7);
        manager.update(test3);
        assertEquals(TaskStatus.DONE, manager.getByID(5).status);
        Subtask test4 = new Subtask("name6", "description1", TaskStatus.NEW, 5);
        test4.setId(6);
        manager.update(test4);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(5).status);
    }

    @Test
    void returnAllTasks() {
        ArrayList<Task> test1 = manager.returnAllTasks();
        for (Task task : test1) {
            assertEquals(manager.getByID(task.id), task);
        }
    }

    @Test
    void deleteAllTasks() {
        manager.deleteAllTasks();
        for (int i = 0; i < 10; i++) {
            assertNull(manager.getByID(i));
        }
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
        ArrayList<Subtask> test3 = manager.getSubtasksByEpicID(5);
        assertEquals(test1, test3.getFirst());
        assertEquals(test2, test3.get(1));
    }

    @Test
    void epicInsideEpic() {
        Task test1 = new Epic("yammy", "description1", TaskStatus.NEW);
        String testname1 = test1.name;
        manager.create(test1);
        ArrayList<Task> test2 = manager.returnAllTasks();
        for (Task task : test2) {
            if (task.name.equals(testname1)) {
                assertFalse(task instanceof Subtask);
                assertInstanceOf(Epic.class, task);
            }
        }
        Task test3 =  new Epic("wrong", "description1", TaskStatus.NEW);
        String testname2 = test3.name;
        test3.setId(6);
        manager.create(test3);
        ArrayList<Task> test4 = manager.returnAllTasks();
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
}