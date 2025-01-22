package me.redstoner2019.statserver.stats.challenge;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeEntryJpaRepository extends JpaRepository<ChallengeEntry, String> {
    Page<ChallengeEntry> findByChallengeId(String challengeId, Pageable pageable);
}
