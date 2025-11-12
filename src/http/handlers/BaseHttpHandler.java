package http.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.adaptors.DurationAdapter;
import http.adaptors.LocalDateTimeAdapter;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BaseHttpHandler implements HttpHandler {

    TaskManager manager;
    String taskType;
    String body;
    String response;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }

    void getByID(int id, HttpExchange httpExchange) throws IOException {
        Task taskReturn = manager.getByID(id);
        if (taskReturn == null) {
            response = "No task with this id = " + id;
            sendNotFound(httpExchange, response);
        } else {
            GsonBuilder gb1 = new GsonBuilder();
            Gson gson1 = gb1
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            response = gson1.toJson(taskReturn);
            sendText(httpExchange, response);
        }
    }

    <T extends Task> void getAll(HttpExchange httpExchange, String taskType) throws IOException {
        List<T> tasksReturn = List.of();
        switch (taskType) {
            case "tasks":
                tasksReturn = (List<T>) manager.getTasks();
                break;
            case "subtasks":
                tasksReturn = (List<T>) manager.getSubtasks();
                break;
            case "epics":
                tasksReturn = (List<T>) manager.getEpics();
                break;
        }
        Gson gson1 = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        response = gson1.toJson(tasksReturn);
        sendText(httpExchange, response);
    }

    void postMethod(HttpExchange httpExchange) throws IOException {
        List<String> contentTypeValues = httpExchange.getRequestHeaders().get("Content-type");
        if ((contentTypeValues == null) || (!contentTypeValues.contains("application/json"))) {
            response = "Need Json to use post";
            sendNotFound(httpExchange, response);
            return;
        }
        try (InputStream inputStream = httpExchange.getRequestBody()) {
            body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        JsonElement jsonElement = JsonParser.parseString(body);
        if (!jsonElement.isJsonObject()) {
            response = "jsonElement is NOT JsonObject()";
            sendNotFound(httpExchange, response);
            return;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Optional<Integer> taskId = Optional.empty();
        boolean idPresent = false;
        if (jsonObject.has("id")) {
            try {
                taskId = Optional.of(jsonObject.get("id").getAsInt());
                idPresent = true;
            } catch (JsonSyntaxException e) {
                response = "Ошибка при преобразовании поля 'id' в целое число";
                sendInternalServerError(httpExchange, response);
                return;
            }
        } else {
            response = "В JSON отсутствует поле 'id'";
            sendNotFound(httpExchange, response);
            return;
        }
        GsonBuilder gb1 = new GsonBuilder();
        Gson gson1 = gb1
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        Task task = null;
        if (jsonObject.has("epicID")) {
            task = gson1.fromJson(body, Subtask.class);
        } else if (jsonObject.has("epicSubtasks")) {
            task = gson1.fromJson(body, Epic.class);
        } else {
            task = gson1.fromJson(body, Task.class);
        }
        if (idPresent && taskId.get() != 0) {
            if (!manager.update(task)) {
                response = "задача пересекается с существующими";
                sendHasInteractions(httpExchange, response);
            } else {
                response = "Вы использовали метод POST! и update";
                sendText201(httpExchange, response);
            }
        } else {
            if (!manager.create(task)) {
                response = "задача пересекается с существующими";
                sendHasInteractions(httpExchange, response);
            } else {
                response = "Вы использовали метод POST! и create";
                sendText201(httpExchange, response);
            }
        }
    }

    void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    void sendText201(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        //httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(201, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    void sendNotFound(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(404, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    void sendHasInteractions(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(406, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    void sendInternalServerError(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(500, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }
}
