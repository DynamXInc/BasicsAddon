package fr.dynamx.addons.basics.client;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiProgressBar;
import fr.aym.acsguis.component.textarea.UpdatableGuiLabel;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.LightHolder;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.addons.basics.common.modules.FuelTankModule;
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

public class FuelTankController implements IVehicleController {
    public static final ResourceLocation STYLE = new ResourceLocation(BasicsAddon.ID, "css/vehicle_hud.css");

    private final BaseVehicleEntity<?> entity;
    private final FuelTankModule module;

    public FuelTankController(BaseVehicleEntity<?> entity, FuelTankModule module) {
        this.entity = entity;
        this.module = module;
    }

    @Override
    public void update() {

    }

    @Override
    public GuiComponent<?> createHud() {
        if (module != null) {
            GuiPanel hud = new GuiPanel();
            hud.add(new UpdatableGuiLabel("Fuel : %s/"+module.getInfo().getTankSize(), (s) -> String.format(s, (int)module.getFuel())));

            return hud;
        }
        return null;
    }

    @Override
    public List<ResourceLocation> getHudCssStyles() {
        if (module != null)
            return Collections.singletonList(STYLE);
        return null;
    }
}
