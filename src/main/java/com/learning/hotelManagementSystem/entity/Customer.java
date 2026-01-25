package com.learning.hotelManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean isActive;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String emailId;

    @Column(name="contact_no",nullable = false,unique = true)
    private String contactNo;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Customer(String name, String emailId, String contactNo) {
        this.name=name;
        this.emailId=emailId;
        this.contactNo=contactNo;
    }

    @PrePersist
    protected void onCreate() {
        this.isActive=true;
        this.createdAt=LocalDateTime.now();
    }

    @OneToMany(mappedBy = "customer")
    private List<Booking> bookings;
}
