package dev.necron.stone;

import com.hakan.core.database.DatabaseObject;
import com.hakan.core.utils.yaml.HYaml;
import dev.necron.stone.action.NecronStoneAction;
import dev.necron.stone.configuration.config.NecronStoneConfigContainer;
import dev.necron.stone.database.NecronStoneDatabase;
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

public class NecronStone implements DatabaseObject {

    private final UUID uid;
    private final HYaml dataFile;
    private final NecronStoneAction action;
    private final NecronStoneDatabase database;

    private int health;
    private int maxHealth;
    private Date respawnAt;
    private String lastDamager;
    private Location location;
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
        this.hologram = NecronStoneHologram.register(this);
        this.database = new NecronStoneDatabase(this);
        this.dataFile = HYaml.create(NecronStonePlugin.getInstance().getDataFolder() + "/data/" + this.uid + ".yml");
    }

    public NecronStone(HYaml dataFile) {
        this.uid = UUID.fromString(dataFile.getString("uid"));
        this.lastDamager = dataFile.getString("lastDamager");
        this.location = (Location) dataFile.get("location");
        this.rewards = dataFile.getStringList("rewards");
        this.maxHealth = dataFile.getInt("maxHealth");
        this.health = dataFile.getInt("health");
        this.respawnAt = dataFile.isSet("respawnAt") ? (Date) dataFile.get("respawnAt") : null;
        this.action = new NecronStoneAction(this);
        this.hologram = NecronStoneHologram.register(this);
        this.database = new NecronStoneDatabase(this);
        this.dataFile = dataFile;
    }

    public UUID getUID() {
        return this.uid;
    }

    public HYaml getDataFile() {
        return this.dataFile;
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

    public NecronStoneDatabase getDatabase() {
        return this.database;
    }

    public int getHealth() {
        return this.health;
    }

    public String getLastDamager() {
        return this.lastDamager;
    }

    public List<String> getRewards() {
        return this.rewards;
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
    DATABASE HANDLERS
     */
    public NecronStone update(boolean async) {
        if (async) this.database.updateAsync();
        else this.database.update();
        return this;
    }

    public NecronStone addToUpdateList() {
        NecronStoneDatabase.getProvider().addUpdateObject(this);
        return this;
    }


    /*
    HANDLERS
     */
    public void setHealth(int health) {
        this.health = health;
        this.addToUpdateList();
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        this.addToUpdateList();
    }

    public void setRewards(List<String> rewards) {
        this.rewards = rewards;
        this.addToUpdateList();
    }

    public void setLastDamager(String lastDamager) {
        this.lastDamager = lastDamager;
        this.addToUpdateList();
    }

    public void changeLocation(Location location) {
        this.location = location;
        this.hologram.move(this.hologram.calculateLocation());
        this.addToUpdateList();
    }

    public void changeHologram(NecronStoneHologram hologram) {
        this.hologram.delete();
        this.hologram = hologram;
        this.hologram.create();
    }

    public boolean damage(Player damager, int count) {
        this.setHealth(this.health - count);
        this.setLastDamager(damager.getName());

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
        this.setHealth(this.maxHealth);
        this.changeHologram(NecronStoneHologram.register(this));
    }

    public void destroy(Player destroyer) {
        NecronStoneDestroyEvent event = this.action.onDestroy(destroyer);
        if (event.isCancelled()) {
            this.setHealth(1);
            return;
        }

        long respawnTime = TimeUnit.SECONDS.toMillis(NecronStoneConfigContainer.RESPAWN_AFTER.asInt());

        this.setHealth(-1);
        this.respawnAt = new Date(System.currentTimeMillis() + respawnTime);
        this.changeHologram(NecronStoneHologram.register(this));
    }
}