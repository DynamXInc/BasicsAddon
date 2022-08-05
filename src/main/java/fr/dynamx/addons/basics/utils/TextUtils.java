package fr.dynamx.addons.basics.utils;

import com.jme3.math.Vector3f;
import fr.aym.acsguis.cssengine.font.CssFontHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

public class TextUtils {
    public static void drawText(Vector3f pos, Vector3f scale, Vector3f rotation, String text, int[] color, String font){
        drawText(pos, scale, rotation, text, color, font, 0.0F);
    }

    public static void drawText(Vector3f pos, Vector3f scale, Vector3f rotation, String text, int[] color, String font, float spacing) {
        if(pos != null) {
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
            String[] lines = text.split("\\\\n");
            for (String line : lines) {
                String line2 = line.replace("\\n", "");
                CssFontHelper.draw((float) (-CssFontHelper.getBoundFont().getWidth(line2) / 2), 0, line2, (color[0] << 16) | (color[1] << 8) | color[2]);
                GlStateManager.translate(0, (spacing + 1) * 100, 0);
            }
            CssFontHelper.popDrawing();
            GlStateManager.enableLighting();
            GlStateManager.resetColor();
            GlStateManager.popMatrix();
        }
    }
}
