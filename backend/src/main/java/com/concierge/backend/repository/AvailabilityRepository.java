package com.concierge.backend.repository;

import com.concierge.backend.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
    
    // We can use native query or JPQL. With RLS, the database enforces tenant isolation.
    @Query("SELECT a FROM Availability a WHERE a.itemId = :itemId AND a.id = :availabilityId AND a.remainingCapacity > 0")
    Optional<Availability> findAvailableSlot(@Param("itemId") UUID itemId, @Param("availabilityId") UUID availabilityId);
}
