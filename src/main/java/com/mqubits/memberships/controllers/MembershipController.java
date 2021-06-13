package com.mqubits.memberships.controllers;

import com.mqubits.memberships.models.Membership;
import com.mqubits.memberships.services.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class MembershipController {
    @Autowired
    protected MembershipService membershipService;

    @GetMapping("/membership")
    public ResponseEntity<String> isMember(
            @RequestParam(required = true, name = "customerId") String customerId,
            @RequestParam(required = true, name = "timeline") String timelineId
    ) {
        if (membershipService.checkMembership(customerId, timelineId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/membership/revoke")
    public ResponseEntity<Membership> suspendMember(
            @RequestParam(required = true, name = "customerId") String customerId,
            @RequestParam(required = true, name = "timeline") String timelineId
    ) {
        var membership = membershipService.revokeMembership(customerId, timelineId);
        if (membership.isPresent()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
