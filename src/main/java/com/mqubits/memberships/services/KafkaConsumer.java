package com.mqubits.memberships.services;

import com.mqubits.memberships.models.dto.MembershipDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class KafkaConsumer {

    public final static String TOPIC_MEMBERSHIP = "create-membership";
    protected static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    MembershipService membershipService;

    @KafkaListener(topics = TOPIC_MEMBERSHIP)
    public void receive(MembershipDTO membershipDTO) {
        LOGGER.info("received payload='{}'", membershipDTO.toString());
        membershipService.createMembership(membershipDTO);
    }
}
