package me.redstoner2019.statserver.stats.challenge.entry;

import org.springframework.data.domain.Page;

public interface ChallengeEntryCustomRepository {
    Page<ChallengeEntry> findByChallengeAndGameAndVersionSortedByDataKey(
            String challenge, String game, String version,
            String sortBy, boolean ascending, int offset, int limit
    );
}

