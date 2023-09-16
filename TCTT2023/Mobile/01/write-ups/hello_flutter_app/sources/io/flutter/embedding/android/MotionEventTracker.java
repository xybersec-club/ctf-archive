package io.flutter.embedding.android;

import android.util.LongSparseArray;
import android.view.MotionEvent;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes.dex */
public final class MotionEventTracker {
    private static MotionEventTracker INSTANCE;
    private final LongSparseArray<MotionEvent> eventById = new LongSparseArray<>();
    private final PriorityQueue<Long> unusedEvents = new PriorityQueue<>();

    /* loaded from: classes.dex */
    public static class MotionEventId {
        private static final AtomicLong ID_COUNTER = new AtomicLong(0);
        private final long id;

        private MotionEventId(long id) {
            this.id = id;
        }

        public static MotionEventId from(long id) {
            return new MotionEventId(id);
        }

        public static MotionEventId createUnique() {
            return from(ID_COUNTER.incrementAndGet());
        }

        public long getId() {
            return this.id;
        }
    }

    public static MotionEventTracker getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MotionEventTracker();
        }
        return INSTANCE;
    }

    private MotionEventTracker() {
    }

    public MotionEventId track(MotionEvent event) {
        MotionEventId eventId = MotionEventId.createUnique();
        this.eventById.put(eventId.id, MotionEvent.obtain(event));
        this.unusedEvents.add(Long.valueOf(eventId.id));
        return eventId;
    }

    public MotionEvent pop(MotionEventId eventId) {
        while (!this.unusedEvents.isEmpty() && this.unusedEvents.peek().longValue() < eventId.id) {
            this.eventById.remove(this.unusedEvents.poll().longValue());
        }
        if (!this.unusedEvents.isEmpty() && this.unusedEvents.peek().longValue() == eventId.id) {
            this.unusedEvents.poll();
        }
        MotionEvent event = this.eventById.get(eventId.id);
        this.eventById.remove(eventId.id);
        return event;
    }
}
