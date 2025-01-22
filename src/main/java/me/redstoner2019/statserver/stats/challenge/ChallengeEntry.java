package me.redstoner2019.statserver.stats.challenge;

import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.UUID;

@Entity
public class ChallengeEntry {
    @Id
    private String id;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="data")
    private String data;
    @Column(name="challengeId")
    private String challengeId;
    @Column(name="userId")
    private String userId;
    @Column
    private long created;
    @Column
    private double score;

    public ChallengeEntry() {
        this.created = System.currentTimeMillis();
    }

    public ChallengeEntry(String challenge, String data, String userId, double score) {
        this.challengeId = challenge;
        this.data = data;
        this.id = UUID.randomUUID().toString();
        this.created = System.currentTimeMillis();
        this.userId = userId;
        this.score = score;
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

    public String getData() {
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
        return json;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("data",data);
        json.put("challengeId",challengeId);
        json.put("userId",userId);
        json.put("created",created);
        json.put("score",score);
        return json.toString();
    }
}
