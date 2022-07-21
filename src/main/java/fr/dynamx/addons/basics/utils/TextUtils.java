package fr.dynamx.addons.basics.utils;

import com.jme3.math.Vector3f;
import fr.aym.acsguis.cssengine.font.CssFontHelper;
import fr.dynamx.api.entities.VehicleEntityProperties;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.modules.EngineModule;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

public class TextUtils {
    public static void drawText(Vector3f pos, Vector3f scale, Vector3f rotation, String text, int[] color, String font) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.translate(pos.x, pos.y, pos.z);
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.rotate(180, 0, 1, 0);
        float rotate = rotation.x;
        if (rotate != 0)
            GlStateManager.rotate(rotate, 1, 0, 0);
        rotate = rotation.y;
        if (rotate != 0)
            GlStateManager.rotate(rotate, 0, 1, 0);
        rotate = rotation.z;
        if (rotate != 0)
            GlStateManager.rotate(rotate, 0, 0, 1);
        GlStateManager.scale(scale.x / 40, scale.y / 40, scale.z / 40);
        GlStateManager.disableLighting();

        CssFontHelper.pushDrawing(new ResourceLocation(font), Collections.emptyList());
        GlStateManager.scale(0.05, 0.05, 0.05);
        CssFontHelper.draw((float) (-CssFontHelper.getBoundFont().getWidth(text) / 2), 0, text, (color[0] << 16) | (color[1] << 8) | color[2]);
        CssFontHelper.popDrawing();
        GlStateManager.enableLighting();
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }
}
