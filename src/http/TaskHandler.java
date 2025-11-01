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
                response = postMethod(httpExchange);
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
