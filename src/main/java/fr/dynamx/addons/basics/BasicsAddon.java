package fr.dynamx.addons.basics;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.dynamx.addons.basics.client.BasicsAddonController;
import fr.dynamx.addons.basics.common.network.SoundsSynchronizedVariable;
import fr.dynamx.addons.basics.common.info.BasicsAddonInfos;
import fr.dynamx.api.contentpack.DynamXAddon;
import fr.dynamx.api.contentpack.registry.SubInfoTypeEntry;
import fr.dynamx.api.network.sync.SynchronizedVariablesRegistry;
import fr.dynamx.common.contentpack.DynamXObjectLoaders;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod(modid = BasicsAddon.ID, version = "1.0.0", name = "DynamX Basics Addon")
@DynamXAddon(modid = BasicsAddon.ID, name = "DynamX Basics", version = "1.0.0")
public class BasicsAddon
{
    public static final String ID = "dynamx_basics";
    public static boolean betterLightsLoaded;
    public static final Map<String, SoundEvent> soundMap = new HashMap<>();

    @DynamXAddon.AddonEventSubscriber
    public static void initAddon()
    {
        DynamXObjectLoaders.WHEELED_VEHICLES.addSubInfoType(new SubInfoTypeEntry<>("BasicsAddon", BasicsAddonInfos.class));
        //FileDefinitionsRegistry.addFileDefinition("KlaxonSound", "klaxonSound", DefinitionType.DynamXDefinitionTypes.STRING.type);
        //FileDefinitionsRegistry.addFileDefinition("SirenSound", "sirenSound", DefinitionType.DynamXDefinitionTypes.STRING.type);
        SynchronizedVariablesRegistry.addSyncVar(SoundsSynchronizedVariable.NAME, SoundsSynchronizedVariable::new);//, (s,e) -> e.getEntity() instanceof ModularVehicleEntity && (e.getSimulationHolder() == SimulationHolder.SERVER_SP ? s.isClient() : s.isServer() || e.getSimulationHolder().isMe(s)));

        if(FMLCommonHandler.instance().getSide().isClient())
            setupClient();
        betterLightsLoaded = Loader.isModLoaded("better_lights");
    }

    private static void setupClient() {
        ClientRegistry.registerKeyBinding(BasicsAddonController.klaxon);
        ClientRegistry.registerKeyBinding(BasicsAddonController.siren);

        ACsGuiApi.registerStyleSheetToPreload(BasicsAddonController.STYLE);
    }
}
