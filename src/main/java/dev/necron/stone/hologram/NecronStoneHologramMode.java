package dev.necron.stone.hologram;

import dev.necron.stone.NecronStone;
import dev.necron.stone.hologram.state.NecronHologramState;
import dev.necron.stone.hologram.state.NecronStoneHologramActive;
import dev.necron.stone.hologram.state.NecronStoneHologramCooldown;

public enum NecronStoneHologramMode {

    ACTIVE(NecronStoneHologramActive.class),
    COOLDOWN(NecronStoneHologramCooldown.class),
    ;


    private final Class<? extends NecronHologramState> hologramClass;

    NecronStoneHologramMode(Class<? extends NecronHologramState> hologramClass) {
        this.hologramClass = hologramClass;
    }

    public NecronHologramState create(NecronStone stone) {
        try {
            return this.hologramClass
                    .getConstructor(NecronStone.class)
                    .newInstance(stone);
        } catch (Exception e) {
            return new NecronStoneHologramActive(stone);
        }
    }
}