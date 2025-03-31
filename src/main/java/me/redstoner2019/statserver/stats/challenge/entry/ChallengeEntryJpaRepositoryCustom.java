package me.redstoner2019.statserver.stats.challenge.entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChallengeEntryJpaRepositoryCustom {
    int countPagesForTopEntries(String gameId, String challengeId, String version, int pageSize);
    Page<ChallengeEntry> findTopEntriesPerUser(
            String gameId,
            String challengeId,
            String version,
            String jsonKey,
            JsonValueType valueType,
            boolean ascending,
            Pageable pageable
    );
}
