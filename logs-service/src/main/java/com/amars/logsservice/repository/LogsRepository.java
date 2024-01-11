package com.amars.logsservice.repository;

import com.amars.logsservice.model.Logs;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogsRepository extends MongoRepository<Logs, String> {
}
