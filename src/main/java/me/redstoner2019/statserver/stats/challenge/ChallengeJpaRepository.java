package me.redstoner2019.statserver.stats.challenge;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeJpaRepository extends JpaRepository<Challenge, String> {
    Challenge findByNameAndGame(String name, String game);
}
