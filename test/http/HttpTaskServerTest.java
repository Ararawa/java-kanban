package http;

import com.google.gson.*;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.*;
import tasks.*;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    static HttpTaskServer httpTaskServer;
    static TaskManager manager = Managers.getDefault();
    static String managerType = "memory";
    static String serverUrl = "http://localhost:8080/tasks";

    @BeforeAll
    static void start() throws IOException {
        httpTaskServer = new HttpTaskServer(manager);
        //httpTaskServer.setManagerTest(manager);
        httpTaskServer.startServer();
        httpTaskServer.handlers();
    }

    @AfterAll
    static void end() {
        httpTaskServer.stopServer();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(serverUrl).openConnection();
            connection.setRequestMethod("GET");
            assertNotEquals(200, connection.getResponseCode());
        } catch (IOException e) {
            System.out.println("Server has been shut down or not accessible.");
        }
    }

    @BeforeEach
    void setUp() {
        manager.deleteAllTasks();
        LocalDateTime startTime = LocalDateTime
                .parse("2022/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        Duration duration = Duration.ofMinutes(5);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task1);
        startTime = startTime.plus(duration).plus(duration);
        Task task2 = new Task("name2", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task2);
        startTime = startTime.plus(duration).plus(duration);
        Epic epic1 = new Epic("name3", "description1", TaskStatus.NEW);
        manager.create(epic1);
        startTime = startTime.plus(duration).plus(duration);
        List<Epic> inter = manager.getEpics();
        int epicIDTest = inter.getFirst().id;
        Subtask subtask1 = new Subtask("name4", "description1", TaskStatus.DONE, epicIDTest,
                startTime, duration);
        manager.create(subtask1);
        startTime = startTime.plus(duration).plus(duration);
        Epic epic2 = new Epic("name5", "description1", TaskStatus.NEW);
        manager.create(epic2);
        inter = manager.getEpics();
        epicIDTest = inter.getLast().id;
        startTime = startTime.plus(duration).plus(duration);
        Subtask subtask2 = new Subtask("name6", "description1", TaskStatus.IN_PROGRESS,
                epicIDTest, startTime, duration);
        manager.create(subtask2);
        startTime = startTime.plus(duration).plus(duration);
        Subtask subtask3 = new Subtask("name7", "description1", TaskStatus.NEW,
                epicIDTest, startTime, duration);
        manager.create(subtask3);
    }

    @Test
    void startServer() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(serverUrl).openConnection();
            connection.setRequestMethod("GET");
            assertEquals(200, connection.getResponseCode());
        } catch (IOException e) {
            System.out.println("Server is not running or not accessible.");
        }
    }

    @Test
    void getTasks() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            if ((contentType == null) || (!contentType.equals("application/json"))) {
                String error1 = "Need Json to use post";
                System.out.println(error1);
            }
            String body = response.body();
            GsonBuilder gb1 = new GsonBuilder();
            Gson gson1 = gb1
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            List<Task> tasksFromServer = gson1.fromJson(body, new ListTypeTokenTask().getType());
            List<Task> tasksFromManager = manager.getTasks();
            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(2, tasksFromServer.size(), "Некорректное количество задач");
            assertEquals("name1", tasksFromServer.getFirst().name, "Некорректное имя задачи");
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void getSubtasks() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            if ((contentType == null) || (!contentType.equals("application/json"))) {
                String error1 = "Need Json to use post";
                System.out.println(error1);
            }
            String body = response.body();
            GsonBuilder gb1 = new GsonBuilder();
            Gson gson1 = gb1
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            List<Subtask> tasksFromServer = gson1.fromJson(body, new ListTypeTokenSubTask().getType());
            List<Subtask> tasksFromManager = manager.getSubtasks();
            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(tasksFromManager.size(), tasksFromServer.size(), "Некорректное количество задач");
            assertEquals(tasksFromManager.getFirst().name, tasksFromServer.getFirst().name, "Некорректное имя задачи");
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void getEpics() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            if ((contentType == null) || (!contentType.equals("application/json"))) {
                String error1 = "Need Json to use post";
                System.out.println(error1);
            }
            String body = response.body();
            GsonBuilder gb1 = new GsonBuilder();
            Gson gson1 = gb1
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            List<Epic> tasksFromServer = gson1.fromJson(body, new ListTypeTokenEpic().getType());
            List<Epic> tasksFromManager = manager.getEpics();
            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(tasksFromManager.size(), tasksFromServer.size(), "Некорректное количество задач");
            assertEquals(tasksFromManager.getFirst().name, tasksFromServer.getFirst().name, "Некорректное имя задачи");
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void getTaskByID() {
        try {
            int id = manager.getTasks().getFirst().id;
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/" + id);
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            if ((contentType != null) && (contentType.equals("application/json"))) {
                System.out.println("Это JSON!");
            } else {
                String error1 = "Need Json to use post";
                System.out.println(error1);
            }
            String body = response.body();

            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                String error1 = "jsonElement is NOT JsonObject()";
                System.out.println(error1);
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Optional<Integer> taskId;
            boolean idPresent;
            if (jsonObject.has("id")) {
                try {
                    taskId = Optional.of(jsonObject.get("id").getAsInt());
                    idPresent = true;
                    System.out.println("Optional<Integer> taskId = " + taskId.get());
                } catch (JsonSyntaxException e) {
                    String error1 = "Ошибка при преобразовании поля 'id' в целое число";
                    System.out.println(error1);
                }
            } else {
                String error1 = "В JSON отсутствует поле 'id'";
                System.out.println(error1);
                idPresent = false;
            }
            GsonBuilder gb1 = new GsonBuilder();
            Gson gson1 = gb1
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            Task task = gson1.fromJson(body, Task.class);
            assertEquals(manager.getTasks().getFirst(), task);
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void getSubtaskByID() {
        try {
            int id = manager.getSubtasks().getFirst().id;
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/subtasks/" + id);
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            if ((contentType != null) && (contentType.equals("application/json"))) {
                System.out.println("Это JSON!");
            } else {
                String error1 = "Need Json to use post";
                System.out.println(error1);
            }
            String body = response.body();

            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                String error1 = "jsonElement is NOT JsonObject()";
                System.out.println(error1);
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Optional<Integer> taskId;
            boolean idPresent;
            if (jsonObject.has("id")) {
                try {
                    taskId = Optional.of(jsonObject.get("id").getAsInt());
                    idPresent = true;
                    System.out.println("Optional<Integer> taskId = " + taskId.get());
                } catch (JsonSyntaxException e) {
                    String error1 = "Ошибка при преобразовании поля 'id' в целое число";
                    System.out.println(error1);
                }
            } else {
                String error1 = "В JSON отсутствует поле 'id'";
                System.out.println(error1);
                idPresent = false;
            }
            GsonBuilder gb1 = new GsonBuilder();
            Gson gson1 = gb1
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            Subtask task = gson1.fromJson(body, Subtask.class);
            assertEquals(manager.getSubtasks().getFirst(), task);
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void getEpicByID() {
        try {
            int id = manager.getEpics().getFirst().id;
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/epics/" + id);
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            if ((contentType != null) && (contentType.equals("application/json"))) {
                System.out.println("Это JSON!");
            } else {
                String error1 = "Need Json to use post";
                System.out.println(error1);
            }
            String body = response.body();

            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                String error1 = "jsonElement is NOT JsonObject()";
                System.out.println(error1);
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Optional<Integer> taskId;
            boolean idPresent;
            if (jsonObject.has("id")) {
                try {
                    taskId = Optional.of(jsonObject.get("id").getAsInt());
                    idPresent = true;
                    System.out.println("Optional<Integer> taskId = " + taskId.get());
                } catch (JsonSyntaxException e) {
                    String error1 = "Ошибка при преобразовании поля 'id' в целое число";
                    System.out.println(error1);
                }
            } else {
                String error1 = "В JSON отсутствует поле 'id'";
                System.out.println(error1);
                idPresent = false;
            }
            GsonBuilder gb1 = new GsonBuilder();
            Gson gson1 = gb1
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            Epic task = gson1.fromJson(body, Epic.class);
            assertEquals(manager.getEpics().getFirst(), task);
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void deleteTask() {
        try {
            int id = manager.getTasks().getFirst().id;
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/" + id);
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<Task> tasksFromManager = manager.getAllTasks();
            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(6, tasksFromManager.size(), "Некорректное количество задач");
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void deleteSubtask() {
        try {
            int id = manager.getSubtasks().getFirst().id;
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/subtasks/" + id);
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<Task> tasksFromManager = manager.getAllTasks();
            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(6, tasksFromManager.size(), "Некорректное количество задач");
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void deleteEpic() {
        try {
            int id = manager.getEpics().getFirst().id;
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/epics/" + id);
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<Task> tasksFromManager = manager.getAllTasks();
            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(5, tasksFromManager.size(), "Некорректное количество задач");
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void getEpicSubtasks() {
        try {
            int id = manager.getEpics().getLast().id;
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/epics/" + id + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            if ((contentType == null) || (!contentType.equals("application/json"))) {
                String error1 = "Need Json to use post";
                System.out.println(error1);
            }
            String body = response.body();
            GsonBuilder gb1 = new GsonBuilder();
            Gson gson1 = gb1
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            List<Subtask> tasksFromServer = gson1.fromJson(body, new ListTypeTokenSubTask().getType());
            List<Subtask> tasksFromManager = manager.getSubtasksByEpicID(id);
            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(2, tasksFromServer.size(), "Некорректное количество задач");
            assertEquals(tasksFromManager, tasksFromServer);
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void history() {
        try {
            manager.deleteAllTasks();
            LocalDateTime startTime = LocalDateTime
                    .parse("2022/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
            Duration duration = Duration.ofMinutes(5);
            Task task1 = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
            manager.create(task1);
            startTime = startTime.plus(duration).plus(duration);
            Task task2 = new Task("name2", "description1", TaskStatus.NEW, startTime, duration);
            manager.create(task2);
            int id1 = manager.getAllTasks().getFirst().id;
            int id2 = manager.getAllTasks().getLast().id;
            manager.getByID(id1);
            manager.getByID(id2);
            manager.getByID(id1);
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/history");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            if ((contentType == null) || (!contentType.equals("application/json"))) {
                String error1 = "Need Json to use post";
                System.out.println(error1);
            }
            String body = response.body();
            GsonBuilder gb1 = new GsonBuilder();
            Gson gson1 = gb1
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            List<Task> tasksFromServer = gson1.fromJson(body, new ListTypeTokenTask().getType());
            List<Task> tasksFromManager = manager.getHistory();
            List<Task> tasksFromManager1 = new ArrayList<>();
            for (Task task : tasksFromManager) {
                tasksFromManager1.add((Task) task);
            }
            assertNotNull(tasksFromServer, "Задачи не возвращаются");
            assertEquals(tasksFromManager.size(), tasksFromServer.size(), "Некорректное количество задач");
            assertEquals(tasksFromManager1, tasksFromServer);

        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void priority() {
        try {
            int id = manager.getEpics().getFirst().id;
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/prioritized");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            if ((contentType == null) || (!contentType.equals("application/json"))) {
                String error1 = "Need Json to use post";
                System.out.println(error1);
            }
            String body = response.body();
            GsonBuilder gb1 = new GsonBuilder();
            Gson gson1 = gb1
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            List<Task> tasksFromServer = gson1.fromJson(body, new ListTypeTokenTask().getType());
            List<Task> tasksFromManager = manager.getPrioritizedTasks();
            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(tasksFromManager.size(), tasksFromServer.size(), "Некорректное количество задач");
            for (int i = 0; i < tasksFromServer.size(); i++) {
                assertEquals(tasksFromManager.get(i).id, tasksFromServer.get(i).id);
                assertEquals(tasksFromManager.get(i).startTime, tasksFromServer.get(i).startTime);
                assertEquals(tasksFromManager.get(i).name, tasksFromServer.get(i).name);
            }
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void getTaskByID404() {
        try {
            int id = manager.getTasks().getFirst().id + 1000;
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/" + id);
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            assertNull(contentType);
            String body = response.body();
            assertEquals("No task with this id = " + id, body);
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void getSubtaskByID404() {
        try {
            int id = manager.getSubtasks().getFirst().id + 1000;
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/subtasks/" + id);
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            assertNull(contentType);
            String body = response.body();
            assertEquals("No task with this id = " + id, body);
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void getEpicByID404() {
        try {
            int id = manager.getEpics().getFirst().id + 1000;
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/epics/" + id);
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            assertNull(contentType);
            String body = response.body();
            assertEquals("No task with this id = " + id, body);
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void getEpicSubtasks404() {
        try {
            int id = manager.getEpics().getLast().id + 1000;
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/epics/" + id + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            assertNull(contentType);
            String body = response.body();
            assertEquals("No epic with this id = " + id, body);
        } catch (Exception e) {
            System.out.println("Exception = " + e.getMessage());
        }
    }

    @Test
    void postTaskCreate() throws IOException, InterruptedException {
        manager.deleteAllTasks();
        LocalDateTime startTime = LocalDateTime
                .parse("2032/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        Duration duration = Duration.ofMinutes(5);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
        GsonBuilder gb1 = new GsonBuilder();
        Gson gson1 = gb1
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String taskJson1 = gson1.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("name1", tasksFromManager.getFirst().name, "Некорректное имя задачи");
    }

    @Test
    void postTaskUpdate() throws IOException, InterruptedException {
        List<Task> inter = manager.getTasks();
        LocalDateTime startTime = LocalDateTime
                .parse("2032/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        Duration duration = Duration.ofMinutes(5);
        Task task1 = new Task("olol", "ododo", TaskStatus.IN_PROGRESS, startTime, duration);
        task1.setId(manager.getTasks().getFirst().id);
        GsonBuilder gb1 = new GsonBuilder();
        Gson gson1 = gb1
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String taskJson1 = gson1.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(inter.size(), tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(task1.name, tasksFromManager.getFirst().name, "Некорректное имя задачи");
        assertEquals(task1.description, tasksFromManager.getFirst().description);
        assertEquals(task1.status, tasksFromManager.getFirst().status);
        assertEquals(task1.startTime, tasksFromManager.getFirst().startTime);
        assertEquals(task1.duration, tasksFromManager.getFirst().duration);
    }

    @Test
    void postTaskCreate406() throws IOException, InterruptedException {
        manager.deleteAllTasks();
        LocalDateTime startTime = LocalDateTime
                .parse("2032/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        Duration duration = Duration.ofMinutes(5);
        Task task = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
        manager.create(task);
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, startTime, duration);
        GsonBuilder gb1 = new GsonBuilder();
        Gson gson1 = gb1
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String taskJson1 = gson1.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        String body = response.body();
        assertEquals("задача пересекается с существующими", body);
    }

    @Test
    void postTaskUpdate406() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime
                .parse("2032/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        Duration duration = Duration.ofMinutes(5);
        Task task = new Task("ol", "od", TaskStatus.NEW, startTime, duration);
        manager.create(task);
        List<Task> inter = manager.getTasks();
        Task task1 = new Task("olol", "ododo", TaskStatus.IN_PROGRESS, startTime, duration);
        task1.setId(manager.getTasks().getFirst().id);
        GsonBuilder gb1 = new GsonBuilder();
        Gson gson1 = gb1
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String taskJson1 = gson1.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        String body = response.body();
        assertEquals("задача пересекается с существующими", body);
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(inter.size(), tasksFromManager.size(), "Некорректное количество задач");
        assertNotEquals(task1.name, tasksFromManager.getFirst().name, "Некорректное имя задачи");
        assertNotEquals(task1.description, tasksFromManager.getFirst().description);
        assertNotEquals(task1.status, tasksFromManager.getFirst().status);
    }

    @Test
    void postSubtaskCreate() throws IOException, InterruptedException {
        manager.deleteAllTasks();
        LocalDateTime startTime = LocalDateTime
                .parse("2032/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        Duration duration = Duration.ofMinutes(5);
        Epic epic1 = new Epic("name3", "description1", TaskStatus.NEW);
        manager.create(epic1);
        int epicIDTest = manager.getEpics().getFirst().id;
        System.out.println("epicIDTest = " + epicIDTest);
        Subtask subtask1 = new Subtask("name4", "description1", TaskStatus.DONE, epicIDTest,
                startTime, duration);
        GsonBuilder gb1 = new GsonBuilder();
        Gson gson1 = gb1
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String taskJson1 = gson1.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Subtask> tasksFromManager = manager.getSubtasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(subtask1.name, tasksFromManager.getFirst().name, "Некорректное имя задачи");
    }

    @Test
    void postSubtaskUpdate() throws IOException, InterruptedException {
        List<Subtask> inter = manager.getSubtasks();
        LocalDateTime startTime = LocalDateTime
                .parse("2032/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        Duration duration = Duration.ofMinutes(5);
        Subtask subtask1 = new Subtask("olol", "ododo", TaskStatus.IN_PROGRESS,
                manager.getEpics().getFirst().getID(), startTime, duration);
        subtask1.setId(manager.getSubtasks().getFirst().id);
        GsonBuilder gb1 = new GsonBuilder();
        Gson gson1 = gb1
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String taskJson1 = gson1.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Subtask> tasksFromManager = manager.getSubtasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(inter.size(), tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(subtask1.name, tasksFromManager.getFirst().name, "Некорректное имя задачи");
        assertEquals(subtask1.description, tasksFromManager.getFirst().description);
        assertEquals(subtask1.status, tasksFromManager.getFirst().status);
        assertEquals(subtask1.startTime, tasksFromManager.getFirst().startTime);
        assertEquals(subtask1.duration, tasksFromManager.getFirst().duration);
    }

    @Test
    void postSubtaskCreate404() throws IOException, InterruptedException {
        manager.deleteAllTasks();
        LocalDateTime startTime = LocalDateTime
                .parse("2032/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        Duration duration = Duration.ofMinutes(5);
        Epic epic1 = new Epic("name3", "description1", TaskStatus.NEW);
        manager.create(epic1);
        int epicIDTest = manager.getEpics().getFirst().id;
        Subtask subtask = new Subtask("name4", "description1", TaskStatus.DONE, epicIDTest,
                startTime, duration);
        manager.create(subtask);
        Subtask subtask1 = new Subtask("name4", "description1", TaskStatus.DONE, epicIDTest,
                startTime, duration);
        GsonBuilder gb1 = new GsonBuilder();
        Gson gson1 = gb1
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String taskJson1 = gson1.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        String body = response.body();
        assertEquals("задача пересекается с существующими", body);
    }

    @Test
    void postSubtaskUpdate404() throws IOException, InterruptedException {
        manager.deleteAllTasks();
        LocalDateTime startTime = LocalDateTime
                .parse("2032/02/24/06/07/00", DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));
        Duration duration = Duration.ofMinutes(5);
        Epic epic1 = new Epic("name3", "description1", TaskStatus.NEW);
        manager.create(epic1);
        int epicIDTest = manager.getEpics().getFirst().id;
        Subtask subtask = new Subtask("name4", "description1", TaskStatus.DONE, epicIDTest,
                startTime, duration);
        manager.create(subtask);
        startTime = startTime.plus(duration).plus(duration);
        Subtask subtask1 = new Subtask("name4", "description1", TaskStatus.DONE, epicIDTest,
                startTime, duration);
        manager.create(subtask1);
        Subtask subtask2 = new Subtask("name4", "description1", TaskStatus.DONE, epicIDTest,
                startTime, duration);
        subtask2.setId(manager.getSubtasks().getFirst().id);
        GsonBuilder gb1 = new GsonBuilder();
        Gson gson1 = gb1
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String taskJson1 = gson1.toJson(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        String body = response.body();
        assertEquals("задача пересекается с существующими", body);
    }

}