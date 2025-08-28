package tasks;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    TaskManager manager;
    HistoryManager managerH;

    @Test
    void twoTasksWithSameID() {
        manager = Managers.getDefault();
        managerH = Managers.getDefaultHistory();
        Task test1 = new Task("name1", "description1", TaskStatus.NEW);
        test1.setId(19);
        Task test2 = new Task("name1", "description1", TaskStatus.NEW);
        test2.setId(19);
        assertEquals(test1, test2);
    }
}