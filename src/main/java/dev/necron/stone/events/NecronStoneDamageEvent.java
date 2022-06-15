package dev.necron.stone.events;

import dev.necron.stone.NecronStone;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NecronStoneDamageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }


    private final NecronStone stone;
    private final Player damager;
    private int damage;
    private boolean cancelled;

    public NecronStoneDamageEvent(NecronStone stone, Player damager, int damage) {
        this.stone = stone;
        this.damager = damager;
        this.damage = damage;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public NecronStone getStone() {
        return this.stone;
    }

    public Player getDamager() {
        return this.damager;
    }

    public int getDamage() {
        return this.damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}