package com.mqubits.memberships.services;

import com.mqubits.customers.models.dto.MembershipDTO;
import com.mqubits.memberships.models.Membership;
import com.mqubits.memberships.models.dto.TimelineDTO;
import com.mqubits.memberships.repositories.MembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Component
public class MembershipService {

    public final static String TOPIC_TIMELINE = "suspend-timeline";

    @Autowired
    protected MembershipRepository membershipRepository;
    @Autowired
    private KafkaProducer kafkaProducer;

    public void createMembership(MembershipDTO membershipDTO) {
        var membership = new Membership(membershipDTO.getEmployer(), membershipDTO.getEmployee(), membershipDTO.getTimeline());
        membershipRepository.save(membership);
    }

    public boolean checkMembership(String customerId, String timelineId) {
        var results = membershipRepository.findOneByEmployeeAndTimeline(customerId, timelineId);
        try {
            return (boolean) results.thenApply((Function<Membership, Object>) Membership::getActive).get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    public Optional<Membership> revokeMembership(String customerId, String timelineId) {
        var results = membershipRepository.findOneByEmployeeAndTimeline(customerId, timelineId);
        try {
            return Optional.of(results.thenApply(
                    m -> {
                        m.setActive(false);
                        membershipRepository.save(m);
                        // Membership Service notifies Timeline Service to revoke any open sessions by that customer for that timeline ( Timeline ID + Employee ID )
                        var timelineDto = new TimelineDTO(customerId, timelineId);
                        kafkaProducer.send(TOPIC_TIMELINE, timelineDto);
                        return m;
                    }
            ).get());
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }
}
