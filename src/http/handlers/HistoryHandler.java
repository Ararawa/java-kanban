package http.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import http.adaptors.DurationAdapter;
import http.adaptors.LocalDateTimeAdapter;
import manager.NotFoundException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String response = "";

            List<Task> result = manager.getHistory();
            Gson gson1 = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            response = gson1.toJson(result);
            sendText(httpExchange, response);
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }
}
