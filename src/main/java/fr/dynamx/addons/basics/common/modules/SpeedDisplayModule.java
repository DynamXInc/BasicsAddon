package fr.dynamx.addons.basics.common.modules;

import com.jme3.math.Vector3f;
import fr.aym.acsguis.cssengine.font.CssFontHelper;
import fr.dynamx.addons.basics.common.infos.SpeedDisplayInfos;
import fr.dynamx.addons.basics.utils.TextUtils;
import fr.dynamx.addons.basics.utils.VehicleUtils;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.client.renders.RenderPhysicsEntity;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpeedDisplayModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IDrawableModule<BaseVehicleEntity<?>> {

    private final List<SpeedDisplayInfos> info = new ArrayList<>();

    public SpeedDisplayModule(SpeedDisplayInfos info) {
        this.info.add(info);
    }

    public List<SpeedDisplayInfos> getInfo() {
        return info;
    }

    public void addInformation(SpeedDisplayInfos info) {
        this.info.add(info);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawParts(RenderPhysicsEntity<?> render, float partialTicks, BaseVehicleEntity<?> entity) {
        String speed = "" + VehicleUtils.getSpeed(entity);
        for (SpeedDisplayInfos info : getInfo()) {
            TextUtils.drawText(info.getPosition(), info.getSize(), info.getRotation(), speed, info.getColor(), info.getFont());
        }
    }
}