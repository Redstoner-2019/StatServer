package me.redstoner2019.statserver.stats.game;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameJpaRepository extends JpaRepository<Game, String> {
    Game findByNameAndVersionNumber(String name, long versionNumber);
    Optional<Game> findByNameAndVersion(String name, String version);
    List<Game> findByName(String name);
    List<Game> findAll();
}
