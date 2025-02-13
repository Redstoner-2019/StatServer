package me.redstoner2019.statserver.stats.version;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VersionJpaRepository extends JpaRepository<Version, String> {
    List<Version> findAllByGame(String game);
    Optional<Version> findVersionByGameAndVersion(String game, String version);
    Optional<Version> findVersionByGameAndVersionNumber(String game, long versionNumber);
}
