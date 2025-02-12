package me.redstoner2019.statserver.stats.game;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameJpaRepository extends JpaRepository<Game, String> {
    Game findByName(String name);
}
