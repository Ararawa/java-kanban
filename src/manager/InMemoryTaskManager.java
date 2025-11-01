package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    public TreeSet<Task> prioritizedTasks = new TreeSet<>();

    public int number = 0;

    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();

    public HistoryManager historyManager = Managers.getDefaultHistory();

    public int generateNumber() {
        return ++number;
    }

    TaskStatus calculateStatus(Epic epic) {
        TaskStatus was = epic.status;
        if (epic.getEpicSubtasks().isEmpty()) {
            return was;
        }
        int inProgress = 0;
        int done = 0;
        int news = 0;
        for (int i : epic.getEpicSubtasks()) {
            if (subtasks.get(i).status == TaskStatus.IN_PROGRESS) {
                inProgress++;
            } else if (subtasks.get(i).status == TaskStatus.DONE) {
                done++;
            } else if (subtasks.get(i).status == TaskStatus.NEW) {
                news++;
            }
        }
        if (news == epic.getEpicSubtasks().size()) {
            return TaskStatus.NEW;
        } else if (done == epic.getEpicSubtasks().size()) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }

    LocalDateTime calculateEpicStart(Epic epic) {
        if (!epic.getEpicSubtasks().isEmpty()) {
            return epic.getEpicSubtasks()
                    .stream()
                    .map(i -> subtasks.get(i).startTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(subtasks.get(epic.getEpicSubtasks().getFirst()).startTime);
        }
        return null;
    }

    LocalDateTime calculateEpicEnd(Epic epic) {
        if (!epic.getEpicSubtasks().isEmpty()) {
            return epic.getEpicSubtasks()
                    .stream()
                    .map(i -> subtasks.get(i).getEndTime())
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(subtasks.get(epic.getEpicSubtasks().getFirst()).getEndTime());
        }
        return null;
    }

    Duration calculateEpicDuration(Epic epic) {
        if (!epic.getEpicSubtasks().isEmpty()) {
            Duration duration = Duration.ofMinutes(0);
            for (Subtask subtask : getSubtasksByEpicID(epic.id)) {
                duration = duration.plus(subtask.duration);
            }
            return duration;
        }
        return null;
    }

    @Override
    public List<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>(tasks.values());
        for (int i : epics.keySet()) {
            allTasks.add(epics.get(i));
            for (int j : subtasks.keySet()) {
                if (subtasks.get(j).epicID == epics.get(i).getID()) {
                    allTasks.add(subtasks.get(j));
                }
            }
        }
        return allTasks;
    }

    @Override
    public void deleteAllTasks() {
        ArrayList<Integer> allId = new ArrayList<>(tasks.keySet());
        allId.addAll(subtasks.keySet());
        allId.addAll(epics.keySet());
        for (int id : allId) {
            deleteByID(id);
        }
        prioritizedTasks.clear();
    }

    @Override
    public Task getByID(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        }
        return null;
    }

    @Override
    public boolean create(Task task) {
        if (task instanceof Epic) {
            Epic epic = new Epic(task.name, task.description, task.status);
            epic.setId(generateNumber());
            epic.setEpicSubtasks(((Epic) task).getEpicSubtasks());
            epic.status = calculateStatus(epic);
            epic.startTime = calculateEpicStart(epic);
            epic.endTime = calculateEpicEnd(epic);
            epic.duration = calculateEpicDuration(epic);
            epics.put(epic.id, epic);
        } else {
            if (scheduleConflict(task)) {
                System.out.println("Schedule conflict in create!");
                return false;
            }
            if (task instanceof Subtask) {
                Subtask subtask = new Subtask(task.name, task.description, task.status, ((Subtask) task).epicID,
                        task.startTime, task.duration);
                subtask.setId(generateNumber());
                subtasks.put(subtask.id, subtask);
                ArrayList<Integer> intermediate = (ArrayList<Integer>) epics.get(subtask.epicID).getEpicSubtasks();
                intermediate.add(subtask.id);
                epics.get(subtask.epicID).setEpicSubtasks(intermediate);
                epics.get(subtask.epicID).status = calculateStatus(epics.get(subtask.epicID));
                epics.get(subtask.epicID).startTime = calculateEpicStart(epics.get(subtask.epicID));
                epics.get(subtask.epicID).endTime = calculateEpicEnd(epics.get(subtask.epicID));
                epics.get(subtask.epicID).duration = calculateEpicDuration(epics.get(subtask.epicID));
                update(epics.get(subtask.epicID));
            } else if (task != null) {
                Task task1 = new Task(task.name, task.description, task.status, task.startTime, task.duration);
                task1.setId(generateNumber());
                tasks.put(task1.id, task1);
                setPriority(tasks.get(task1.id));
            }
        }
        return true;
    }

    @Override
    public boolean update(Task task) {
        if (scheduleConflict(task)) {
            Task inter = getByID(task.id);
            boolean ok = inter.id == task.id && inter.startTime.equals(task.startTime) &&
                    inter.duration.equals(task.duration);
            if (!ok) {
                System.out.println("Schedule conflict in update!");
                return false;
            }
        }
        if (task != null) {
            prioritizedTasks.remove(getByID(task.id));
        }
        if (task instanceof Epic) {
            epics.get(task.id).description = task.description;
            epics.get(task.id).name = task.name;
            epics.get(task.id).setEpicSubtasks(((Epic) task).getEpicSubtasks());
            epics.get(task.id).status = calculateStatus(epics.get(task.id));
            epics.get(task.id).startTime = calculateEpicStart(epics.get(task.id));
            epics.get(task.id).endTime = calculateEpicEnd(epics.get(task.id));
            epics.get(task.id).duration = calculateEpicDuration(epics.get(task.id));
            setPriority(epics.get(task.id));
            getSubtasksByEpicID(task.id).forEach(this::setPriority);
        } else if (task instanceof Subtask) {
            subtasks.get(task.id).description = task.description;
            subtasks.get(task.id).status = task.status;
            subtasks.get(task.id).name = task.name;
            subtasks.get(task.id).startTime = task.startTime;
            subtasks.get(task.id).duration = task.duration;
            subtasks.get(task.id).epicID = ((Subtask) task).epicID;
            update(epics.get(subtasks.get(task.id).epicID));
        } else if (task != null) {
            tasks.get(task.id).description = task.description;
            tasks.get(task.id).status = task.status;
            tasks.get(task.id).name = task.name;
            tasks.get(task.id).startTime = task.startTime;
            tasks.get(task.id).duration = task.duration;
            setPriority(tasks.get(task.id));
        }
        return true;
    }

    @Override
    public void deleteByID(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subtasks.containsKey(id)) {
            int epicID = subtasks.get(id).epicID;
            ArrayList<Integer> intermediate = (ArrayList<Integer>) epics.get(epicID).getEpicSubtasks();
            intermediate.remove((Integer) id);
            epics.get(epicID).setEpicSubtasks(intermediate);
            epics.get(epicID).status = calculateStatus(epics.get(epicID));
            epics.get(epicID).startTime = calculateEpicStart(epics.get(epicID));
            epics.get(epicID).endTime = calculateEpicEnd(epics.get(epicID));
            epics.get(epicID).duration = calculateEpicDuration(epics.get(epicID));
            subtasks.remove(id);
        } else {
            for (int i = 0; i < epics.get(id).getEpicSubtasks().size(); i++) {
                subtasks.remove(epics.get(id).getEpicSubtasks().get(i));
            }
            epics.get(id).setEpicSubtasks(new ArrayList<>());
            epics.remove(id);
        }
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksByEpicID(int epicID) {
        return (subtasks.values()
                .stream()
                .filter(subtask -> (subtask.epicID == epicID))
                .toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        if (prioritizedTasks.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(prioritizedTasks);

    }

    public void setPriority(Task task) {
//        if (task.startTime == null) {
//            return;
//        }
        prioritizedTasks.add(task);
    }

    public boolean scheduleConflict(Task task) {
        boolean conflict;
        if (task instanceof Subtask) {
            conflict = prioritizedTasks
                    .stream()
                    .filter(task1 -> !(task1 instanceof Epic))
                    .anyMatch(task1 -> {
                        boolean b = (task.getEndTime().isBefore(task1.startTime)) ||
                                (task.startTime.isAfter(task1.getEndTime()));
                        return !b;
                    });
        } else {
            conflict = prioritizedTasks
                    .stream()
                    .anyMatch(task1 -> {
                        boolean b = (task.getEndTime().isBefore(task1.startTime)) ||
                                (task.startTime.isAfter(task1.getEndTime()));
                        return !b;
                    });
        }
        return conflict;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }
}
