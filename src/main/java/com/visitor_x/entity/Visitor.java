package com.visitor_x.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitorId;

    @Column(nullable = false,length = 100)
    private String name;

    @Column(nullable = false,length = 15)
    private String mobileNumber;

    @Column(nullable = false,length = 100)
    private String email;

    private String address;

    private String purposeOfVisit;

    @Column(nullable = false)
    private String photoUrl;

    @CreationTimestamp
    private LocalDateTime visitDateTime;
}