package me.redstoner2019.statserver.stats.challenge.version;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeVersionJpaRepository extends JpaRepository<ChallengeVersion, String> {
    Optional<ChallengeVersion> findByVersionAndChallenge(String version, String challenge);
}
