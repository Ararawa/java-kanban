package manager;

public class Managers {

    static HistoryManager defaultHM = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileManipulator(String filename) {
        return new FileBackedTaskManager(filename);
    }

    public static HistoryManager getDefaultHistory() {
        return defaultHM;
    }
}
