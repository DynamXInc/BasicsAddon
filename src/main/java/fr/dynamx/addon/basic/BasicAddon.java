package fr.dynamx.addon.basic;

import fr.dynamx.addon.basic.fuel.FuelComponent;
import fr.dynamx.addon.basic.fuel.FuelTankConfig;
import fr.dynamx.addon.basic.key.VehicleLockComponent;
import fr.dynamx.addon.basic.plate.PlateComponent;
import fr.dynamx.addon.basic.plate.PlateConfig;
import fr.dynamx.api.addon.DynamXAddon;
import fr.dynamx.api.addon.DynamXApiContext;
import fr.dynamx.api.ecs.ComponentNetworkSync;
import fr.dynamx.api.ecs.ComponentPersistence;
import fr.dynamx.api.pack.PackType;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.EnumSet;

public final class BasicAddon implements DynamXAddon {

    private static final Logger LOGGER = LoggerFactory.getLogger("DynamXBasicAddon");

    /**
     * Contexte API stashé au pre-init (pattern documenté par DynamX) pour les usages runtime :
     * {@code withEntity} depuis les handlers d'interaction Minecraft.
     */
    private static volatile DynamXApiContext apiContext;

    /** Le contexte DynamX, ou {@code null} tant que le pre-init de l'addon n'a pas eu lieu. */
    public static DynamXApiContext apiContext() {
        return apiContext;
    }

    @Override
    public String id() {
        return "dynamx-basic-addon";
    }

    @Override
    public String version() {
        return "0.1.0";
    }

    @Override
    public String dynamXVersionRange() {
        return ">=0.1.0 <1.0.0";
    }

    @Override
    public void onPreInit(DynamXApiContext context) {
        LOGGER.info("BasicAddon pre-init (DynamX {})", context.dynamXVersion());
        apiContext = context;

        // Serrure de véhicule : persistée en NBT avec le véhicule (PhantomSaveCodec sauvegarde
        // automatiquement les composants d'addon enregistrés avec une persistance). Pas de
        // network sync : le verrouillage est entièrement décidé côté serveur.
        context.registerComponent(VehicleLockComponent.class,
                new ComponentPersistence<>() {
                    @Override
                    public CompoundTag save(VehicleLockComponent component) {
                        CompoundTag tag = new CompoundTag();
                        tag.putString("key_id", component.keyId());
                        tag.putBoolean("locked", component.locked());
                        return tag;
                    }

                    @Override
                    public VehicleLockComponent load(CompoundTag tag) {
                        return new VehicleLockComponent(
                                tag.getStringOr("key_id", "missing"),
                                tag.getBooleanOr("locked", false));
                    }
                },
                null);

        // Réservoir : persisté avec le véhicule ; champ YAML fuel_tank (TankSize/FuelConsumption 1.12).
        // Sync réseau : fuel + tankSize (8 octets) vers le mirror client pour la jauge HUD ;
        // la consommation reste serveur (préservée par merge quand le mirror en a déjà une).
        context.registerComponent(FuelComponent.class,
                new ComponentPersistence<>() {
                    @Override
                    public CompoundTag save(FuelComponent component) {
                        CompoundTag tag = new CompoundTag();
                        tag.putFloat("fuel", component.fuel());
                        tag.putFloat("tank_size", component.tankSize());
                        tag.putFloat("consumption", component.consumptionPerSecond());
                        return tag;
                    }

                    @Override
                    public FuelComponent load(CompoundTag tag) {
                        return new FuelComponent(
                                tag.getFloatOr("fuel", 0f),
                                tag.getFloatOr("tank_size", 100f),
                                tag.getFloatOr("consumption", 1f));
                    }
                },
                new ComponentNetworkSync<>() {
                    @Override
                    public void encode(FuelComponent component, ByteBuffer buffer) {
                        buffer.putFloat(component.fuel());
                        buffer.putFloat(component.tankSize());
                    }

                    @Override
                    public FuelComponent decode(ByteBuffer buffer) {
                        return new FuelComponent(buffer.getFloat(), buffer.getFloat(), 0f);
                    }

                    @Override
                    public FuelComponent merge(@Nullable FuelComponent existing, FuelComponent decoded) {
                        // La consommation n'est pas sur le fil : la préserver depuis le mirror.
                        return existing != null
                                ? new FuelComponent(decoded.fuel(), decoded.tankSize(), existing.consumptionPerSecond())
                                : decoded;
                    }

                    @Override
                    public int maxBytes() {
                        return 2 * Float.BYTES;
                    }
                });
        context.registerYamlField("fuel_tank", EnumSet.of(PackType.VEHICLE), FuelTankConfig.class,
                (entity, cfg) -> entity.set(FuelComponent.full(cfg.tankSizeOrDefault(), cfg.consumptionOrDefault())));

        // Plaque : générée au spawn depuis le pattern (Pattern 1.12), persistée ensuite —
        // à la restauration d'une sauvegarde, la plaque sauvegardée écrase celle regénérée ici.
        context.registerComponent(PlateComponent.class,
                new ComponentPersistence<>() {
                    @Override
                    public CompoundTag save(PlateComponent component) {
                        CompoundTag tag = new CompoundTag();
                        tag.putString("plate", component.plate());
                        return tag;
                    }

                    @Override
                    public PlateComponent load(CompoundTag tag) {
                        return new PlateComponent(tag.getStringOr("plate", "??-???-??"));
                    }
                },
                null);
        context.registerYamlField("license_plate", EnumSet.of(PackType.VEHICLE), PlateConfig.class,
                (entity, cfg) -> entity.set(new PlateComponent(PlateComponent.generate(cfg.patternOrDefault()))));
    }

    @Override
    public void onPostInit(DynamXApiContext context) {
        LOGGER.info("BasicAddon post-init, addons découverts : {}", context.addons().keySet());
    }
}
