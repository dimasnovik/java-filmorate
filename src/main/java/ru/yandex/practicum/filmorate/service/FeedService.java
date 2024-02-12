package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Feed;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class FeedService {

    private FeedStorage feedStorage;

    public List<Feed> getFeed(int userId) {
        return feedStorage.getFeed(userId);
    }

    public void createFeed(int userId, EventType eventType, EventOperation eventOperation, int entityId) {
        Feed feed = new Feed(Instant.now().toEpochMilli(), userId, eventType, eventOperation, entityId);
        feedStorage.createFeed(feed);
    }
}
