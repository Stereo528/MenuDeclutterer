package dev.stereo528.declutter.client;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuDeclutterer implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Declutter");
    @Override
    public void onInitializeClient() {
        LOGGER.info("MenuDeclutterer Loaded!");
        MidnightConfig.init("declutter", DeclutterConfig.class);
    }
}
