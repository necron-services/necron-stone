package dev.necron.stone.configuration.config;

import dev.necron.stone.configuration.NecronStoneConfiguration;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings({"unchecked"})
public enum NecronStoneConfigContainer {

    HEALTH("settings.default-health", 100),
    RESPAWN_AFTER("settings.default-respawn-after", 120),
    REWARD_COMMANDS("settings.default-reward-commands", new ArrayList<>()),

    HOLOGRAM_ACTIVE_HEIGHT("hologram-active.height", 0.5),
    HOLOGRAM_ACTIVE_LINES("hologram-active.lines", new ArrayList<>()),

    HOLOGRAM_COOLDOWN_HEIGHT("hologram-cooldown.height", 0.5),
    HOLOGRAM_COOLDOWN_LINES("hologram-cooldown.lines", new ArrayList<>()),
    ;


    public static void reload() {
        Arrays.asList(NecronStoneConfigContainer.values())
                .forEach(container -> {
                    container.value = NecronStoneConfiguration.CONFIG.get(container.path);
                    if (container.value == null) {
                        container.value = container.defaultValue;
                        NecronStoneConfiguration.CONFIG.set(container.path, container.defaultValue);
                        NecronStoneConfiguration.CONFIG.save();
                    }
                });
    }


    private final String path;
    private final Object defaultValue;
    private Object value;

    NecronStoneConfigContainer(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.value = NecronStoneConfiguration.CONFIG.get(this.path);

        if (this.value == null) {
            this.value = defaultValue;
            NecronStoneConfiguration.CONFIG.set(this.path, defaultValue);
            NecronStoneConfiguration.CONFIG.save();
        }
    }

    public String getPath() {
        return this.path;
    }

    public <T> T getValue() {
        return (T) this.value;
    }

    public <T> T getValue(Class<T> tClass) {
        return tClass.cast(this.value);
    }

    public String asString() {
        return this.value + "";
    }

    public int asInt() {
        return Integer.parseInt(this.asString());
    }

    public long asLong() {
        return Long.parseLong(this.asString());
    }

    public double asDouble() {
        return Double.parseDouble(this.asString());
    }

    public boolean asBoolean() {
        return this.getValue(Boolean.class);
    }

    @Override
    public String toString() {
        return this.asString();
    }
}