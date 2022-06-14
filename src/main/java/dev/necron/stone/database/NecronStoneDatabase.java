package dev.necron.stone.database;

import com.hakan.core.HCore;
import com.hakan.core.database.DatabaseProvider;
import dev.necron.stone.NecronStone;
import dev.necron.stone.database.providers.NecronStoneYamlProvider;

import java.util.concurrent.TimeUnit;

public class NecronStoneDatabase {

    public static void initialize() {
        try {
            DatabaseProvider<NecronStone> provider = new NecronStoneYamlProvider();
            provider.create();
            provider.updateEvery(10, TimeUnit.MINUTES);
            HCore.registerDatabaseProvider(NecronStone.class, provider);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseProvider<NecronStone> getProvider() {
        return HCore.getDatabaseProvider(NecronStone.class);
    }


    private final NecronStone stone;

    public NecronStoneDatabase(NecronStone stone) {
        this.stone = stone;
    }

    public NecronStone getStone() {
        return this.stone;
    }

    public void insert() {
        getProvider().insert(this.stone);
    }

    public void update() {
        getProvider().update(this.stone);
    }

    public void delete() {
        getProvider().delete(this.stone);
    }

    public void insertAsync() {
        HCore.asyncScheduler().run(this::insert);
    }

    public void updateAsync() {
        HCore.asyncScheduler().run(this::update);
    }

    public void deleteAsync() {
        HCore.asyncScheduler().run(this::delete);
    }
}