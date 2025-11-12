package http.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import http.adaptors.DurationAdapter;
import http.adaptors.LocalDateTimeAdapter;
import manager.NotFoundException;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
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
                    postMethod(httpExchange);
                    break;
                case "GET":
                    if (pathArray.length == 4) {
                        List<Subtask> epicSubtasks = manager.getSubtasksByEpicID(id.get());
                        if (epicSubtasks.isEmpty()) {
                            response = "No epic with this id = " + id.get();
                            sendNotFound(httpExchange, response);
                        } else {
                            GsonBuilder gb1 = new GsonBuilder();
                            Gson gson1 = gb1
                                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                                    .create();
                            response = gson1.toJson(epicSubtasks);
                            sendText(httpExchange, response);
                        }
                    } else if (id.isPresent()) {
                        getByID(id.get(), httpExchange);
                    } else {
                        getAll(httpExchange, pathArray[1]);
                    }
                    break;
                case "DELETE":
                    response = "Вы использовали метод DELETE!";
                    if (id.isPresent()) {
                        manager.deleteByID(id.get());
                        sendText(httpExchange, response);
                    } else {
                        response = "Cannot DELETE without id";
                        sendNotFound(httpExchange, response);
                    }
                    break;
                default:
                    response = "Вы использовали какой-то другой метод!";
                    sendNotFound(httpExchange, response);
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        } catch (IOException e) {
            response = "Internal Server Error IOException";
            sendInternalServerError(httpExchange, response);
        }
    }
}
