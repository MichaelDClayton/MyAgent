package com.concierge.backend.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private UUID bookingId;
    private UUID tenantId;
    private String customerName;
    private String customerEmail;
    private ZonedDateTime scheduledAt;
    private String status;
}
