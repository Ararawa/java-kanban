package manager;

public class Managers {

    static HistoryManager defaultHM = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return defaultHM;
    }
}
