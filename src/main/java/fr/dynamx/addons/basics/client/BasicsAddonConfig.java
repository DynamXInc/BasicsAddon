package fr.dynamx.addons.basics.client;

import fr.dynamx.addons.basics.BasicsAddon;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = BasicsAddon.ID, category = "sound")
@Mod.EventBusSubscriber(modid = BasicsAddon.ID)
public class BasicsAddonConfig {

    @Config.RangeDouble(min = 0.0, max = 1.0)
    @Config.Name("Siren Volume")
    public static double sirenVolume = 1.0;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(BasicsAddon.ID)) {
            ConfigManager.sync(BasicsAddon.ID, Config.Type.INSTANCE);
        }
    }
}
