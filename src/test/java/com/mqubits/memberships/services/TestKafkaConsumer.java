package com.mqubits.memberships.services;

import com.mqubits.memberships.models.dto.TimelineDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

import static com.mqubits.memberships.services.MembershipService.TOPIC_TIMELINE;

@Component
public class TestKafkaConsumer {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestKafkaConsumer.class);

    protected CountDownLatch latch = new CountDownLatch(1);
    protected String payload = null;
    private TimelineDTO timelineDTO;

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    @KafkaListener(topics = TOPIC_TIMELINE)
    public void receive(TimelineDTO timelineDTO) {
        LOGGER.info("received payload='{}'", timelineDTO.toString());
        setPayload(timelineDTO.toString());
        this.timelineDTO = timelineDTO;
        latch.countDown();
    }

    public TimelineDTO getTimelineDTO() {
        return timelineDTO;
    }
}
