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

    @Column(name="releaseURL")
    private String releaseURL;

    public Version() {
    }

    public Version(String game, String version, long versionNumber, String releaseURL) {
        this.game = game;
        this.version = version;
        this.versionNumber = versionNumber;
        this.releaseURL = releaseURL;
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

    public String getReleaseURL() {
        return releaseURL;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("game",game);
        json.put("version", version);
        json.put("versionNumber", versionNumber);
        json.put("releaseURL", releaseURL);
        return json;
    }
}
