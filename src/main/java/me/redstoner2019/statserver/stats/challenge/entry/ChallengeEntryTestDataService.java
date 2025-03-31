package me.redstoner2019.statserver.stats.challenge.entry;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ChallengeEntryTestDataService {

    private final ChallengeEntryJpaRepository repository;

    public ChallengeEntryTestDataService(ChallengeEntryJpaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void resetAndFillTestData() {
        repository.deleteAll();

        String gameId = "gameId";
        String challengeId = "challengeId";

        // User A - two entries with same score, second one newer
        createEntry("userA", gameId, challengeId, "v1.3.0",20.0, LocalDateTime.now().minusDays(2));
        createEntry("userA", gameId, challengeId, "v1.3.0",120.0, LocalDateTime.now());

        // User B - two entries with same score, older one should win for ranking
        createEntry("userB", gameId, challengeId, "v1.2.0",120.0, LocalDateTime.now().minusDays(3));
        createEntry("userB", gameId, challengeId, "v1.2.0",120.0, LocalDateTime.now().minusDays(1));

        // User C - entry with best score
        createEntry("userC", gameId, challengeId, "v1.3.0",90.5, LocalDateTime.now().minusDays(1));

        // User D - entry with worst score
        createEntry("userD", gameId, challengeId, "v1.3.0",150.5, LocalDateTime.now().minusDays(1));
        createEntry("userD", gameId, challengeId, "v1.3.0",151.5, LocalDateTime.now().minusDays(1));
    }

    private void createEntry(String username, String gameId, String challengeId, String version, double time, LocalDateTime createdAt) {
        ChallengeEntry entry = new ChallengeEntry();
        entry.setUsername(username);
        entry.setGameId(gameId);
        entry.setChallengeId(challengeId);
        entry.setData("{" + "\"time\":" + time + "}");
        entry.setCreated(createdAt);
        entry.setVersion(version);
        repository.save(entry);
    }
}
