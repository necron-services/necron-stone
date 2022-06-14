package dev.necron.stone.events;

import dev.necron.stone.NecronStone;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NecronStoneRespawnEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }


    private final NecronStone stone;
    private boolean cancelled;

    public NecronStoneRespawnEvent(NecronStone stone) {
        this.stone = stone;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public NecronStone getStone() {
        return this.stone;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}