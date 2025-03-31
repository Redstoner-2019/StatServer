package me.redstoner2019.statserver.stats.challenge.entry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChallengeEntryJpaRepository extends JpaRepository<ChallengeEntry, UUID>, ChallengeEntryJpaRepositoryCustom {
}
