package br.com.naysinger.config;

import br.com.naysinger.domain.port.CpfValidationPort;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {

    @Bean
    @Primary
    public CpfValidationPort cpfValidationPort() {
        return Mockito.mock(CpfValidationPort.class);
    }
}
