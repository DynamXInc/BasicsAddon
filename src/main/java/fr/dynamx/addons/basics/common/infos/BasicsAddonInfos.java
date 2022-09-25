package fr.dynamx.addons.basics.common.infos;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.api.contentpack.object.INamedObject;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoType;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.entities.modules.ModuleListBuilder;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfoBuilder;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.utils.RegistryNameSetter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nullable;

public class BasicsAddonInfos implements ISubInfoType<ModularVehicleInfoBuilder> {
    private final ModularVehicleInfoBuilder owner;

    @PackFileProperty(configNames = {"HornSound", "KlaxonSound"}, required = false)
    public String klaxonSound;
    @PackFileProperty(configNames = {"HornCooldown", "KlaxonCooldown"}, required = false)
    public byte klaxonCooldown = 20;
    @PackFileProperty(configNames = "SirenSound", required = false)
    public String sirenSound;
    @PackFileProperty(configNames = "SirenLightSource", required = false)
    public int sirenLightSource = 0;
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

    public BasicsAddonInfos(ISubInfoTypeOwner<ModularVehicleInfoBuilder> owner) {
        this.owner = (ModularVehicleInfoBuilder) owner;
    }

    @Override
    public void appendTo(ModularVehicleInfoBuilder owner) {
        if (klaxonSound != null) {
           // if (!klaxonSound.contains(":"))
              //  klaxonSound = fr.dynamx.addons.basics.BasicsAddon.ID + ":" + klaxonSound;
            ResourceLocation r = new ResourceLocation(klaxonSound);
            SoundEvent event = new SoundEvent(r);
            RegistryNameSetter.setRegistryName(event, klaxonSound);
            BasicsAddon.soundMap.put(klaxonSound, event);
        }
        if (sirenSound != null) {
            //if (!sirenSound.contains(":"))
            //    sirenSound = fr.dynamx.addons.basics.BasicsAddon.ID + ":" + sirenSound;
            ResourceLocation r = new ResourceLocation(sirenSound);
            SoundEvent event = new SoundEvent(r);
            RegistryNameSetter.setRegistryName(event, sirenSound);
            BasicsAddon.soundMap.put(sirenSound, event);
        }
        owner.addSubProperty(this);
    }

    @Nullable
    @Override
    public ModularVehicleInfoBuilder getOwner() {
        return owner;
    }

    @Override
    public void addModules(BaseVehicleEntity<?> entity, ModuleListBuilder modules) {
        ISubInfoType.super.addModules(entity, modules);
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
