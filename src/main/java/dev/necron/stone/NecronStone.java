package dev.necron.stone;

import dev.necron.stone.action.NecronStoneAction;
import dev.necron.stone.configuration.config.NecronStoneConfigContainer;
import dev.necron.stone.events.NecronStoneDestroyEvent;
import dev.necron.stone.hologram.NecronStoneHologram;
import dev.necron.stone.hologram.NecronStoneHologramType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NecronStone {

    public static int DEFAULT_HEALTH = NecronStoneConfigContainer.HEALTH.asInt();
    public static long RESPAWN_AFTER = TimeUnit.SECONDS.toMillis(NecronStoneConfigContainer.RESPAWN_AFTER.asInt());


    private final UUID uid;
    private final int maxHealth;
    private final Location location;
    private final NecronStoneHologram hologram;
    private final NecronStoneAction action;

    private int health;
    private Date respawnAt;
    private String lastDamager;
    private List<String> rewards;

    public NecronStone(Location location) {
        this(location, new ArrayList<>());
    }

    public NecronStone(Location location, List<String> rewards) {
        this.uid = UUID.randomUUID();
        this.lastDamager = "-";
        this.location = location;
        this.rewards = rewards;
        this.health = DEFAULT_HEALTH;
        this.maxHealth = DEFAULT_HEALTH;
        this.hologram = new NecronStoneHologram(this);
        this.action = new NecronStoneAction(this);
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

    public NecronStoneHologram getHologram() {
        return this.hologram;
    }

    public NecronStoneAction getAction() {
        return this.action;
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
    public void respawn() {
        this.respawnAt = null;
        this.lastDamager = "-";
        this.health = DEFAULT_HEALTH;
        this.hologram.changeType(NecronStoneHologramType.ACTIVE);
    }

    public void destroy(Player destroyer) {
        NecronStoneDestroyEvent event = this.action.onDestroy(destroyer);
        if (event.isCancelled()) {
            this.health = 1;
            return;
        }

        this.health = -1;
        this.respawnAt = new Date(System.currentTimeMillis() + RESPAWN_AFTER);
        this.hologram.changeType(NecronStoneHologramType.COOLDOWN);
    }

    public boolean damage(Player damager, int count) {
        this.health -= count;
        this.lastDamager = damager.getName();

        if (this.health <= 0) {
            this.destroy(damager);
            return true;
        }
        return false;
    }
}