package fr.dynamx.addons.basics.client;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.addons.basics.common.modules.FuelTankModule;
import fr.dynamx.api.entities.VehicleEntityProperties;
import fr.dynamx.api.events.DynamXEntityRenderEvents;
import fr.dynamx.api.events.VehicleEntityEvent;
import fr.dynamx.client.handlers.hud.CarController;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.modules.AbstractLightsModule;
import fr.dynamx.common.entities.modules.engines.BasicEngineModule;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = BasicsAddon.ID, value = Side.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void createHud(VehicleEntityEvent.CreateHud event) {
        if (event.getEntity().getModuleByType(BasicsAddonModule.class) != null && event.getEntity() instanceof BaseVehicleEntity) {
            CarController.setHudIcons(new BasicsAddonHudIcons(event.getEntity().getModuleByType(BasicsAddonModule.class), (BaseVehicleEntity<?>) event.getEntity()));
        }
    }

    @SubscribeEvent
    public static void renderLights(DynamXEntityRenderEvents.Render event) {
        if (event.getRenderType() == DynamXEntityRenderEvents.Render.Type.ENTITY && event.getEntity() != null) {
            BasicsAddonModule module = event.getEntity().getModuleByType(BasicsAddonModule.class);
            if (module != null) {
                AbstractLightsModule lights = event.getEntity().getModuleByType(AbstractLightsModule.class);
                if (lights != null && module.getInfos() != null) {
                    lights.setLightOn(module.getInfos().sirenLightSource, module.isBeaconsOn() || module.isSirenOn());

                    if (module.hasHeadLights()) {
                        if (module.isHeadLightsOn()) {
                            lights.setLightOn(module.getInfos().headLightsSource, true);
                            lights.setLightOn(module.getInfos().backLightsSource, true);
                        } else {
                            lights.setLightOn(module.getInfos().headLightsSource, false);
                            lights.setLightOn(module.getInfos().backLightsSource, false);
                        }
                    }

                    if (module.hasDRL()) {
                        if (module.isDRLOn()) {
                            lights.setLightOn(module.getInfos().drLightSource, true);
                        } else {
                            lights.setLightOn(module.getInfos().drLightSource, false);
                        }
                    }

                    if (module.hasTurnSignals()) {
                        lights.setLightOn(module.getInfos().turnLeftLightSource, module.isTurnSignalLeftOn());
                        lights.setLightOn(module.getInfos().turnRightLightSource, module.isTurnSignalRightOn());
                    }

                    BasicEngineModule engine = event.getEntity().getModuleByType(BasicEngineModule.class);
                    if (engine != null) {
                        if (engine.isReversing()) {
                            if (engine.getEngineProperty(VehicleEntityProperties.EnumEngineProperties.ACTIVE_GEAR) == -1) {
                                lights.setLightOn(module.getInfos().reverseLightsSource, true);
                                lights.setLightOn(module.getInfos().brakeLightsSource, false);
                            } else {
                                lights.setLightOn(module.getInfos().reverseLightsSource, false);
                                lights.setLightOn(module.getInfos().brakeLightsSource, true);
                            }
                        } else if (engine.isAccelerating()) {
                            if (engine.getEngineProperty(VehicleEntityProperties.EnumEngineProperties.ACTIVE_GEAR) == -1) {
                                lights.setLightOn(module.getInfos().brakeLightsSource, true);
                                lights.setLightOn(module.getInfos().reverseLightsSource, false);
                            } else {
                                lights.setLightOn(module.getInfos().reverseLightsSource, false);
                                lights.setLightOn(module.getInfos().brakeLightsSource, false);
                            }
                        } else {
                            lights.setLightOn(module.getInfos().reverseLightsSource, false);
                            lights.setLightOn(module.getInfos().brakeLightsSource, false);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void updateVehicleController(VehicleEntityEvent.ControllerUpdate event) {
        if (event.getController() instanceof CarController) {
            FuelTankModule module = event.getEntity().getModuleByType(FuelTankModule.class);
            if (module != null && module.getFuel() == 0) {
                ((CarController) event.getController()).setEngineStarted(false);
            }
        }
    }
}
