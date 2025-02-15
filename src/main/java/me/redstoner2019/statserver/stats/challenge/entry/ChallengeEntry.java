package me.redstoner2019.statserver.stats.challenge.entry;

import jakarta.persistence.*;
import me.redstoner2019.statserver.stats.util.JSONObjectConverter;
import org.json.JSONObject;

import java.util.UUID;

@Entity
public class ChallengeEntry {
    @Id
    private String id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Convert(converter = JSONObjectConverter.class)
    @Column(columnDefinition = "TEXT", name = "data")
    private JSONObject data;

    @Column(name="challengeId")
    private String challengeId;

    @Column(name="game")
    private String game;

    @Column(name="version")
    private String version;

    @Column(name="userId")
    private String userId;

    @Column
    private long created;

    @Column
    private double score;

    public ChallengeEntry() {
        this.created = System.currentTimeMillis();
    }

    public ChallengeEntry(String challenge,String game, String version, JSONObject data, String userId, double score) {
        this.challengeId = challenge;
        this.data = data;
        this.id = UUID.randomUUID().toString();
        this.created = System.currentTimeMillis();
        this.userId = userId;
        this.score = score;
        this.game = game;
        this.version = version;
    }

    public String getGame() {
        return game;
    }

    public String getVersion() {
        return version;
    }

    public double getScore() {
        return score;
    }

    public String getUserId() {
        return userId;
    }

    public long getCreated() {
        return created;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public String getId() {
        return id;
    }

    public JSONObject getData() {
        return data;
    }

    public String getChallenge() {
        return challengeId;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("data",data);
        json.put("challengeId",challengeId);
        json.put("userId",userId);
        json.put("created",created);
        json.put("score",score);
        json.put("game",game);
        json.put("version",version);
        return json;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
