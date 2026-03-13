package com.conference.website.service;

import com.conference.website.repository.TalkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ViewTrackingService {

    private final TalkRepository talkRepository;
    private final ConcurrentHashMap<Long, AtomicLong> viewCounterByTalk = new ConcurrentHashMap<>();

    public ViewTrackingService(TalkRepository talkRepository) {
        this.talkRepository = talkRepository;
    }

    public long recordView(long talkId) {
        ensureTalkExists(talkId);
        AtomicLong counter = viewCounterByTalk.computeIfAbsent(talkId, ignored -> new AtomicLong(0));
        return counter.incrementAndGet();
    }

    public long simulateConcurrentViews(long talkId, int viewEvents) {
        ensureTalkExists(talkId);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Long>> tasks = new ArrayList<>();
            for (int i = 0; i < viewEvents; i++) {
                tasks.add(executor.submit(() -> recordView(talkId)));
            }
            for (Future<Long> task : tasks) {
                task.get();
            }
        } catch (Exception exception) {
            throw new RuntimeException("Unable to simulate concurrent view traffic", exception);
        }

        return getCurrentViews(talkId);
    }

    public long getCurrentViews(long talkId) {
        ensureTalkExists(talkId);
        return viewCounterByTalk.getOrDefault(talkId, new AtomicLong(0)).get();
    }

    private void ensureTalkExists(long talkId) {
        if (!talkRepository.existsById(talkId)) {
            throw new NotFoundException("Talk not found: " + talkId);
        }
    }
}
