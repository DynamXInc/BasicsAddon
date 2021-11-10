package fr.dynamx.addons.basics.client;

import com.jme3.math.Vector3f;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.UpdatableGuiLabel;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.LightHolder;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.utils.maths.DynamXGeometry;
import fr.dynamx.utils.optimization.Vector3fPool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.Collections;
import java.util.List;

public class BasicsAddonController implements IVehicleController {
    public static final ResourceLocation STYLE = new ResourceLocation(fr.dynamx.addons.basics.BasicsAddon.ID, "css/vehicle_hud_siren.css");

    private final BaseVehicleEntity<?> entity;
    private final BasicsAddonModule module;
    private final fr.dynamx.addons.basics.common.LightHolder lights;

    public BasicsAddonController(BaseVehicleEntity<?> entity, BasicsAddonModule module, LightHolder lights) {
        this.entity = entity;
        this.module = module;
        this.lights = lights;
    }

    @SideOnly(Side.CLIENT)
    public void updateSiren() {
        if (module.isSirenOn() && module.hasSiren()) {
            if (sirenSound == null || !Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(sirenSound)) {
                sirenSound = new StoppableEntitySound(BasicsAddon.soundMap.get(module.getInfos().sirenSound), SoundCategory.PLAYERS, entity);
                Minecraft.getMinecraft().getSoundHandler().playSound(sirenSound);
            }
            if (lights != null) {
                Vector3f pos = Vector3fPool.get(0.706687f, 3.28461f, 3.3705f);
                pos = DynamXGeometry.rotateVectorByQuaternion(pos, entity.physicsRotation);
                pos = pos.add(entity.physicsPosition);
                double angle = entity.ticksExisted % 15 * Math.PI * 2 / 15;
                Vector3f rot = Vector3fPool.get((float) (16 * Math.cos(angle)), -4, (float) (16 * Math.sin(angle)));
                rot = DynamXGeometry.rotateVectorByQuaternion(rot, entity.physicsRotation);
                lights.update1(pos, rot);

                pos = Vector3fPool.get(-0.706687f, 3.28461f, 3.3705f);
                pos = DynamXGeometry.rotateVectorByQuaternion(pos, entity.physicsRotation);
                pos = pos.add(entity.physicsPosition);
                angle = entity.ticksExisted % 15 * Math.PI * 2 / 15;
                rot = Vector3fPool.get((float) (16 * Math.cos(angle)), -4, (float) (16 * Math.sin(angle)));
                rot = DynamXGeometry.rotateVectorByQuaternion(rot, entity.physicsRotation);
                lights.update2(pos, rot);
            }
        } else if (sirenSound != null) {
            sirenSound.stop();
            sirenSound = null;
            if (lights != null)
                lights.destroy();
        }
    }

    @SideOnly(Side.CLIENT)
    private StoppableEntitySound sirenSound;
    @SideOnly(Side.CLIENT)
    public static final KeyBinding klaxon = new KeyBinding("Klaxon", Keyboard.KEY_K, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding siren = new KeyBinding("Siren", Keyboard.KEY_I, "DynamX basics");
    private byte klaxonHullDown;

    @Override
    @SideOnly(Side.CLIENT)
    public void update() {
        if (klaxonHullDown > 0)
            klaxonHullDown--;
        if (module.hasKlaxon()) {
            module.playKlaxon(klaxon.isPressed() && klaxonHullDown == 0);
            //System.out.println("Klaxon : "+b+" "+playKlaxon+" "+klaxonHullDown);
            if (module.playKlaxon())
                klaxonHullDown = module.getInfos().klaxonCooldown;
        }
        if (module.hasSiren()) {
            if (siren.isPressed())
                module.setSirenOn(!module.isSirenOn());
        }
    }

    @Override
    public GuiComponent<?> createHud() {
        if (module.hasSiren()) {
            GuiPanel cir = new GuiPanel();
            cir.setCssId("siren_hud");
            cir.setCssClass("hud_item");
            cir.add(new UpdatableGuiLabel("Siren : %s", (s) -> String.format(s, module.isSirenOn() ? "On" : "Off")));
            return cir;
        }
        return null;
    }

    @Override
    public List<ResourceLocation> getHudCssStyles() {
        if (module.hasSiren())
            return Collections.singletonList(STYLE);
        return null;
    }
}
