package com.concierge.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concierge.backend.model.FailedMessage;

public interface FailedMessageRepository extends JpaRepository<FailedMessage, Long> {

    List<FailedMessage> findByStatus(String string);

}