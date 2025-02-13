package me.redstoner2019.statserver.stats.challenge.version;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "versionChallenge",
        uniqueConstraints = @UniqueConstraint(columnNames = {"version", "challenge"}))
public class ChallengeVersion {
    @Id
    private String id;
    @Column(name="version")
    private String version;
    @Column(name="challenge")
    private String challenge;

    public ChallengeVersion() {
    }

    public ChallengeVersion(String version, String challenge) {
        this.version = version;
        this.challenge = challenge;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }
}
