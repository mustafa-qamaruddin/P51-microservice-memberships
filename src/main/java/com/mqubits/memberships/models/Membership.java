package com.mqubits.memberships.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Membership {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(
            name = "system-uuid",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private String id;

    @Column(name = "`created_at`")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "`updated_at`")
    @UpdateTimestamp
    private Timestamp updatedAt;

    private String employer;
    private String employee;
    private String timeline;
    private Boolean active;

    public Membership() {
    }

    public Membership(String employer, String employee, String timeline) {
        this.employer = employer;
        this.employee = employee;
        this.timeline = timeline;
        this.active = true;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
