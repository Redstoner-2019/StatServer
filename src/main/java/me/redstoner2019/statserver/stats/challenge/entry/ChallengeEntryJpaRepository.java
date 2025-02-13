package me.redstoner2019.statserver.stats.challenge.entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeEntryJpaRepository extends JpaRepository<ChallengeEntry, String> {
    Page<ChallengeEntry> findByChallengeId(String challengeId, Pageable pageable);
}
