package me.redstoner2019.statserver.stats.challenge.entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChallengeEntryJpaRepository extends JpaRepository<ChallengeEntry, String>, ChallengeEntryCustomRepository {
    Page<ChallengeEntry> findByChallengeIdAndGameAndVersion(String challengeId,String game, String version, Pageable pageable);


    @Query(value = """
        SELECT * FROM challenge_entry
        WHERE challenge_id = :challenge
          AND game = :game
          AND version = :version
        ORDER BY JSON_UNQUOTE(JSON_EXTRACT(data, :jsonPath)) COLLATE utf8mb3_general_ci
        LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<ChallengeEntry> findSortedByJsonKeyMariaDb(
            @Param("challenge") String challenge,
            @Param("game") String game,
            @Param("version") String version,
            @Param("jsonPath") String jsonPath, // like "$.score"
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
        SELECT COUNT(*) FROM challenge_entry
        WHERE challenge_id = :challenge
          AND game = :game
          AND version = :version
    """, nativeQuery = true)
    long countByChallengeAndGameAndVersion(
            @Param("challenge") String challenge,
            @Param("game") String game,
            @Param("version") String version
    );
}
