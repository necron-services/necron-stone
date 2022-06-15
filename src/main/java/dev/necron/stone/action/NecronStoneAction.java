package dev.necron.stone.action;

import dev.necron.stone.NecronStone;
import dev.necron.stone.events.NecronStoneDamageEvent;
import dev.necron.stone.events.NecronStoneDestroyEvent;
import dev.necron.stone.events.NecronStoneRespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NecronStoneAction {

    private final NecronStone stone;

    public NecronStoneAction(NecronStone stone) {
        this.stone = stone;
    }

    public NecronStone getStone() {
        return this.stone;
    }

    public NecronStoneDamageEvent onDamage(Player damager, int damage) {
        NecronStoneDamageEvent event = new NecronStoneDamageEvent(this.stone, damager, damage);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public NecronStoneRespawnEvent onRespawn() {
        NecronStoneRespawnEvent event = new NecronStoneRespawnEvent(this.stone);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public NecronStoneDestroyEvent onDestroy(Player destroyer) {
        NecronStoneDestroyEvent event = new NecronStoneDestroyEvent(this.stone, destroyer);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }
}