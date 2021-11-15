package fr.dynamx.addons.basics.common.modules;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.common.infos.ImmatriculationPlateInfos;
import fr.dynamx.addons.basics.common.network.ImmatriculationPlateSynchronizedVariable;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.network.sync.SimulationHolder;
import fr.dynamx.client.renders.RenderPhysicsEntity;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.RandomStringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ImmatriculationPlateModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IDrawableModule<BaseVehicleEntity<?>> {

    private final List<ImmatriculationPlateInfos> info = new ArrayList<>();
    private String plate = "";

    public ImmatriculationPlateModule(ImmatriculationPlateInfos info) {
        this.info.add(info);
        // Thanks to Kerlan
        String firstNumber = RandomStringUtils.random(2, 97, 122, true, false);
        String secondNumber = RandomStringUtils.randomNumeric(3);
        String thirdNumber = RandomStringUtils.random(2, 97, 122, true, false);
        this.plate = firstNumber.toUpperCase() + "-" + secondNumber + "-" + thirdNumber.toUpperCase();
    }

    public List<ImmatriculationPlateInfos> getInfo() {
        return info;
    }

    public void addInformation(ImmatriculationPlateInfos info) {
        this.info.add(info);
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    @Override
    public void addSynchronizedVariables(Side side, SimulationHolder simulationHolder, List variables) {
        if (side.isServer()) {
            variables.add(ImmatriculationPlateSynchronizedVariable.NAME);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawParts(RenderPhysicsEntity<?> render, float partialTicks, BaseVehicleEntity<?> entity) {
        for (ImmatriculationPlateInfos immatriculationPlateInfos : getInfo()) {

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
            Minecraft.getMinecraft().fontRenderer.drawString(getPlate(), (float) (- Minecraft.getMinecraft().fontRenderer.getStringWidth(getPlate()) / 2), 0, 0xFFFFFF, false);
            RenderHelper.enableStandardItemLighting();
            GlStateManager.resetColor();

            GlStateManager.popMatrix();
        }
    }

    @SideOnly(Side.CLIENT)
    public static void drawCenteredString(String text, int x, int y, int color) {
    }
}