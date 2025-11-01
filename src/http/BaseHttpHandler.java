package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class BaseHttpHandler implements HttpHandler {

    TaskManager manager;
    String taskType;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }

    String getByID(int id, HttpExchange httpExchange) throws IOException {
        System.out.println("getByID");
        Task taskReturn = manager.getByID(id);
        if (taskReturn == null) {
            String resp = "No task with this id = " + id;
            System.out.println(resp);
            httpExchange.sendResponseHeaders(404, 0);
            return resp;
        } else {
            GsonBuilder gb1 = new GsonBuilder();
            Gson gson1 = gb1
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
            String jsonOut = gson1.toJson(taskReturn);
            httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(200, 0);
            return jsonOut;
        }
    }

    <T extends Task> String getAll(HttpExchange httpExchange, String taskType) throws IOException {
        System.out.println("getTasks");
        List<T> tasksReturn = List.of();
        tasksReturn = switch (taskType) {
            case "tasks" -> (List<T>) manager.getTasks();
            case "subtasks" -> (List<T>) manager.getSubtasks();
            case "epics" -> (List<T>) manager.getEpics();
            default -> tasksReturn;
        };
        Gson gson1 = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        String jsonOut = gson1.toJson(tasksReturn);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, 0);
        return jsonOut;
    }

//    он будет содержать общие методы для чтения и отправки данных:
//    sendText — для отправки общего ответа в случае успеха;
//    sendNotFound — для отправки ответа в случае, если объект не был найден;
//    sendHasInteractions — для отправки ответа, если при создании или обновлении задача пересекается с уже существующими.
}
