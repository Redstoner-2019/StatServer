package me.redstoner2019.statserver.stats.challenge.entry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChallengeEntryCustomRepositoryImpl implements ChallengeEntryCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<ChallengeEntry> findByChallengeAndGameAndVersionSortedByDataKey(
            String challenge, String game, String version,
            String sortBy, boolean ascending, int offset, int limit
    ) {
        String direction = ascending ? "ASC" : "DESC";
        System.out.println("Test");
        String sql = """
    SELECT * FROM challenge_entry
    WHERE challenge_id = :challenge
      AND game = :game
      AND version = :version
    ORDER BY JSON_UNQUOTE(JSON_EXTRACT(data, '$.""" + sortBy + """
                                         ')) """ + direction + """
    LIMIT :limit OFFSET :offset
""";

       /*String sql = """
    SELECT * FROM challenge_entry
    WHERE challenge_id = :challenge
      AND game = :game
      AND version = :version
    ORDER BY
        CASE
            WHEN JSON_UNQUOTE(JSON_EXTRACT(data, '$.:sortBy')) REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
            THEN CAST(JSON_UNQUOTE(JSON_EXTRACT(data, '$.:sortBy')) AS DECIMAL)
            ELSE JSON_UNQUOTE(JSON_EXTRACT(data, '$.:sortBy'))
        END DESC
    LIMIT :limit OFFSET :offset
""";*/


        jakarta.persistence.Query query = em.createNativeQuery(sql, ChallengeEntry.class)
                .setParameter("challenge", challenge)
                .setParameter("game", game)
                .setParameter("version", version)
                .setParameter("limit", limit)
                .setParameter("offset", offset);

        @SuppressWarnings("unchecked")
        List<ChallengeEntry> resultList = query.getResultList();

        long count = countByChallengeAndGameAndVersion(challenge, game, version);

        System.out.println("Count: " + count);
        System.out.println("Limit: " + limit);

        return new PageImpl<>(resultList, PageRequest.of(offset / limit, limit), count);
    }

    private long countByChallengeAndGameAndVersion(String challenge, String game, String version) {
        String countSql = """
            SELECT COUNT(*) FROM challenge_entry
            WHERE challenge_id = :challenge
              AND game = :game
              AND version = :version
        """;

        return ((Number) em.createNativeQuery(countSql)
                .setParameter("challenge", challenge)
                .setParameter("game", game)
                .setParameter("version", version)
                .getSingleResult()).longValue();
    }
}

