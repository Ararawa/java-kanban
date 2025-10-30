package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public List<Task> getAllTasks() {
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
    public List<Subtask> getSubtasksByEpicID(int epicID) {
        return super.getSubtasksByEpicID(epicID);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file.getName(), StandardCharsets.UTF_8)) {
            String headLine = "id,type,name,status,description,startTime,duration,epic";
            ArrayList<Task> allTasks = (ArrayList<Task>) getAllTasks();
            writer.write(headLine + "\n");
            for (Task task : allTasks) {
                writer.write(taskToString(task) + "\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных", e);
        }
    }

    public String taskToString(Task task) {
        String startTime = "";
        if (task.startTime != null) {
            startTime += task.startTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        }
        String duration = "";
        if (task.duration != null) {
            duration += String.valueOf(task.duration.toMinutes());
        }
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
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                task.id, type, task.name, task.status, task.description, startTime, duration, epicID);
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
            throw new ManagerSaveException("Save test IOExсeption", e);
        }
        return fbtmNew;
    }

    public static Task fromString(String loaded) {
        Task task = null;
        TaskStatus status = null;
        LocalDateTime startTime = null;
        Duration duration = null;
        String[] split = loaded.split(",");

        if (split[3].equals(String.valueOf(TaskStatus.NEW))) {
            status = TaskStatus.NEW;
        } else if (split[3].equals(String.valueOf(TaskStatus.IN_PROGRESS))) {
            status = TaskStatus.IN_PROGRESS;
        } else if (split[3].equals(String.valueOf(TaskStatus.DONE))) {
            status = TaskStatus.DONE;
        }
        if (split[5] != null) {
            startTime = LocalDateTime.parse(split[5], DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        }
        if (split[6] != null) {
            duration = Duration.ofMinutes(Long.parseLong(split[6]));
        }
        if (split[1].equals(String.valueOf(TaskType.EPIC))) {
            task = new Epic(split[2], split[4], status);
        } else if (split[1].equals(String.valueOf(TaskType.SUBTASK))) {
            task = new Subtask(split[2], split[4], status, Integer.parseInt(split[7]), startTime, duration);
        } else if (split[1].equals(String.valueOf(TaskType.TASK))) {
            task = new Task(split[2], split[4], status, startTime, duration);
        }
        task.id = Integer.parseInt(split[0]);
        return task;
    }

    public ArrayList<Task> getTasks() {
        return super.getTasks();
    }

    public ArrayList<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    public ArrayList<Epic> getEpics() {
        return super.getEpics();
    }
}
