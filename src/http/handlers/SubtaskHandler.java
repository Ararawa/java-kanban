package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager manager) {
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
        } catch (IOException e) {
            httpExchange.sendResponseHeaders(500, 0);
            response = "Internal Server Error IOException";
        }

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

}
