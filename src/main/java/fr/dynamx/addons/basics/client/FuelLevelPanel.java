package fr.dynamx.addons.basics.client;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.api.entities.VehicleEntityProperties;
import fr.dynamx.client.handlers.hud.CircleCounterPanel;
import fr.dynamx.common.contentpack.type.vehicle.EngineInfo;
import fr.dynamx.utils.DynamXConstants;
import net.minecraft.util.ResourceLocation;

public class FuelLevelPanel extends CircleCounterPanel {
    private final FuelTankController tankController;

    public FuelLevelPanel(FuelTankController tankController, float scale, float maxRpm) {
        super(new ResourceLocation(BasicsAddon.ID, "textures/fuelbar.png"), true, 300, 300, scale, maxRpm);
        this.tankController = tankController;
    }

    @Override
    public void tick() {
        super.tick();
        prevValue = value;
        value = tankController.getModule().getFuel()/2;
    }
}