package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;



public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " запроса от клиента.");

        String path = httpExchange.getRequestURI().getPath();
        String[] pathArray = path.split("/");
        Optional<Integer> id;
        if (pathArray.length > 2) {
            id = Optional.of(Integer.parseInt(pathArray[2]));
        } else {
            id = Optional.empty();
        }

        String response = "";
        switch(method) {
            case "POST":
                List<String> contentTypeValues = httpExchange.getRequestHeaders().get("Content-type");
                if ((contentTypeValues != null) && (contentTypeValues.contains("application/json"))) {
                    System.out.println("Это JSON!");
                } else {
                    System.out.println("Need Json to use post");
                    break;
                }
                String body;
                try (InputStream inputStream = httpExchange.getRequestBody()) {
                    body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                }

                JsonElement jsonElement = JsonParser.parseString(body);
                if (!jsonElement.isJsonObject()) {
                    System.out.println("!jsonElement.isJsonObject()");
                    break;
                }
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Optional<Integer> taskId = Optional.of(jsonObject.get("id").getAsInt());

                Gson gson = new Gson();
                Task task = gson.fromJson(body, Task.class);
                if (taskId.isPresent()) {
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
                break;
            case "GET":
                if (id.isPresent()) {
                    response = getByID(id.get(), httpExchange);
                } else {
                    response = getAll(httpExchange, pathArray[1]);
                }
                break;
            case "DELETE":
                response = "Вы использовали метод DELETE!";
                if (id.isPresent()) {
                    System.out.println("deleteByID");
                    manager.deleteByID(id.get());
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    response = "Cannot DELETE without id";
                    System.out.println(response);
                }
                break;
            default:
                response = "Вы использовали какой-то другой метод!";
        }

//        httpExchange.sendResponseHeaders(200, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

}
