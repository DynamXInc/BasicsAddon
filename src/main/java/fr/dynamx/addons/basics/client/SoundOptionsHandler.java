package fr.dynamx.addons.basics.client;

import fr.dynamx.addons.basics.BasicsAddon;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = BasicsAddon.ID, value = Side.CLIENT)
public class SoundOptionsHandler {

    @SubscribeEvent
    public static void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof GuiScreenOptionsSounds))
            return;

        GuiButton subtitlesButton = null;
        GuiButton doneButton = null;
        for (GuiButton button : event.getButtonList()) {
            if (button.id == 201) subtitlesButton = button;
            if (button.id == 200) doneButton = button;
        }

        int centerX = event.getGui().width / 2;
        int sliderY;

        if (subtitlesButton != null) {
            sliderY = subtitlesButton.y;
            subtitlesButton.y += 24;
        } else {
            sliderY = event.getGui().height / 6 + 144;
        }

        if (doneButton != null) {
            doneButton.y += 24;
        }

        GuiSlider sirenSlider = new GuiSlider(
                9100, centerX - 155, sliderY, 310, 20,
                I18n.format("basadd.sound.siren") + ": ", "%",
                0, 100, BasicsAddonConfig.sirenVolume * 100, false, true,
                slider -> {
                    BasicsAddonConfig.sirenVolume = slider.getValue() / 100.0;
                    ConfigManager.sync(BasicsAddon.ID, Config.Type.INSTANCE);
                }
        );
        event.getButtonList().add(sirenSlider);
    }
}
