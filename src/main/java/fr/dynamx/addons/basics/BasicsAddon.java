package fr.dynamx.addons.basics;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.dynamx.addons.basics.client.BasicsAddonController;
import fr.dynamx.addons.basics.client.InteractionKeyController;
import fr.dynamx.addons.basics.common.infos.*;
import fr.dynamx.addons.basics.common.network.BasicsAddonSV;
import fr.dynamx.addons.basics.common.network.FuelSynchronizedVariable;
import fr.dynamx.addons.basics.common.network.ImmatriculationPlateSynchronizedVariable;
import fr.dynamx.addons.basics.common.network.InteractionKeySV;
import fr.dynamx.addons.basics.server.CommandBasicsSpawn;
import fr.dynamx.addons.basics.utils.FuelJerrycanUtils;
import fr.dynamx.addons.basics.utils.VehicleKeyUtils;
import fr.dynamx.api.contentpack.DynamXAddon;
import fr.dynamx.api.contentpack.registry.SubInfoTypeEntry;
import fr.dynamx.api.network.sync.SynchronizedVariablesRegistry;
import fr.dynamx.common.contentpack.DynamXObjectLoaders;
import fr.dynamx.common.contentpack.type.objects.ItemObject;
import fr.dynamx.common.items.DynamXItem;
import fr.dynamx.common.items.DynamXItemRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(modid = BasicsAddon.ID, version = "1.0.0", name = "DynamX Basics Addon", dependencies = "before: dynamxmod")
@DynamXAddon(modid = BasicsAddon.ID, name = "DynamX Basics", version = "1.0.0")
public class BasicsAddon {
    public static final String ID = "dynamx_basics";
    public static boolean betterLightsLoaded;
    public static final Map<String, SoundEvent> soundMap = new HashMap<>();

    public static DynamXItem<?> keysItem;
    public static DynamXItem<?> jerrycanItem;

    @DynamXAddon.AddonEventSubscriber
    public static void initAddon() {
        DynamXObjectLoaders.WHEELED_VEHICLES.addSubInfoType(new SubInfoTypeEntry<>("BasicsAddon", BasicsAddonInfos.class));
        DynamXObjectLoaders.WHEELED_VEHICLES.addSubInfoType(new SubInfoTypeEntry<>("ImmatriculationPlate", ImmatriculationPlateInfos::new, false));
        DynamXObjectLoaders.WHEELED_VEHICLES.addSubInfoType(new SubInfoTypeEntry<>("FuelTank", FuelTankInfos::new, false));
        DynamXObjectLoaders.WHEELED_VEHICLES.addSubInfoType(new SubInfoTypeEntry<>("SpeedDisplay", SpeedDisplayInfos::new, false));
        DynamXObjectLoaders.ITEMS.addSubInfoType(new SubInfoTypeEntry("BasicsAddon", BasicsItemInfo.class));

        SynchronizedVariablesRegistry.addSyncVar(BasicsAddonSV.NAME, BasicsAddonSV::new);
        SynchronizedVariablesRegistry.addSyncVar(ImmatriculationPlateSynchronizedVariable.NAME, ImmatriculationPlateSynchronizedVariable::new);
        SynchronizedVariablesRegistry.addSyncVar(FuelSynchronizedVariable.NAME, FuelSynchronizedVariable::new);
        SynchronizedVariablesRegistry.addSyncVar(InteractionKeySV.NAME, InteractionKeySV::new);
        if (FMLCommonHandler.instance().getSide().isClient()) {
            setupClient();
        }
        betterLightsLoaded = Loader.isModLoaded("better_lights");

        registerKey();
        registerJerrycan();
    }

    private static void registerKey() {
        keysItem = new DynamXItem(ID, "car_keys", "disable_rendering") {
            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                if(VehicleKeyUtils.hasLinkedVehicle(stack)) {
                    tooltip.add(I18n.format("basadd.key.linked.to", stack.getTagCompound().getString("VehicleName")));
                }
            }
        };
        keysItem.setCreativeTab(DynamXItemRegistry.objectTab);
        ItemObject<?> info = (ItemObject<?>) keysItem.getInfo();
        BasicsItemInfo bas = new BasicsItemInfo(info);
        bas.setKey(true);
        info.addSubProperty(bas);
    }

    private static void registerJerrycan() {
        jerrycanItem = new DynamXItem(ID, "fuel_jerrycan", "item/jerrycan.obj"){
            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                if(FuelJerrycanUtils.hasFuel(stack)) {
                    tooltip.add(I18n.format("basadd.fuel.jerrycan",  FuelJerrycanUtils.getFuel(stack)));
                }
            }
        };
        jerrycanItem.setCreativeTab(DynamXItemRegistry.objectTab);
        ItemObject<?> info = (ItemObject<?>) jerrycanItem.getInfo();
        BasicsItemInfo bas = new BasicsItemInfo(info);
        bas.setFuelCapacity(60);
        info.addSubProperty(bas);
    }

    @SideOnly(Side.CLIENT)
    private static void setupClient() {
        ClientRegistry.registerKeyBinding(BasicsAddonController.hornKey);
        ClientRegistry.registerKeyBinding(BasicsAddonController.sirenKey);
        ClientRegistry.registerKeyBinding(BasicsAddonController.beaconKey);
        ClientRegistry.registerKeyBinding(BasicsAddonController.headlights);
        ClientRegistry.registerKeyBinding(BasicsAddonController.turnLeft);
        ClientRegistry.registerKeyBinding(BasicsAddonController.turnRight);
        ClientRegistry.registerKeyBinding(BasicsAddonController.warnings);
        ClientRegistry.registerKeyBinding(InteractionKeyController.interaction);

        ACsGuiApi.registerStyleSheetToPreload(BasicsAddonController.STYLE);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandBasicsSpawn());
    }
}
