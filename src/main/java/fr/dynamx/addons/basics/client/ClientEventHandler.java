package fr.dynamx.addons.basics.client;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.infos.ImmatriculationPlateInfos;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.addons.basics.common.modules.ImmatriculationPlateModule;
import fr.dynamx.api.events.PhysicsEntityEvent;
import fr.dynamx.api.events.VehicleEntityEvent;
import fr.dynamx.common.entities.modules.EngineModule;
import fr.dynamx.common.entities.modules.VehicleLightsModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.awt.*;

@Mod.EventBusSubscriber(modid = BasicsAddon.ID, value = Side.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void renderLights(VehicleEntityEvent.RenderVehicleEntityEvent event) {
        if (event.phase == PhysicsEntityEvent.Phase.PRE && event.type == VehicleEntityEvent.RenderVehicleEntityEvent.Type.LIGHTS) {
            BasicsAddonModule module = event.getEntity().getModuleByType(BasicsAddonModule.class);
            if (module != null) {
                VehicleLightsModule lights = event.getEntity().getModuleByType(VehicleLightsModule.class);
                if (lights != null) {
                    if (module.isSirenOn()) {
                        lights.setLightOn(module.getInfos().sirenLightSource, event.getEntity().ticksExisted % 20 >= 10);
                    } else {
                        lights.setLightOn(module.getInfos().sirenLightSource, false);
                    }
                    lights.setLightOn(1, event.getEntity().getModuleByType(EngineModule.class).isReversing());
                }
            }
        }
    }

    @SubscribeEvent
    public static void immatriculationPlate(VehicleEntityEvent.RenderVehicleEntityEvent e) {
        ImmatriculationPlateModule module = e.carEntity.getModuleByType(ImmatriculationPlateModule.class);
        if (module != null) {
            for (ImmatriculationPlateInfos immatriculationPlateInfos : module.getInfo()) {

                Vector3f platePos = immatriculationPlateInfos.getImmatriculationPosition();
                Vector3f plateSize = immatriculationPlateInfos.getImmatriculationSize();
                Vector3f plateRotation = immatriculationPlateInfos.getImmatriculationRotation();

                GlStateManager.pushMatrix();

                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.translate(platePos.x, platePos.y, platePos.z);
                GlStateManager.rotate(180, 1, 0, 0);
                GlStateManager.rotate(180, 0, 1, 0);
                float rotate = plateRotation.x;
                if (rotate != 0)
                    GlStateManager.rotate(rotate, 1, 0, 0);
                rotate = plateRotation.y;
                if (rotate != 0)
                    GlStateManager.rotate(rotate, 0, 1, 0);
                rotate = plateRotation.z;
                if (rotate != 0)
                    GlStateManager.rotate(rotate, 0, 0, 1);
                GlStateManager.scale(plateSize.x / 40, plateSize.y / 40, plateSize.z / 40);
                RenderHelper.disableStandardItemLighting();
                drawCenteredString(module.getPlate(), 0, 0, Color.white.getRGB());
                RenderHelper.enableStandardItemLighting();
                GlStateManager.resetColor();

                GlStateManager.popMatrix();
            }
        }
    }

    public static void drawCenteredString(String text, int x, int y, int color) {
        Minecraft.getMinecraft().fontRenderer.drawString(text, (float) (x - Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2), (float) y, color, false);
    }
}
