package com.github.camotoy.bedrockskinutility.client;

import com.github.camotoy.bedrockskinutility.client.pluginmessage.CapeDecoder;
import com.github.camotoy.bedrockskinutility.client.pluginmessage.SkinDataDecoder;
import com.github.camotoy.bedrockskinutility.client.pluginmessage.SkinInfoDecoder;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SkinManager {
    private static SkinManager instance;

    private final CapeDecoder capeDecoder;
    private final SkinDataDecoder skinDataDecoder;
    private final SkinInfoDecoder skinInfoDecoder;

    /**
     * If a player cannot be found, then stuff the UUID in here until they spawn.
     */
    private final Cache<UUID, BedrockCachedProperties> cachedPlayers = CacheBuilder.newBuilder()
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build();

    private final Map<UUID, SkinInfo> skinInfo = new ConcurrentHashMap<>();

    public SkinManager(Logger logger) {
        instance = this;
        this.capeDecoder = new CapeDecoder(logger, this);
        this.skinDataDecoder = new SkinDataDecoder(logger, this);
        this.skinInfoDecoder = new SkinInfoDecoder(logger, this);
    }

    public CapeDecoder getCapeDecoder() {
        return capeDecoder;
    }

    public SkinDataDecoder getSkinDataDecoder() {
        return skinDataDecoder;
    }

    public SkinInfoDecoder getSkinInfoDecoder() {
        return skinInfoDecoder;
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
