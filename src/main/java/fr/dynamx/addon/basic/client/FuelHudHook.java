package fr.dynamx.addon.basic.client;

import fr.dynamx.addon.basic.BasicAddon;
import fr.dynamx.addon.basic.fuel.FuelComponent;
import fr.dynamx.api.addon.DynamXApiContext;
import fr.dynamx.api.ecs.EcsLinkedEntity;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public final class FuelHudHook {

    private static final Identifier FUEL_HUD = Identifier.parse("dynamx_basic_addon:fuel_hud");

    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 8;
    private static final int MARGIN = 10;
    private static final int COLOR_BACK = 0xA0000000;
    private static final int COLOR_OK = 0xFF3FBF3F;
    private static final int COLOR_LOW = 0xFFE6A020;
    private static final int COLOR_EMPTY = 0xFFE64040;
    private static final int COLOR_TEXT = 0xE6FFFFFF;

    private FuelHudHook() {}

    public static void register() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR, FUEL_HUD,
                (graphics, deltaTracker) -> render(graphics));
    }

    private static void render(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.options.hideGui
                || mc.getDebugOverlay().showDebugScreen() || mc.player.isSpectator()) {
            return;
        }
        if (!(mc.player.getVehicle() instanceof EcsLinkedEntity linked)) {
            return;
        }
        DynamXApiContext dynamx = BasicAddon.apiContext();
        if (dynamx == null) {
            return;
        }

        // Lecture courte sous le verrou ECS ; dessin après.
        float[] fuelAndSize = {-1f, -1f};
        dynamx.withEntity(linked.localEcsEntityId(), ref -> {
            FuelComponent fuel = ref.get(FuelComponent.class);
            if (fuel != null) {
                fuelAndSize[0] = fuel.fuel();
                fuelAndSize[1] = fuel.tankSize();
            }
        });
        if (fuelAndSize[1] <= 0f) {
            return; // pas de réservoir sur ce véhicule
        }

        float ratio = Math.max(0f, Math.min(1f, fuelAndSize[0] / fuelAndSize[1]));
        int x = MARGIN;
        int y = graphics.guiHeight() - MARGIN - BAR_HEIGHT;

        graphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, COLOR_BACK);
        int fillColor = ratio <= 0.02f ? COLOR_EMPTY : (ratio <= 0.25f ? COLOR_LOW : COLOR_OK);
        graphics.fill(x + 1, y + 1, x + 1 + Math.round((BAR_WIDTH - 2) * ratio), y + BAR_HEIGHT - 1, fillColor);

        String label = String.format("%.0f / %.0f L", fuelAndSize[0], fuelAndSize[1]);
        graphics.drawString(mc.font, label, x + BAR_WIDTH + 6, y, COLOR_TEXT, true);
        if (ratio <= 0.02f) {
            graphics.drawString(mc.font, "Panne sèche !", x, y - 12, COLOR_EMPTY, true);
        }
    }
}
