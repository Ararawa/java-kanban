package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager manager;
    static LocalDateTime startTime = LocalDateTime
            .parse("2022/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
    static Duration duration = Duration.ofMinutes(5);

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        startTime = startTime.plus(duration).plus(duration);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task1);
        startTime = startTime.plus(duration).plus(duration);
        Task task2 = new Task("name2", "description2", TaskStatus.NEW, startTime, duration);
        manager.create(task2);
    }

    @AfterEach
    void cleanUp() {
        manager.deleteAllTasks();
    }

    @Test
    void add() {
        System.out.println(manager.getByID(2));
        manager.getByID(1);
        manager.getByID(2);
        manager.getByID(1);
        assertEquals(manager.getByID(2), manager.getHistory().getLast());
        ArrayList<Task> list1 = (ArrayList<Task>) manager.getHistory();
        int duobles = -1;
        for (Task task : list1) {
            if (task.id == 2) {
                duobles++;
            }
        }
        System.out.println("doubles = " + duobles);
        assertEquals(0, duobles);
    }

    @Test
    void remove() {
        System.out.println("---\nremove\n---");
        startTime = startTime.plus(duration).plus(duration);
        Task task3 = new Task("name3", "description3", TaskStatus.NEW, startTime, duration);
        manager.create(task3);
        System.out.println(manager.getByID(3));
        manager.getByID(2);
        manager.getByID(3);
        manager.getByID(2);
        assertEquals(manager.getByID(3), manager.getHistory().getLast());
        ArrayList<Task> list1 = (ArrayList<Task>) manager.getHistory();
        int duobles = -1;
        for (Task task : list1) {
            if (task.id == 3) {
                duobles++;
            }
            System.out.println(task);
        }
        System.out.println("doubles = " + duobles);
        assertEquals(0, duobles);
        System.out.println("---\nremove\n---");
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
        manager.create(test1);
        ArrayList<Task> arara = (ArrayList<Task>) manager.getAllTasks();
        int testnumber = 0;
        for (Task task : arara) {
            if (task.description.equals(testdescr1))
                testnumber = task.id;
        }
        startTime = startTime.plus(duration).plus(duration);
        Task test3 = new Task("ololo", "trololo", TaskStatus.DONE, startTime, duration);
        manager.create(test3);
        assertEquals(manager.getByID(testnumber), manager.getHistory().getLast());
        ArrayList<Task> test4 = (ArrayList<Task>) manager.getHistory();
        for (Task task : test4) {
            System.out.println(task);
        }
        System.out.println("---\n---\n---");
    }
}