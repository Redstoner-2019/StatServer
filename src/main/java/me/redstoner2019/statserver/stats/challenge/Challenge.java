package me.redstoner2019.statserver.stats.challenge;

import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.UUID;

@Entity
public class Challenge {
    @Id
    private String id;

    @Column(name="game")
    private String game;

    @Column(name="name", unique=true)
    private String name;

    @Column(name="description")
    private String description;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="data")
    private String data;

    @Column
    private long created;

    public Challenge() {
        this.created = System.currentTimeMillis();
    }

    public Challenge(String name, String description, String game, JSONObject data) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.created = System.currentTimeMillis();
        this.description = description;
        this.game = game;
        this.data = data.toString();
    }

    public String getData() {
        return data;
    }

    public String getDescription() {
        return description;
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

    public String getGame() {
        return game;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("name", name);
        json.put("description",description);
        json.put("created",created);
        json.put("game",game);
        return json;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("name", name);
        json.put("description",description);
        json.put("created",created);
        json.put("game",game);
        return json.toString();
    }
}
