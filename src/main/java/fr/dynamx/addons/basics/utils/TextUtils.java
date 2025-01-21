package fr.dynamx.addons.basics.utils;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import fr.aym.acsguis.cssengine.font.CssFontHelper;
import fr.dynamx.utils.client.ClientDynamXUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.joml.Matrix4f;

import java.util.Collections;

public class TextUtils {
    public static void drawText(Matrix4f transform, Vector3f rotationXYZ, String text, int[] color, String font) {
        drawText(transform, rotationXYZ, text, color, font, 0.0F);
    }

    public static void drawText(Matrix4f transform, Vector3f rotationXYZ, String text, int[] color, String font, float spacing) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);
        transform.rotate(FastMath.PI, 1, 0, 0);
        transform.rotate(FastMath.PI, 0, 1, 0);
        float rotate = rotationXYZ.x;
        if (rotate != 0)
            transform.rotate(rotate * FastMath.PI / 180, 1, 0, 0);
        rotate = rotationXYZ.y;
        if (rotate != 0)
            transform.rotate(rotate * FastMath.PI / 180, 0, 1, 0);
        rotate = rotationXYZ.z;
        if (rotate != 0)
            transform.rotate(rotate * FastMath.PI / 180, 0, 0, 1);
        GlStateManager.multMatrix(ClientDynamXUtils.getMatrixBuffer(transform));
        CssFontHelper.pushDrawing(new ResourceLocation(font), Collections.emptyList());
        String[] lines = text.split("\\\\n");
        for (String line : lines) {
            String line2 = line.replace("\\n", "");
            CssFontHelper.draw((float) (-CssFontHelper.getBoundFont().getWidth(line2) / 2), 0, line2, (color[0] << 16) | (color[1] << 8) | color[2]);
            GlStateManager.translate(0, (spacing + 1) * 100, 0);
        }
        CssFontHelper.popDrawing();
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }
}
