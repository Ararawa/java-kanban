package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
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
        try (FileWriter writer = new FileWriter(file.getName(), StandardCharsets.UTF_8)) {
            String headLine = "id,type,name,status,description,epic";
            ArrayList<Task> allTasks = getAllTasks();
            writer.write(headLine + "\n");
            for (Task task : allTasks) {
                writer.write(taskToString(task) + "\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        } catch (IOException e) {
            try {
                throw new ManagerSaveException("Ошибка при сохранении данных", e);
            } catch (ManagerSaveException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public String taskToString(Task task) {
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

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fbtmNew = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            br.readLine();
            ArrayList<Task> tasksToLoad = new ArrayList<>();
            while (br.ready()) {
                String line = br.readLine();
                tasksToLoad.add(fromString(line));
            }
            for (Task task : tasksToLoad) {
                fbtmNew.create(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fbtmNew;
    }

    public static Task fromString(String loaded) {
        Task task = null;
        TaskStatus status = null;
        String[] split = loaded.split(",");
        if (split[3].equals(String.valueOf(TaskStatus.NEW))) {
            status = TaskStatus.NEW;
        } else if (split[3].equals(String.valueOf(TaskStatus.IN_PROGRESS))) {
            status = TaskStatus.IN_PROGRESS;
        } else if (split[3].equals(String.valueOf(TaskStatus.DONE))) {
            status = TaskStatus.DONE;
        }
        if (split[1].equals(String.valueOf(TaskType.EPIC))) {
            task = new Epic(split[2], split[4], status);
        } else if (split[1].equals(String.valueOf(TaskType.SUBTASK))) {
            task = new Subtask(split[2], split[4], status, Integer.parseInt(split[5]));
        } else if (split[1].equals(String.valueOf(TaskType.TASK))) {
            task = new Task(split[2], split[4], status);
        }
        task.id = Integer.parseInt(split[0]);
        return task;
    }
}
