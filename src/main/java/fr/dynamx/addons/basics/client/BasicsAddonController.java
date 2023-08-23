package fr.dynamx.addons.basics.client;


import fr.aym.acsguis.component.GuiComponent;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.api.audio.EnumSoundState;
import fr.dynamx.api.audio.IDynamXSound;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.client.ClientProxy;
import fr.dynamx.client.handlers.ClientEventHandler;
import fr.dynamx.client.sound.VehicleSound;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.modules.CarEngineModule;
import fr.dynamx.utils.optimization.Vector3fPool;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.Collections;
import java.util.List;

public class BasicsAddonController implements IVehicleController {
    public static final ResourceLocation STYLE = new ResourceLocation(fr.dynamx.addons.basics.BasicsAddon.ID, "css/vehicle_hud.css");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding hornKey = new KeyBinding("Horn", Keyboard.KEY_K, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding beaconKey = new KeyBinding("Beacons", Keyboard.KEY_P, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding sirenKey = new KeyBinding("Siren", Keyboard.KEY_I, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding headlights = new KeyBinding("HeadLights", Keyboard.KEY_U, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding turnLeft = new KeyBinding("TurnLeft", Keyboard.KEY_LEFT, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding turnRight = new KeyBinding("TurnRight", Keyboard.KEY_RIGHT, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding warnings = new KeyBinding("Warnings", Keyboard.KEY_DOWN, "DynamX basics");

    public static IDynamXSound indicatorsSound;

    private final BaseVehicleEntity<?> entity;
    private final BasicsAddonModule module;
    //private final fr.dynamx.addons.basics.common.LightHolder lights;

    @SideOnly(Side.CLIENT)
    private SirenSound sirenSound;
    private byte klaxonHullDown;
    private boolean warningsOn;

    public BasicsAddonController(BaseVehicleEntity<?> entity, BasicsAddonModule module) {
        this.entity = entity;
        this.module = module;
        this.sirenSound = new SirenSound(entity, module);
        //this.lights = lights;

        unpress(hornKey);
        unpress(beaconKey);
        unpress(sirenKey);
        unpress(headlights);
        unpress(turnLeft);
        unpress(turnRight);
        unpress(warnings);
    }

    private void unpress(KeyBinding key) {
        while (key.isPressed()) {
        }
    }

    @SideOnly(Side.CLIENT)
    public void updateSiren() {
        if (module.isSirenOn() && module.hasSirenSound()) {
            if (!ClientProxy.SOUND_HANDLER.getPlayingSounds().contains(sirenSound)) {
                ClientProxy.SOUND_HANDLER.playStreamingSound(Vector3fPool.get(entity.posX, entity.posY, entity.posZ), sirenSound);
                ClientProxy.SOUND_HANDLER.setSoundDistance(sirenSound, 100);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void update() {
        if (klaxonHullDown > 0)
            klaxonHullDown--;
        if (module.hasKlaxon()) {
            module.playKlaxon(hornKey.isPressed() && klaxonHullDown == 0);
            if (module.playKlaxon()) {
                klaxonHullDown = module.getInfos().klaxonCooldown;
            }
        }
        if (module.hasSiren()) {
            if (sirenKey.isPressed())
                module.setSirenOn(!module.isSirenOn());
            if (beaconKey.isPressed())
                module.setBeaconsOn(!module.isBeaconsOn());
        }
        if (module.hasHeadLights()) {
            if (headlights.isPressed())
                module.setHeadLightsOn(!module.isHeadLightsOn());
        }
        if (module.hasTurnSignals()) {
            if (turnLeft.isPressed()) {
                System.out.println(module.isTurnSignalLeftOn() + " wtf " + module.isTurnSignalRightOn());
                if (!warningsOn)
                    module.setTurnSignalLeftOn(!module.isTurnSignalLeftOn());
                else
                    warningsOn = false;
                module.setTurnSignalRightOn(false);
            } else if (turnRight.isPressed()) {
                module.setTurnSignalLeftOn(false);
                if (!warningsOn)
                    module.setTurnSignalRightOn(!module.isTurnSignalRightOn());
                else
                    warningsOn = false;
            } else if (warnings.isPressed()) {
                warningsOn = !warningsOn;
                module.setTurnSignalLeftOn(warningsOn);
                module.setTurnSignalRightOn(warningsOn);
            }
            String sound = module.getInfos().indicatorsSound;
            if (!StringUtils.isNullOrEmpty(sound) && (module.isTurnSignalLeftOn() || module.isTurnSignalRightOn()) && indicatorsSound == null) {
                indicatorsSound = new VehicleSound(sound, entity) {
                    @Override
                    public boolean isSoundActive() {
                        return ClientEventHandler.MC.player.getRidingEntity() == entity && (module.isTurnSignalLeftOn() || module.isTurnSignalRightOn());
                    }

                    @Override
                    public void setState(EnumSoundState state) {
                        super.setState(state);
                        if (state == EnumSoundState.STOPPING)
                            ClientProxy.SOUND_HANDLER.stopSound(this);
                    }

                    @Override
                    public boolean tryStop() {
                        indicatorsSound = null;
                        return true;
                    }
                };
                ClientProxy.SOUND_HANDLER.playStreamingSound(Vector3fPool.get(entity.posX, entity.posY, entity.posZ), indicatorsSound);
            }
        }
        if (module.hasDRL()) {
            module.setDRLOn(entity.getModuleByType(CarEngineModule.class).isEngineStarted());
        }
    }

    @Override
    public GuiComponent<?> createHud() {
        return null;
    }

    @Override
    public List<ResourceLocation> getHudCssStyles() {
        return Collections.EMPTY_LIST;
    }
}
