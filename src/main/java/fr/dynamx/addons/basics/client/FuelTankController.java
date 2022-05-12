package fr.dynamx.addons.basics.client;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.UpdatableGuiLabel;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.FuelTankModule;
import fr.dynamx.api.entities.VehicleEntityProperties;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.client.handlers.hud.SpeedometerPanel;
import fr.dynamx.common.contentpack.type.vehicle.EngineInfo;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.utils.debug.ClientDebugSystem;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class FuelTankController implements IVehicleController {
    public static final ResourceLocation STYLE = new ResourceLocation(BasicsAddon.ID, "css/vehicle_hud.css");

    private final FuelTankModule module;

    public FuelTankController(FuelTankModule module) {
        this.module = module;
    }

    @Override
    public void update() {

    }

    @Override
    public GuiComponent<?> createHud() {
        if (module != null) {
            GuiPanel panel = new GuiPanel();
            float maxFuel = module.getInfo().getTankSize();
            float scale = 90f / 300;
            GuiPanel speed = new FuelLevelPanel(this, scale, maxFuel);
            speed.setCssClass("speed_pane");
            speed.setCssId("fuel_gauge");

            //speed.add(new UpdatableGuiLabel("%s", s -> String.format(s, module.getFuel())).setCssId("engine_fuel"));

            panel.add(speed);
            panel.setCssId("engine_hud");

            return panel;
        }
        return null;
    }

    @Override
    public List<ResourceLocation> getHudCssStyles() {
        if (module != null)
            return Collections.singletonList(STYLE);
        return null;
    }

    public FuelTankModule getModule() {
        return module;
    }
}
