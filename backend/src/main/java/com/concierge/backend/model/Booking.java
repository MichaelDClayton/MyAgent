package com.concierge.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @UuidGenerator // This replaces the deprecated GenericGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "scheduled_at", nullable = false)
    private ZonedDateTime scheduledAt;

    @Column(nullable = false)
    private String status = "PENDING";
}
