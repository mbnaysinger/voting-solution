package br.com.naysinger.infrastructure.adapter;

import br.com.naysinger.common.enums.CpfStatus;
import br.com.naysinger.common.exception.CpfNotFoundException;
import br.com.naysinger.domain.port.CpfValidationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class CpfValidationFakeClient implements CpfValidationPort {
	private static final Logger LOGGER = LoggerFactory.getLogger(CpfValidationFakeClient.class);
	private final ThreadLocalRandom rnd = ThreadLocalRandom.current();
	
	@Override
	public Mono<CpfStatus> check(String cpf) {
		String maskedCpf = cpf != null && cpf.length() >= 4 ? "***********".substring(0, Math.max(0, cpf.length() - 4)) + cpf.substring(cpf.length() - 4) : "***";
		LOGGER.info("[CpfValidationFakeClient] Validando CPF (fake). cpfMasked={}", maskedCpf);
		long delay = rnd.nextLong(100, 501);
		return Mono.delay(Duration.ofMillis(delay))
			.then(Mono.defer(() -> {
				int roll = rnd.nextInt(100);
				// 30% inválido/não apto -> 404
				if (roll < 30) {
					LOGGER.warn("[CpfValidationFakeClient] CPF inválido/não apto. cpfMasked={}", maskedCpf);
					return Mono.error(new CpfNotFoundException(cpf));
				}
				// 70% válidos: metade apto, metade não apto -> não apto também como 404 conforme requisito
				boolean able = rnd.nextBoolean();
				if (able) {
					LOGGER.info("[CpfValidationFakeClient] CPF apto para votar. cpfMasked={}", maskedCpf);
					return Mono.just(CpfStatus.ABLE_TO_VOTE);
				}
				LOGGER.warn("[CpfValidationFakeClient] CPF não apto para votar. cpfMasked={}", maskedCpf);
				return Mono.error(new CpfNotFoundException(cpf));
			}));
	}
}
