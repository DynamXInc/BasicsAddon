package fr.dynamx.addons.basics.common.infos;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoType;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.contentpack.registry.RegisteredSubInfoType;
import fr.dynamx.api.contentpack.registry.SubInfoTypeRegistries;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.utils.RegistryNameSetter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nullable;

@RegisteredSubInfoType(
        name = "BasicsAddon",
        registries = {SubInfoTypeRegistries.WHEELED_VEHICLES, SubInfoTypeRegistries.HELICOPTER},
        strictName = false
)
public class BasicsAddonInfos implements ISubInfoType<ModularVehicleInfo> {
    private final ModularVehicleInfo owner;

    @PackFileProperty(configNames = {"HornSound", "KlaxonSound"}, required = false)
    public String klaxonSound;
    @PackFileProperty(configNames = {"HornCooldown", "KlaxonCooldown"}, required = false)
    public byte klaxonCooldown = 20;
    @PackFileProperty(configNames = "SirenSound", required = false)
    public String sirenSound;
    @PackFileProperty(configNames = "SirenDistance", required = false)
    public float sirenDistance = 50;
    @PackFileProperty(configNames = "SirenLightSource", required = false)
    public int sirenLightSource = 0;
    @PackFileProperty(configNames = "DRLightSource", required = false)
    public int drLightSource = 0;
    @PackFileProperty(configNames = "HeadLightsSource", required = false)
    public int headLightsSource = 0;
    @PackFileProperty(configNames = "BackLightsSource", required = false)
    public int backLightsSource = 0;
    @PackFileProperty(configNames = "BrakeLightsSource", required = false)
    public int brakeLightsSource = 0;
    @PackFileProperty(configNames = "ReverseLightsSource", required = false)
    public int reverseLightsSource = 0;
    @PackFileProperty(configNames = "TurnSignalLeftLightSource", required = false)
    public int turnLeftLightSource = 0;
    @PackFileProperty(configNames = "TurnSignalRightLightSource", required = false)
    public int turnRightLightSource = 0;

    @PackFileProperty(configNames = "IndicatorsSound", required = false)
    public String indicatorsSound;
    @PackFileProperty(configNames = "LockSoundOutdoor", required = false)
    public String lockSoundOutdoor;
    @PackFileProperty(configNames = "LockSoundIndoor", required = false)
    public String lockSoundIndoor;

    public BasicsAddonInfos(ISubInfoTypeOwner<ModularVehicleInfo> owner) {
        this.owner = (ModularVehicleInfo) owner;
    }

    @Override
    public void appendTo(ModularVehicleInfo owner) {
        if (klaxonSound != null) {
            ResourceLocation r = new ResourceLocation(klaxonSound);
            SoundEvent event = new SoundEvent(r);
            RegistryNameSetter.setRegistryName(event, klaxonSound);
            BasicsAddon.soundMap.put(klaxonSound, event);
        }
        if (sirenSound != null) {
            ResourceLocation r = new ResourceLocation(sirenSound);
            SoundEvent event = new SoundEvent(r);
            RegistryNameSetter.setRegistryName(event, sirenSound);
            BasicsAddon.soundMap.put(sirenSound, event);
        }
        if (lockSoundOutdoor != null) {
            ResourceLocation r = new ResourceLocation(lockSoundOutdoor);
            SoundEvent event = new SoundEvent(r);
            RegistryNameSetter.setRegistryName(event, lockSoundOutdoor);
            BasicsAddon.soundMap.put(lockSoundOutdoor, event);
        }
        owner.addSubProperty(this);
    }

    @Nullable
    @Override
    public ModularVehicleInfo getOwner() {
        return owner;
    }

    @Override
    public String getName() {
        return "BasicsAddonInfos of " + owner.getName();
    }

    @Override
    public String getPackName() {
        return owner.getPackName();
    }
}
