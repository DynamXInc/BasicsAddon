package fr.dynamx.addons.basics.client;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.UpdatableGuiLabel;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.LightHolder;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.common.entities.BaseVehicleEntity;
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
    public static final ResourceLocation STYLE = new ResourceLocation(fr.dynamx.addons.basics.BasicsAddon.ID, "css/vehicle_hud.css");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding hornKey = new KeyBinding("Horn", Keyboard.KEY_K, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding beaconKey = new KeyBinding("Beacons", Keyboard.KEY_I, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding sirenKey = new KeyBinding("Siren", Keyboard.KEY_O, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding headlights = new KeyBinding("HeadLights", Keyboard.KEY_U, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding turnLeft = new KeyBinding("TurnLeft", Keyboard.KEY_LEFT, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding turnRight = new KeyBinding("TurnRight", Keyboard.KEY_RIGHT, "DynamX basics");
    @SideOnly(Side.CLIENT)
    public static final KeyBinding warnings = new KeyBinding("Warnings", Keyboard.KEY_DOWN, "DynamX basics");

    private final BaseVehicleEntity<?> entity;
    private final BasicsAddonModule module;
    //private final fr.dynamx.addons.basics.common.LightHolder lights;

    @SideOnly(Side.CLIENT)
    private StoppableEntitySound sirenSound;
    private byte klaxonHullDown;
    private boolean warningsOn;

    public BasicsAddonController(BaseVehicleEntity<?> entity, BasicsAddonModule module, LightHolder lights) {
        this.entity = entity;
        this.module = module;
        //this.lights = lights;

        unpress(hornKey);
        unpress(beaconKey);
        unpress(sirenKey);
        unpress(headlights);
        unpress(turnLeft);
        unpress(turnRight);
        unpress(warnings);
    }

    private void unpress(KeyBinding key) {
        while (key.isPressed()) {}
    }

    @SideOnly(Side.CLIENT)
    public void updateSiren() {
       /* VehicleLightsModule module1 = entity.getModuleByType(VehicleLightsModule.class);
        if(module1 != null) {
            if (((EngineModule) ((IModuleContainer.IEngineContainer) entity).getEngine()).isReversing()) {
                module1.setLightOn(1, true);
            } else {
                module1.setLightOn(1, false);
            }
        }*/
        if (module.isSirenOn() && module.hasSirenSound()) {
            if (sirenSound == null || !Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(sirenSound)) {
                sirenSound = new StoppableEntitySound(BasicsAddon.soundMap.get(module.getInfos().sirenSound), SoundCategory.PLAYERS, entity);
                Minecraft.getMinecraft().getSoundHandler().playSound(sirenSound);
            }
            /*if (lights != null) {

         /*   VehicleLightsModule module = entity.getModuleByType(VehicleLightsModule.class);
            if(module != null) {
                module.setLightOn(9, entity.ticksExisted%20 >= 10);
            }*/

           /* if (lights != null) {
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
            }*/
        } else if (sirenSound != null) {
            sirenSound.stop();
            sirenSound = null;
            //if (lights != null)
            //    lights.destroy();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void update() {
        if (klaxonHullDown > 0)
            klaxonHullDown--;
        if (module.hasKlaxon()) {
            module.playKlaxon(hornKey.isPressed() && klaxonHullDown == 0);
            //System.out.println("Klaxon : "+b+" "+playKlaxon+" "+klaxonHullDown);
            if (module.playKlaxon()) {
                klaxonHullDown = module.getInfos().klaxonCooldown;
            }
        }
        if (module.hasSiren()) {
            if (module.hasSirenSound() && sirenKey.isPressed()) {
                module.setSirenOn(!module.isSirenOn());
            }
            if (beaconKey.isPressed()) {
                module.setBeaconsOn(!module.isBeaconsOn());
            }
        }
        if (module.hasHeadLights()) {
            if (headlights.isPressed()) {
                module.setHeadLightsOn(!module.isHeadLightsOn());
            }
        }
        if (module.hasTurnSignals()) {
            if (turnLeft.isPressed()) {
                if (!warningsOn) {
                    module.setTurnSignalLeftOn(!module.isTurnSignalLeftOn());
                } else {
                    warningsOn = false;
                }
                module.setTurnSignalRightOn(false);
            } else if (turnRight.isPressed()) {
                module.setTurnSignalLeftOn(false);
                if (!warningsOn) {
                    module.setTurnSignalRightOn(!module.isTurnSignalRightOn());
                } else {
                    warningsOn = false;
                }
            } else if (warnings.isPressed()) {
                warningsOn = !warningsOn;
                module.setTurnSignalLeftOn(warningsOn);
                module.setTurnSignalRightOn(warningsOn);
            }
        }
    }

    @Override
    public GuiComponent<?> createHud() {
        if (module.hasSiren()) {
            GuiPanel cir = new GuiPanel();
            cir.setCssId("siren_hud");
            cir.setLayout(new GridLayout(-1, 15, 0, GridLayout.GridDirection.HORIZONTAL, 1));
            //cir.setCssClass("hud_item");
            cir.add(new UpdatableGuiLabel("%s %s %s", (s) -> String.format(s, module.isHeadLightsOn() ? "Hd" : "", module.isBeaconsOn() ? "Be" : "", module.isSirenOn() ? "Si" : "")));

            cir.add(new UpdatableGuiLabel("%s", (s) -> String.format(s, module.getFuelLevel() + "%")));

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
