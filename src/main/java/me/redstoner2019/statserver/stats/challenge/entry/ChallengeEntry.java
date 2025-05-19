package me.redstoner2019.statserver.stats.challenge.entry;

import jakarta.persistence.*;
import me.redstoner2019.statserver.stats.util.JSONObjectConverter;
import org.json.JSONObject;

import java.util.UUID;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "challenge_entry")
public class ChallengeEntry {

    @Id
    private String id;

    private String gameId;
    private String challengeId;
    private String username;
    private String version;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String data; // JSON as String

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created")
    private LocalDateTime created;

    public ChallengeEntry() {
        this.id = UUID.randomUUID().toString();
    }

    public ChallengeEntry(String id, String gameId, String challengeId, String username, String version, String data, LocalDateTime created) {
        this.id = id;
        this.gameId = gameId;
        this.challengeId = challengeId;
        this.username = username;
        this.version = version;
        this.data = data;
        this.created = created;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getChallengeId() { return challengeId; }
    public void setChallengeId(String challengeId) { this.challengeId = challengeId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("gameId",gameId);
        json.put("challengeId",challengeId);
        json.put("username",username);
        json.put("version",version);
        json.put("created",created);
        json.put("data",new JSONObject(data));
        return json;
    }
}
