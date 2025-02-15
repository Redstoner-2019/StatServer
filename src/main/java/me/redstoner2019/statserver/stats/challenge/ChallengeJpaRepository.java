package me.redstoner2019.statserver.stats.challenge;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeJpaRepository extends JpaRepository<Challenge, String> {
    Challenge findByNameAndGame(String name, String game);
    List<Challenge> findByGame(String game);
}
