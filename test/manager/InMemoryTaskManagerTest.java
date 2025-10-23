package manager;

import org.junit.jupiter.api.BeforeEach;

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

}