package manager;

import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    @Override
    public ArrayList<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
    }

    @Override
    public Task getByID(int id) {
        return super.getByID(id);
    }

    @Override
    public void create(Task task) {
        super.create(task);
        save();
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public void deleteByID(int id) {
        super.deleteByID(id);
        save();
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicID(int epicID) {
        return super.getSubtasksByEpicID(epicID);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return super.getHistory();
    }

    public void save() {}
}
