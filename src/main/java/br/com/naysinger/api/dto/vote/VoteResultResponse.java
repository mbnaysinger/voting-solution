package br.com.naysinger.api.dto.vote;

import br.com.naysinger.common.enums.SessionResult;

public record VoteResultResponse(long simVotes, long naoVotes, long totalVotes, SessionResult winner) {

}
