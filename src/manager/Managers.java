package manager;

import java.io.File;

public class Managers {

    static HistoryManager defaultHM = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileManipulator(File file) {
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return defaultHM;
    }
}
