package fr.dynamx.addons.basics.client;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.api.events.PhysicsEntityEvent;
import fr.dynamx.api.events.VehicleEntityEvent;
import fr.dynamx.common.entities.modules.EngineModule;
import fr.dynamx.common.entities.modules.VehicleLightsModule;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = BasicsAddon.ID, value = Side.CLIENT)
public class ClientEventHandler
{
    @SubscribeEvent
    public static void renderLights(VehicleEntityEvent.RenderVehicleEntityEvent event) {
        if(event.phase == PhysicsEntityEvent.Phase.PRE && event.type == VehicleEntityEvent.RenderVehicleEntityEvent.Type.LIGHTS)
        {
            BasicsAddonModule module = event.getEntity().getModuleByType(BasicsAddonModule.class);
            if(module != null) {
                VehicleLightsModule lights = event.getEntity().getModuleByType(VehicleLightsModule.class);
                if(lights != null) {
                    if(module.isSirenOn()) {
                        lights.setLightOn(module.getInfos().sirenLightSource, event.getEntity().ticksExisted % 20 >= 10);
                    }
                    else {
                        lights.setLightOn(module.getInfos().sirenLightSource, false);
                    }
                    lights.setLightOn(1, event.getEntity().getModuleByType(EngineModule.class).isReversing());
                }
            }
        }
    }
}
