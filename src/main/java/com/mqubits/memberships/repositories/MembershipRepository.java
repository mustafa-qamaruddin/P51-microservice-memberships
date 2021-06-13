package com.mqubits.memberships.repositories;

import com.mqubits.memberships.models.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, String> {

    CompletableFuture<Membership> findOneByEmployeeAndTimeline(String employeeId, String timelineId);
}
