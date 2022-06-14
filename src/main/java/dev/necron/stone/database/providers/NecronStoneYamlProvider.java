package dev.necron.stone.database.providers;

import com.hakan.core.database.DatabaseProvider;
import com.hakan.core.utils.yaml.HYaml;
import dev.necron.stone.NecronStone;
import dev.necron.stone.NecronStonePlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NecronStoneYamlProvider extends DatabaseProvider<NecronStone> {

    @Override
    public void create() {
    }

    @Nonnull
    @Override
    public List<NecronStone> getValues() {
        List<NecronStone> necronStones = new ArrayList<>();

        File folder = new File(NecronStonePlugin.getInstance().getDataFolder() + "/data/");
        File[] files = folder.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                HYaml yaml = new HYaml(file);
                NecronStone stone = new NecronStone(yaml);
                necronStones.add(stone);
            }
        }

        return necronStones;
    }

    @Nullable
    @Override
    public NecronStone getValue(@Nonnull String s, @Nonnull Object o) {
        File file = new File(NecronStonePlugin.getInstance().getDataFolder() + "/data/" + o + ".yml");
        if (!file.exists())
            return null;

        HYaml yaml = new HYaml(file);
        return new NecronStone(yaml);
    }

    @Override
    public void insert(@Nonnull NecronStone stone) {
        HYaml yaml = stone.getDataFile();
        yaml.set("uid", stone.getUID().toString());
        yaml.set("maxHealth", stone.getMaxHealth());
        yaml.set("location", stone.getLocation());
        yaml.set("rewards", stone.getRewards());
        yaml.save();
    }

    @Override
    public void update(@Nonnull NecronStone stone) {
        HYaml yaml = stone.getDataFile();
        yaml.set("health", stone.getHealth());
        yaml.set("lastDamager", stone.getLastDamager());
        yaml.set("respawnAt", stone.getRespawnDate());
        yaml.set("maxHealth", stone.getMaxHealth());
        yaml.set("location", stone.getLocation());
        yaml.set("rewards", stone.getRewards());
        yaml.save();
    }

    @Override
    public void delete(@Nonnull NecronStone stone) {
        stone.getDataFile().delete();
    }

    @Override
    public void insert(@Nonnull Collection<NecronStone> collection) {
        collection.forEach(this::insert);
    }

    @Override
    public void update(@Nonnull Collection<NecronStone> collection) {
        collection.forEach(this::update);
    }

    @Override
    public void delete(@Nonnull Collection<NecronStone> collection) {
        collection.forEach(this::delete);
    }
}