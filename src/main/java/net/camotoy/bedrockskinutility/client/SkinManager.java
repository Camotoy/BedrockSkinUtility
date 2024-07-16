package net.camotoy.bedrockskinutility.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SkinManager {
    private static SkinManager instance;

    /**
     * If a player cannot be found, then stuff the UUID in here until they spawn.
     */
    private final Cache<UUID, BedrockCachedProperties> cachedPlayers = CacheBuilder.newBuilder()
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build();

    private final Map<UUID, SkinInfo> skinInfo = new ConcurrentHashMap<>();

    public SkinManager() {
        instance = this;
    }

    public Cache<UUID, BedrockCachedProperties> getCachedPlayers() {
        return cachedPlayers;
    }

    public Map<UUID, SkinInfo> getSkinInfo() {
        return skinInfo;
    }

    public static SkinManager getInstance() {
        return instance;
    }
}
