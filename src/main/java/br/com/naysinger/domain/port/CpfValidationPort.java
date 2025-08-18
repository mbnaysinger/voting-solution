package br.com.naysinger.domain.port;

import br.com.naysinger.common.enums.CpfStatus;
import reactor.core.publisher.Mono;

public interface CpfValidationPort {
	Mono<CpfStatus> check(String cpf);
}
