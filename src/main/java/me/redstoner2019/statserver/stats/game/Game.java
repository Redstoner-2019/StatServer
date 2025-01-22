package me.redstoner2019.statserver.stats.game;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.json.JSONObject;

import java.util.UUID;

@Entity
public class Game {
    @Id
    private String id;

    @Column(name="name")
    private String name;

    @Column(name="version")
    private String version;

    @Column(name="versionNumber", unique=true)
    private long versionNumber;

    @Column
    private long created;

    public Game(){
        this.created = System.currentTimeMillis();
    }

    public Game(String name, String version, long versionNumber) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.version = version;
        this.versionNumber = versionNumber;
        this.created = System.currentTimeMillis();
    }

    public long getCreated() {
        return created;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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
        json.put("name",name);
        json.put("version",version);
        json.put("versionNumber",versionNumber);
        json.put("created",created);
        return json;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("name",name);
        json.put("version",version);
        json.put("versionNumber",versionNumber);
        json.put("created",created);
        return json.toString();
    }
}
