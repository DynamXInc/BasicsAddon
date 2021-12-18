package fr.dynamx.addons.basics.common.infos;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoType;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.common.contentpack.loader.ModularVehicleInfoBuilder;
import fr.dynamx.utils.RegistryNameSetter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class BasicsAddonInfos implements ISubInfoType<ModularVehicleInfoBuilder> {
    private final ISubInfoTypeOwner<ModularVehicleInfoBuilder> owner;

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
        this.owner = owner;
    }

    @Override
    public void appendTo(ModularVehicleInfoBuilder owner) {
        if (klaxonSound != null) {
           // if (!klaxonSound.contains(":"))
              //  klaxonSound = fr.dynamx.addons.basics.BasicsAddon.ID + ":" + klaxonSound;
            ResourceLocation r = new ResourceLocation(klaxonSound);
            SoundEvent event = new SoundEvent(r);
            RegistryNameSetter.setRegistryName(event, klaxonSound);
            fr.dynamx.addons.basics.BasicsAddon.soundMap.put(klaxonSound, event);
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

    @Override
    public String getName() {
        return "BasicsAddonInfos of " + owner.getName();
    }

    @Override
    public String getPackName() {
        return owner.getPackName();
    }
}
