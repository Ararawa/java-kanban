package tasks;

import java.time.LocalDateTime;

public interface ListTask {
    int getID();

    void setId(int id);

    LocalDateTime getStartTime();

    LocalDateTime getEndTime();
}
