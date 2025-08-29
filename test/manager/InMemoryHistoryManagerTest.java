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
}