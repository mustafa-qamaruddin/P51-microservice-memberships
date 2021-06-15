package com.mqubits.memberships.services;

import com.mqubits.customers.models.dto.MembershipDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class MembershipServiceTest {

    @Autowired
    MembershipService membershipService;

    @Autowired
    TestKafkaConsumer testKafkaConsumer;

    @Test
    void canCRUDMembership() {
        // create membership
        var testemployer = "testEmployer";
        var testEmployee = "testEmployee";
        var testTimeline = "testTimeline";
        membershipService.createMembership(new MembershipDTO(testemployer, testEmployee, testTimeline));

        // check membership
        assertFalse(membershipService.checkMembership(testemployer, testTimeline));
        assertTrue(membershipService.checkMembership(testEmployee, testTimeline));

        // revoke membership
        membershipService.revokeMembership(testEmployee, testTimeline);
        assertFalse(membershipService.checkMembership(testEmployee, testTimeline));

        // is notification pushed to kafka?
        verifyCountDown(testKafkaConsumer);
        assertEquals(testKafkaConsumer.getTimelineDTO().getCustomer(), testEmployee);
        assertEquals(testKafkaConsumer.getTimelineDTO().getTimeline(), testTimeline);

        // delete membership clean
        membershipService.membershipRepository.deleteAll();
    }

    private void verifyCountDown(TestKafkaConsumer consumer) {
        try {
            consumer.getLatch().await(59, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            consumer.resetLatch();
        }
        assertThat(consumer.getLatch().getCount(), equalTo(0L));
    }
}