package http.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import http.adaptors.DurationAdapter;
import http.adaptors.LocalDateTimeAdapter;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class PriorityHandler extends BaseHttpHandler {

    public PriorityHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String response = "";

        List<Task> result = manager.getPrioritizedTasks();
        Gson gson1 = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        response = gson1.toJson(result);
        sendText(httpExchange, response);
    }
}
