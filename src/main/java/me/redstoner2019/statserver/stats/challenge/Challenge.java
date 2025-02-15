package me.redstoner2019.statserver.stats.challenge;

import jakarta.persistence.*;
import me.redstoner2019.statserver.stats.util.JSONObjectConverter;
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

    @Column(name="owner")
    private String owner;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Convert(converter = JSONObjectConverter.class)
    @Column(columnDefinition = "TEXT", name = "data")
    private JSONObject data;

    @Column
    private long created;

    public Challenge() {
        this.created = System.currentTimeMillis();
    }

    public Challenge(String name, String description, String game, String owner, JSONObject data) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.created = System.currentTimeMillis();
        this.description = description;
        this.game = game;
        this.data = data;
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public JSONObject getData() {
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
        json.put("owner",owner);
        return json;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
