package dev.necron.stone;

import dev.necron.stone.action.NecronStoneAction;
import dev.necron.stone.configuration.config.NecronStoneConfigContainer;
import dev.necron.stone.events.NecronStoneDestroyEvent;
import dev.necron.stone.events.NecronStoneRespawnEvent;
import dev.necron.stone.hologram.NecronStoneHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NecronStone {

    private final UUID uid;
    private final int maxHealth;
    private final Location location;
    private final NecronStoneAction action;

    private int health;
    private Date respawnAt;
    private String lastDamager;
    private List<String> rewards;
    private NecronStoneHologram hologram;

    public NecronStone(Location location) {
        this(location, new ArrayList<>());
    }

    public NecronStone(Location location, List<String> rewards) {
        this.uid = UUID.randomUUID();
        this.lastDamager = "-";
        this.location = location;
        this.rewards = rewards;
        this.maxHealth = NecronStoneConfigContainer.HEALTH.asInt();
        this.health = this.maxHealth;
        this.action = new NecronStoneAction(this);
        this.hologram = NecronStoneHologram.create(this);
    }

    public UUID getUID() {
        return this.uid;
    }

    public Location getLocation() {
        return this.location.clone();
    }

    public int getMaxHealth() {
        return this.maxHealth;
    }

    public NecronStoneAction getAction() {
        return this.action;
    }

    public NecronStoneHologram getHologram() {
        return this.hologram;
    }

    public void setHologram(NecronStoneHologram hologram) {
        this.hologram = hologram;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getLastDamager() {
        return this.lastDamager;
    }

    public void setLastDamager(String lastDamager) {
        this.lastDamager = lastDamager;
    }

    public List<String> getRewards() {
        return this.rewards;
    }

    public void setRewards(List<String> rewards) {
        this.rewards = rewards;
    }

    public Date getRespawnDate() {
        return this.respawnAt;
    }

    public long getRemainTime() {
        return (this.health == -1) ? (this.respawnAt.getTime() - System.currentTimeMillis()) : 0;
    }

    public boolean isAlive() {
        return (this.health != -1);
    }


    /*
    HANDLERS
     */
    public boolean damage(Player damager, int count) {
        this.health -= count;
        this.lastDamager = damager.getName();

        if (this.health <= 0) {
            this.destroy(damager);
            return true;
        }
        return false;
    }

    public void respawn() {
        NecronStoneRespawnEvent event = this.action.onRespawn();
        if (event.isCancelled()) {
            this.respawnAt = new Date();
            return;
        }

        this.respawnAt = null;
        this.lastDamager = "-";
        this.health = this.maxHealth;
        this.hologram.delete();
        this.hologram = NecronStoneHologram.create(this);
    }

    public void destroy(Player destroyer) {
        NecronStoneDestroyEvent event = this.action.onDestroy(destroyer);
        if (event.isCancelled()) {
            this.health = 1;
            return;
        }

        long respawnTime = TimeUnit.SECONDS.toMillis(NecronStoneConfigContainer.RESPAWN_AFTER.asInt());

        this.health = -1;
        this.respawnAt = new Date(System.currentTimeMillis() + respawnTime);
        this.hologram.delete();
        this.hologram = NecronStoneHologram.create(this);
    }
}