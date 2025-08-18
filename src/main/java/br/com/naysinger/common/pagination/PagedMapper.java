package br.com.naysinger.common.pagination;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Component
public class PagedMapper {

    /**
     * Converte PagedResult de um tipo para outro de forma reativa
     */
    public <T, R> Mono<PagedResult<R>> mapReactive(PagedResult<T> source, Function<List<T>, Mono<List<R>>> mapper) {
        if (source == null || source.getContent() == null) {
            return Mono.just(PagedResult.empty(0, 10));
        }

        return mapper.apply(source.getContent())
                .map(mappedContent -> new PagedResult<>(
                        mappedContent,
                        source.getCurrentPage(),
                        source.getPageSize(),
                        source.getTotalElements(),
                        source.getTotalPages(),
                        source.getSortBy(),
                        source.getSortDirection()
                ));
    }

    /**
     * Converte PagedResult de um tipo para outro de forma síncrona
     */
    public <T, R> PagedResult<R> map(PagedResult<T> source, Function<T, R> mapper) {
        if (source == null || source.getContent() == null) {
            return PagedResult.empty(0, 10);
        }

        List<R> mappedContent = source.getContent().stream()
                .map(mapper)
                .toList();

        return new PagedResult<>(
                mappedContent,
                source.getCurrentPage(),
                source.getPageSize(),
                source.getTotalElements(),
                source.getTotalPages(),
                source.getSortBy(),
                source.getSortDirection()
        );
    }
}
