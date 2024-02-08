package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Feed {
    private int eventId;
    private long timestamp;
    private int userId;
    private EventType eventType;
    private EventOperation operation;
    private int entityId;

    public Feed(long timestamp, int userId, EventType eventType, EventOperation operation, int entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }

}
