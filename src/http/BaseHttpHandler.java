package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
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

    String getByID(int id, HttpExchange httpExchange) throws IOException {
        System.out.println("getByID");
        Task taskReturn = manager.getByID(id);
        if (taskReturn == null) {
            String resp = "No task with this id = " + id;
            System.out.println(resp);
            httpExchange.sendResponseHeaders(404, 0);
            return resp;
        } else {
            System.out.println("start building");
            GsonBuilder gb1 = new GsonBuilder();
            System.out.println("make gson");
            Gson gson1 = gb1
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            System.out.println("make json");
            String jsonOut = gson1.toJson(taskReturn);
            System.out.println("json = " + jsonOut);
            httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(200, 0);
            return jsonOut;
        }
    }

    <T extends Task> String getAll(HttpExchange httpExchange, String taskType) throws IOException {
        System.out.println("getTasks");
        System.out.println("taskType = " + taskType);
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
        System.out.println("tasksReturn = " + tasksReturn.toString());
        System.out.println("Gson gson1 = new GsonBuilder()");
        Gson gson1 = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        System.out.println("String jsonOut = gson1.toJson(tasksReturn);");
        String jsonOut = gson1.toJson(tasksReturn);
        System.out.println("json = " + jsonOut);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, 0);
        return jsonOut;
    }

    String postMethod(HttpExchange httpExchange) throws IOException {
        List<String> contentTypeValues = httpExchange.getRequestHeaders().get("Content-type");
        if ((contentTypeValues != null) && (contentTypeValues.contains("application/json"))) {
            System.out.println("Это JSON!");
        } else {
            response = "Need Json to use post";
            System.out.println(response);
            return response;
        }
        try (InputStream inputStream = httpExchange.getRequestBody()) {
            body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        System.out.println("JsonElement jsonElement = JsonParser.parseString(body);");
        JsonElement jsonElement = JsonParser.parseString(body);
        if (!jsonElement.isJsonObject()) {
            response = "jsonElement is NOT JsonObject()";
            System.out.println(response);
            return response;
        }
        System.out.println("JsonObject jsonObject = jsonElement.getAsJsonObject();");
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        System.out.println("jsonObject = " + jsonObject.toString());
        System.out.println("Optional<Integer> taskId = Optional.of(jsonObject.get(\"id\").getAsInt());");
        Optional<Integer> taskId;
        boolean idPresent;
        if (jsonObject.has("id")) {
            try {
                taskId = Optional.of(jsonObject.get("id").getAsInt());
                idPresent = true;
                System.out.println("Optional<Integer> taskId = " + taskId.get());
            } catch (JsonSyntaxException e) {
                response = "Ошибка при преобразовании поля 'id' в целое число";
                System.out.println(response);
                return response;
            }
        } else {
            response = "В JSON отсутствует поле 'id'";
            System.out.println(response);
            idPresent = false;
        }
        System.out.println("GsonBuilder gb1 = new GsonBuilder();");
        GsonBuilder gb1 = new GsonBuilder();
        System.out.println("make gson");
        Gson gson1 = gb1
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        System.out.println("Task task = gson.fromJson(body, Task.class);");
        Task task = gson1.fromJson(body, Task.class);
        System.out.println("task = " + task);
        if (idPresent) {
            System.out.println("update");
            if (!manager.update(task)) {
                response = "задача пересекается с существующими";
                httpExchange.sendResponseHeaders(406, 0);
            } else {
                response = "Вы использовали метод POST! и update";
                httpExchange.sendResponseHeaders(201, 0);
            }
        } else {
            System.out.println("create");
            if (!manager.create(task)) {
                response = "задача пересекается с существующими";
                httpExchange.sendResponseHeaders(406, 0);
            } else {
                response = "Вы использовали метод POST! и create";
                httpExchange.sendResponseHeaders(201, 0);
            }
        }
        return response;
    }

//он будет содержать общие методы для чтения и отправки данных:
//sendText — для отправки общего ответа в случае успеха;
//sendNotFound — для отправки ответа в случае, если объект не был найден;
//sendHasInteractions — для отправки ответа, если при создании или обновлении задача пересекается с уже существующими.
//сначала надо проверить работу уже написанных, потом добавить эти; написал больше и сложнее.
}
