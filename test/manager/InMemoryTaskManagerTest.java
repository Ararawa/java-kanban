package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @Override
    TaskManager createManager() {
        return Managers.getDefault();
    }

    @BeforeEach @Override
    void setUp() {
       manager = createManager();
       super.setUp();
    }

    @Test
    void statusEpicAllVariations() {
        InMemoryTaskManager man = (InMemoryTaskManager) manager;

        startTime = startTime.plus(duration).plus(duration);
        Epic epic = new Epic("n", "d", TaskStatus.NEW, startTime, duration);
        manager.create(epic);
        startTime = startTime.plus(duration).plus(duration);
        Subtask subtask1 = new Subtask("n1", "d1", TaskStatus.NEW, 8, startTime, duration);
        subtask1.setId(9);
        manager.create(subtask1);
        startTime = startTime.plus(duration).plus(duration);
        Subtask subtask2 = new Subtask("n2", "d2", TaskStatus.NEW, 8, startTime, duration);
        subtask2.setId(10);
        manager.create(subtask2);
        assertEquals(TaskStatus.NEW, manager.getByID(8).status);

        subtask1.status = TaskStatus.DONE;
        subtask2.status = TaskStatus.DONE;
        manager.update(subtask1);
        manager.update(subtask2);
        assertEquals(TaskStatus.DONE, manager.getByID(8).status);

        subtask1.status = TaskStatus.NEW;
        subtask2.status = TaskStatus.DONE;
        manager.update(subtask1);
        manager.update(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(8).status);

        subtask1.status = TaskStatus.DONE;
        subtask2.status = TaskStatus.NEW;
        manager.update(subtask1);
        manager.update(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(8).status);

        subtask1.status = TaskStatus.NEW;
        subtask2.status = TaskStatus.IN_PROGRESS;
        manager.update(subtask1);
        manager.update(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(8).status);

        subtask1.status = TaskStatus.DONE;
        subtask2.status = TaskStatus.IN_PROGRESS;
        manager.update(subtask1);
        manager.update(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(8).status);

        subtask1.status = TaskStatus.IN_PROGRESS;
        subtask2.status = TaskStatus.NEW;
        manager.update(subtask1);
        manager.update(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(8).status);

        subtask1.status = TaskStatus.IN_PROGRESS;
        subtask2.status = TaskStatus.DONE;
        manager.update(subtask1);
        manager.update(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(8).status);

        subtask1.status = TaskStatus.IN_PROGRESS;
        subtask2.status = TaskStatus.IN_PROGRESS;
        manager.update(subtask1);
        manager.update(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getByID(8).status);


    }
}