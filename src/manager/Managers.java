package manager;

public class Managers {

    static HistoryManager defaultHM = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileManipulator() {
        return new FileBackedTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return defaultHM;
    }
}
