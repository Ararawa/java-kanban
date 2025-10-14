package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    String filename;

    public FileBackedTaskManager(String filename) {
        this.filename = filename;
    }

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

    public void save() {
        try (FileWriter writer = new FileWriter(filename, StandardCharsets.UTF_8, true)) {
            String headLine = "id,type,name,status,description,epic";
            ArrayList<Task> allTasks = getAllTasks();
            writer.write(headLine + "\n");
            for (Task task : allTasks) {
                writer.write(toString(task) + "\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    String toString(Task task) {
        String type;
        String epicID = "";
        if (task instanceof Subtask) {
            epicID += ((Subtask) task).epicID;
            type = String.valueOf(TaskType.SUBTASK);
        } else if (task instanceof Epic) {
            type = String.valueOf(TaskType.EPIC);
        } else {
            type = String.valueOf(TaskType.TASK);
        }
        return String.format("%s,%s,%s,%s,%s,%s",
                task.id, type, task.name, task.status, task.description, epicID);
    }
}
