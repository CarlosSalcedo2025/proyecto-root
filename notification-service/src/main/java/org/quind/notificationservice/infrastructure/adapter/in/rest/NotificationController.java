package org.quind.notificationservice.infrastructure.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.quind.notificationservice.infrastructure.adapter.out.persistence.EventLogEntity;
import org.quind.notificationservice.infrastructure.adapter.out.persistence.MongoEventLogRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final MongoEventLogRepository repository;

    @GetMapping
    public Flux<EventLogEntity> getNotifications(@RequestParam String orderId) {
        return repository.findByAggregateId(orderId);
    }
}
