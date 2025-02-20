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

    @Column(name="name", unique = true)
    private String name;

    @Column(name="owner")
    private String owner;

    @Column(name="icon")
    private String icon = "./default.png";

    @Column
    private long created;

    public Game(){

    }

    public Game(String name, String owner, String icon) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.created = System.currentTimeMillis();
        this.owner = owner;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public String getOwner() {
        return owner;
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

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id",id);
        json.put("name",name);
        json.put("created",created);
        json.put("owner",owner);
        json.put("icon",icon);
        return json;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
}
