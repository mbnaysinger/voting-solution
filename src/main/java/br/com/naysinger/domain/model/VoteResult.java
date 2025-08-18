package br.com.naysinger.domain.model;

import br.com.naysinger.common.enums.SessionResult;

public record VoteResult(long simVotes, long naoVotes, long totalVotes) {

    public SessionResult getWinner() {
        if (simVotes > naoVotes) return SessionResult.SIM;
        if (naoVotes > simVotes) return SessionResult.NAO;
        return SessionResult.EMPATE;
    }
}
