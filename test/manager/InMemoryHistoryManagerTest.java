package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager manager;
    HistoryManager managerH;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        managerH = Managers.getDefaultHistory();
        Task task1 = new Task("name1", "description1", TaskStatus.NEW);
        manager.create(task1);
        Task task2 = new Task("name2", "description1", TaskStatus.NEW);
        manager.create(task2);
    }

    @Test
    void addTaskToHistory() {
        assertEquals(manager.getByID(2), managerH.getHistory().getLast());
    }

    @Test
    void getHistory() {
        assertEquals(manager.getByID(2), managerH.getHistory().getLast());
        ArrayList<Task> test2 = managerH.getHistory();
        for (Task task : test2) {
            System.out.println(task);
        }
        System.out.println("---\n---\n---");
        Task test1 = new Task("ololo", "trololo", TaskStatus.DONE);
        test1.setId(2);
        manager.update(test1);
        assertEquals(manager.getByID(2), managerH.getHistory().getLast());
        ArrayList<Task> test3 = managerH.getHistory();
        for (Task task : test3) {
            System.out.println(task);
        }
    }
}