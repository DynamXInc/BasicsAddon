package fr.dynamx.addons.basics.client;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.client.handlers.hud.CircleCounterPanel;
import net.minecraft.util.ResourceLocation;

public class FuelLevelPanel extends CircleCounterPanel {
    private final FuelTankController tankController;

    public FuelLevelPanel(FuelTankController tankController, float scale, float maxRpm) {
        super(new ResourceLocation(BasicsAddon.ID, "textures/fuelbar.png"), true, 300, 300, scale, maxRpm);
        this.tankController = tankController;
    }

    @Override
    public boolean tick() {
        if(super.tick()) {
            prevValue = value;
            value = tankController.getModule().getFuel() / 2;
            return true;
        }
        return false;
    }
}