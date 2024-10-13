package com.github.jenkaby.persistance.repository;

import com.github.jenkaby.persistance.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {

}
