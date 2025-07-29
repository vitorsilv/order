package com.itau.desafio.infrastructure.messaging;

import com.itau.desafio.config.AwsSnsConfig;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.sns.SnsClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AwsSnsConfig.class)
@TestPropertySource(properties = "spring.cloud.aws.sns.endpoint=http://localhost:4566")
class AwsSnsConfigTest {

    @Autowired
    private SnsClient snsClient;

    @Autowired
    private SnsTemplate snsTemplate;

    @Test
    void shouldCreateSnsClientBean() {
        assertThat(snsClient).isNotNull();
        assertThat(snsClient.serviceName()).isEqualTo("sns");
    }

    @Test
    void shouldCreateSnsTemplateBean() {
        assertThat(snsTemplate).isNotNull();
    }
}