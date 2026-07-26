package fr.dynamx.addon.basic;

import com.mojang.serialization.Codec;
import fr.dynamx.addon.basic.fuel.FuelSystem;
import fr.dynamx.addon.basic.fuel.JerrycanItem;
import fr.dynamx.addon.basic.horn.HornPayload;
import fr.dynamx.addon.basic.horn.HornServerHandler;
import fr.dynamx.addon.basic.key.VehicleKeyItem;
import fr.dynamx.addon.basic.key.VehicleKeySystem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class BasicAddonMod implements ModInitializer {

    public static final String MOD_ID = "dynamx_basic_addon";

    /** Item clé de véhicule ({@code dynamx_basic_addon:vehicle_key}). */
    public static Item VEHICLE_KEY;

    /** Item jerrican d'essence ({@code dynamx_basic_addon:fuel_jerrycan}). */
    public static Item JERRYCAN;

    /** Identifiant de liaison stocké sur le stack de la clé ; absent tant que la clé est vierge. */
    public static DataComponentType<String> KEY_ID;

    /** Nom du type de véhicule lié, affiché dans le tooltip de la clé (parité addon 1.12). */
    public static DataComponentType<String> KEY_VEHICLE_NAME;

    /** Niveau de carburant du jerrican ; absent = vide. */
    public static DataComponentType<Float> JERRYCAN_FUEL;

    /** Son de klaxon embarqué (vehicle_horn_1.ogg de l'addon 1.12). */
    public static SoundEvent HORN_SOUND;

    @Override
    public void onInitialize() {
        registerItems();
        registerDataComponents();
        registerSounds();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> {
                    entries.accept(VEHICLE_KEY);
                    entries.accept(JERRYCAN);
                });

        // Ordre important : le jerrican d'abord (remplissage possible même verrouillé),
        // puis la clé (liaison/verrouillage + blocage de la montée).
        UseEntityCallback.EVENT.register(FuelSystem::onUseEntity);
        UseEntityCallback.EVENT.register(VehicleKeySystem::onUseEntity);

        PayloadTypeRegistry.playC2S().register(HornPayload.TYPE, HornPayload.STREAM_CODEC);
        HornServerHandler.register();

        ServerTickEvents.END_SERVER_TICK.register(FuelSystem::onServerTick);

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> BasicsCommands.register(dispatcher));
    }

    private void registerItems() {
        Identifier keyItemId = Identifier.fromNamespaceAndPath(MOD_ID, "vehicle_key");
        // MC 1.21.x : la ResourceKey doit être posée sur les Properties avant l'instanciation
        ResourceKey<Item> keyKey = ResourceKey.create(Registries.ITEM, keyItemId);
        VEHICLE_KEY = new VehicleKeyItem(new Item.Properties().setId(keyKey).stacksTo(1));
        Registry.register(BuiltInRegistries.ITEM, keyItemId, VEHICLE_KEY);

        Identifier jerrycanId = Identifier.fromNamespaceAndPath(MOD_ID, "fuel_jerrycan");
        ResourceKey<Item> jerrycanKey = ResourceKey.create(Registries.ITEM, jerrycanId);
        JERRYCAN = new JerrycanItem(new Item.Properties().setId(jerrycanKey).stacksTo(1));
        Registry.register(BuiltInRegistries.ITEM, jerrycanId, JERRYCAN);
    }

    private void registerDataComponents() {
        KEY_ID = DataComponentType.<String>builder()
                .persistent(Codec.STRING)
                .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                .build();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(MOD_ID, "key_id"), KEY_ID);

        KEY_VEHICLE_NAME = DataComponentType.<String>builder()
                .persistent(Codec.STRING)
                .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                .build();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(MOD_ID, "key_vehicle_name"), KEY_VEHICLE_NAME);

        JERRYCAN_FUEL = DataComponentType.<Float>builder()
                .persistent(Codec.FLOAT)
                .networkSynchronized(ByteBufCodecs.FLOAT)
                .build();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(MOD_ID, "jerrycan_fuel"), JERRYCAN_FUEL);
    }

    private void registerSounds() {
        Identifier hornId = Identifier.fromNamespaceAndPath(MOD_ID, "horn");
        HORN_SOUND = Registry.register(BuiltInRegistries.SOUND_EVENT, hornId,
                SoundEvent.createVariableRangeEvent(hornId));
    }
}
