package fr.dynamx.addons.basics.client;

import fr.aym.acsguis.component.GuiComponent;
import fr.dynamx.addons.basics.common.modules.InteractionKeyModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.common.entities.BaseVehicleEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.Collections;
import java.util.List;

public class InteractionKeyController implements IVehicleController {
    @SideOnly(Side.CLIENT)
    public static final KeyBinding interaction = new KeyBinding("Interaction In Vehicle", Keyboard.KEY_N, "DynamX basics");
    private final BaseVehicleEntity<?> entity;
    private final InteractionKeyModule module;

    public InteractionKeyController(BaseVehicleEntity<?> entity, InteractionKeyModule module) {
        this.entity = entity;
        this.module = module;
        unpress(interaction);
    }

    private void unpress(KeyBinding key) {
        while (key.isPressed()) {
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void update() {
        if (interaction.isPressed()) {
            module.setActivate(!module.isActivateOn());
        }
    }

    @Override
    public GuiComponent<?> createHud() {
        return null;
    }

    @Override
    public List<ResourceLocation> getHudCssStyles() {
        return Collections.EMPTY_LIST;
    }
}