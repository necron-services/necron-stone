package dev.necron.stone;

import com.hakan.core.HCore;
import com.hakan.core.database.DatabaseObject;
import com.hakan.core.utils.yaml.HYaml;
import dev.necron.stone.action.NecronStoneAction;
import dev.necron.stone.configuration.config.NecronStoneConfigContainer;
import dev.necron.stone.database.NecronStoneDatabase;
import dev.necron.stone.events.NecronStoneDamageEvent;
import dev.necron.stone.events.NecronStoneDestroyEvent;
import dev.necron.stone.events.NecronStoneRespawnEvent;
import dev.necron.stone.hologram.NecronStoneHologram;
import dev.necron.stone.hologram.NecronStoneHologramMode;
import dev.necron.stone.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private Material blockType;
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
        this.blockType = location.getBlock().getType();
        this.maxHealth = NecronStoneConfigContainer.HEALTH.asInt();
        this.health = this.maxHealth;
        this.action = new NecronStoneAction(this);
        this.hologram = new NecronStoneHologram(this);
        this.database = new NecronStoneDatabase(this);
        this.dataFile = HYaml.create(NecronStonePlugin.getInstance().getDataFolder() + "/data/" + this.uid + ".yml");
    }

    public NecronStone(HYaml dataFile) {
        this.uid = UUID.fromString(dataFile.getString("uid"));
        this.location = LocationUtil.deserialize(dataFile.getString("location"));
        this.respawnAt = dataFile.isSet("respawnAt") ? new Date(dataFile.getLong("respawnAt")) : null;
        this.lastDamager = dataFile.getString("lastDamager");
        this.rewards = dataFile.getStringList("rewards");
        this.blockType = Material.getMaterial(dataFile.getString("blockType"));
        this.maxHealth = dataFile.getInt("maxHealth");
        this.health = dataFile.getInt("health");
        this.action = new NecronStoneAction(this);
        this.hologram = new NecronStoneHologram(this);
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

    public Material getBlockType() {
        return this.blockType;
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
        return (!this.isAlive()) ? (this.respawnAt.getTime() - System.currentTimeMillis()) : 0;
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
    public void setBlockType(Material blockType) {
        this.blockType = blockType;
        this.addToUpdateList();
    }

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

    public void changeHologramMode(NecronStoneHologramMode mode) {
        this.hologram.setMode(mode);
    }

    public boolean damage(Player damager, int count) {
        NecronStoneDamageEvent event = this.action.onDamage(damager, count);
        if (event.isCancelled())
            return false;

        this.setHealth(this.health - event.getDamage());
        this.setLastDamager(damager.getName());
        this.hologram.update();

        HCore.sendActionBar(damager, NecronStoneConfigContainer.MESSAGE_INFO_BREAK_STONE_ACTIONBAR.asString()
                .replace("%health%", ((this.health != -1) ? this.health : 0) + "")
                .replace("%max_health%", this.maxHealth + ""));

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
        this.hologram.setMode(NecronStoneHologramMode.ACTIVE);
        this.location.getBlock().setType(this.blockType);
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
        this.hologram.setMode(NecronStoneHologramMode.COOLDOWN);
        this.location.getBlock().setType(Material.valueOf(NecronStoneConfigContainer.BLOCK_TYPE_AFTER_DESTROY.asString()));
    }
}