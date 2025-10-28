package tasks;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    TaskManager manager;

    @Test
    void twoTasksWithSameID() {
        manager = Managers.getDefault();
        Task test1 = new Subtask("name1", "description1", TaskStatus.NEW, 7);
        test1.setId(23);
        Task test2 = new Subtask("name1", "description1", TaskStatus.NEW, 7);
        test2.setId(23);
        assertEquals(test1, test2);
    }
}