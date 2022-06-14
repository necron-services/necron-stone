package dev.necron.stone.events;

import dev.necron.stone.NecronStone;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NecronStoneDestroyEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }


    private final NecronStone stone;
    private final Player destroyer;
    private boolean cancelled;

    public NecronStoneDestroyEvent(NecronStone stone, Player destroyer) {
        this.stone = stone;
        this.destroyer = destroyer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public NecronStone getStone() {
        return this.stone;
    }

    public Player getDestroyer() {
        return this.destroyer;
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