package me.redstoner2019.statserver.stats.challenge.entry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ChallengeEntryJpaRepositoryImpl implements ChallengeEntryJpaRepositoryCustom {

    public int countPagesForTopEntries(String gameId, String challengeId, String version, int pageSize) {
        String countQuery = """
    SELECT COUNT(DISTINCT ce.username)
    FROM challenge_entry ce
    WHERE ce.game_id = :gameId AND ce.challenge_id = :challengeId AND ce.version = :version
""";



        Long total = ((Number) entityManager.createNativeQuery(countQuery)
                .setParameter("gameId", gameId)
                .setParameter("challengeId", challengeId)
                .setParameter("version", version)
                .getSingleResult()).longValue();

        return (int) Math.ceil((double) total / pageSize);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ChallengeEntry> findTopEntriesPerUser(
            String gameId,
            String challengeId,
            String version,
            String jsonKey,
            JsonValueType valueType,
            boolean ascending,
            Pageable pageable) {

        String valueExpr;
        switch (valueType) {
            case INTEGER -> valueExpr = "CAST(JSON_UNQUOTE(JSON_EXTRACT(data, CONCAT('$.', :jsonKey))) AS SIGNED)";
            case DOUBLE -> valueExpr = "CAST(JSON_UNQUOTE(JSON_EXTRACT(data, CONCAT('$.', :jsonKey))) AS DECIMAL(20, 10))";
            case STRING -> valueExpr = "JSON_UNQUOTE(JSON_EXTRACT(data, CONCAT('$.', :jsonKey)))";
            default -> throw new IllegalArgumentException("Unsupported type");
        }

        String nativeQuery = String.format("""
            SELECT ce.*
            FROM challenge_entry ce
            WHERE ce.game_id = :gameId AND ce.challenge_id = :challengeId AND ce.version = :version
            ORDER BY %1$s %2$s, ce.created ASC
        """, valueExpr, ascending ? "ASC" : "DESC");

        @SuppressWarnings("unchecked")
        List<ChallengeEntry> allEntries = entityManager.createNativeQuery(nativeQuery, ChallengeEntry.class)
                .setParameter("gameId", gameId)
                .setParameter("challengeId", challengeId)
                .setParameter("version", version)
                .setParameter("jsonKey", jsonKey)
                .getResultList();

        Map<String, ChallengeEntry> topEntries = new LinkedHashMap<>();
        for (ChallengeEntry entry : allEntries) {
            topEntries.compute(entry.getUsername(), (user, existing) -> {
                if (existing == null) return entry;
                return entry.getCreated().isAfter(existing.getCreated()) ? entry : existing;
            });
        }

        List<ChallengeEntry> result = topEntries.values().stream()
                .sorted((e1, e2) -> {
                    int cmp = extractComparableValue(e1, jsonKey, valueType)
                            .compareTo(extractComparableValue(e2, jsonKey, valueType));
                    if (cmp == 0) return e1.getCreated().compareTo(e2.getCreated());
                    return cmp;
                })
                .collect(Collectors.toList());

        if (!ascending) {
            Collections.reverse(result);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), result.size());
        List<ChallengeEntry> paged = result.subList(start, end);
        return new PageImpl<>(paged, pageable, result.size());
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> T extractComparableValue(ChallengeEntry entry, String key, JsonValueType type) {
        try {
            String json = entry.getData();
            String match = json.replaceAll(".*\\\"" + key + "\\\":(.*?)([},]).*", "$1");
            return switch (type) {
                case STRING -> (T) match.replaceAll("[\"{}]", "").trim();
                case INTEGER -> (T) Integer.valueOf(match.trim());
                case DOUBLE -> (T) Double.valueOf(match.trim());
            };
        } catch (Exception e) {
            return null;
        }
    }
}