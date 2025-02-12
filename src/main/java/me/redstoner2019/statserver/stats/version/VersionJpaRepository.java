package me.redstoner2019.statserver.stats.version;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VersionJpaRepository extends JpaRepository<Version, String> {
    Optional<Version> findAllByGame(String game);
}
