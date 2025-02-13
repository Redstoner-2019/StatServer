package me.redstoner2019.statserver.stats.version;

import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.UUID;

@Entity
@Table(name = "version",
        uniqueConstraints = @UniqueConstraint(columnNames = {"game", "versionNumber"}))
public class Version {
    @Id
    private String id;

    @Column(name="game")
    private String game;

    @Column(name="version")
    private String version;

    @Column(name="versionNumber")
    private long versionNumber;

    public Version() {
    }

    public Version(String game, String version, long versionNumber) {
        this.game = game;
        this.version = version;
        this.versionNumber = versionNumber;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getGame() {
        return game;
    }

    public String getVersion() {
        return version;
    }

    public long getVersionNumber() {
        return versionNumber;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("game",game);
        json.put("version", version);
        return json;
    }
}
