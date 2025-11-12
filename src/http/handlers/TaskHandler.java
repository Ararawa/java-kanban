package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;

import java.io.IOException;
import java.util.Optional;



public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager manager) {
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
                    if (id.isPresent()) {
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
