package fr.dynamx.addons.basics.client;

import fr.aym.acsguis.event.CssReloadEvent;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.api.entities.VehicleEntityProperties;
import fr.dynamx.api.events.PhysicsEntityEvent;
import fr.dynamx.api.events.VehicleEntityEvent;
import fr.dynamx.common.entities.modules.EngineModule;
import fr.dynamx.common.entities.modules.VehicleLightsModule;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = BasicsAddon.ID, value = Side.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void renderLights(VehicleEntityEvent.RenderVehicleEntityEvent event) {
        if (event.phase == PhysicsEntityEvent.Phase.PRE && event.type == VehicleEntityEvent.RenderVehicleEntityEvent.Type.LIGHTS) {
            BasicsAddonModule module = event.getEntity().getModuleByType(BasicsAddonModule.class);
            if (module != null) {
                VehicleLightsModule lights = event.getEntity().getModuleByType(VehicleLightsModule.class);
                if (lights != null && module.getInfos() != null) {
                    lights.setLightOn(module.getInfos().sirenLightSource, module.isSirenOn());

                    if(module.hasHeadLights()) {
                        if(module.isHeadLightsOn()) {
                            lights.setLightOn(module.getInfos().headLightsSource, true);
                            lights.setLightOn(module.getInfos().backLightsSource, true);
                        } else {
                            lights.setLightOn(module.getInfos().headLightsSource, false);
                            lights.setLightOn(module.getInfos().backLightsSource, false);
                        }
                    }

                    if(module.hasTurnSignals()) {
                        lights.setLightOn(module.getInfos().turnLeftLightSource, module.isTurnSignalLeftOn());
                        lights.setLightOn(module.getInfos().turnRightLightSource, module.isTurnSignalRightOn());
                    }

                    EngineModule engine = event.getEntity().getModuleByType(EngineModule.class);
                    if(engine != null) {
                        if(engine.isReversing()) {
                            if (engine.getEngineProperty(VehicleEntityProperties.EnumEngineProperties.ACTIVE_GEAR) == -1) {
                                lights.setLightOn(module.getInfos().reverseLightsSource, true);
                                lights.setLightOn(module.getInfos().brakeLightsSource, false);
                            } else {
                                lights.setLightOn(module.getInfos().reverseLightsSource, false);
                                lights.setLightOn(module.getInfos().brakeLightsSource, true);
                            }
                        } else if(engine.isAccelerating()) {
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
}
