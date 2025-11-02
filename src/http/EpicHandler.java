package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
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

            switch (method) {
                case "POST":
                    response = postMethod(httpExchange);
                    break;
                case "GET":
                    if (pathArray.length == 4) {
                        System.out.println("getByID");
                        List<Subtask> epicSubtasks = manager.getSubtasksByEpicID(id.get());
                        if (epicSubtasks == null) {
                            response = "No epic with this id = " + id;
                            System.out.println(response);
                            httpExchange.sendResponseHeaders(404, 0);
                        } else {
                            GsonBuilder gb1 = new GsonBuilder();
                            Gson gson1 = gb1
                                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                                    .create();
                            response = gson1.toJson(epicSubtasks);
                            httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                            httpExchange.sendResponseHeaders(200, 0);
                        }
                    } else if (id.isPresent()) {
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
        } catch (IOException e) {
            httpExchange.sendResponseHeaders(500, 0);
            response = "Internal Server Error IOException";
        }
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

}
