package org.quind.notificationservice;

import org.junit.jupiter.api.Test;
import org.quind.notificationservice.infrastructure.adapter.out.persistence.MongoEventLogRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class NotificationServiceApplicationTests {

    @MockBean
    private MongoEventLogRepository eventLogRepository;

    @Test
    void contextLoads() {
    }

}
