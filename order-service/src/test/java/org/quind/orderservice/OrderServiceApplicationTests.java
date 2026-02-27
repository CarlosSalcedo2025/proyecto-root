package org.quind.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@org.junit.jupiter.api.Disabled("Deshabilitado debido a incompatibilidad de Testcontainers con variables de entorno en Windows (SONAR_JAVA_PATH)")
class OrderServiceApplicationTests extends BaseIntegrationTest {

    @Test
    void contextLoads() {
    }

}
