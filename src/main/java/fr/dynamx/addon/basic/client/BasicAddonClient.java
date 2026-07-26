package fr.dynamx.addon.basic.client;

import com.mojang.blaze3d.platform.InputConstants;
import fr.dynamx.addon.basic.BasicAddonMod;
import fr.dynamx.addon.basic.horn.HornPayload;
import fr.dynamx.api.ecs.EcsLinkedEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class BasicAddonClient implements ClientModInitializer {

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(BasicAddonMod.MOD_ID, "basics"));

    private static KeyMapping hornKey;

    @Override
    public void onInitializeClient() {
        FuelHudHook.register();

        hornKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.dynamx_basic_addon.horn", GLFW.GLFW_KEY_K, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }
            while (hornKey.consumeClick()) {
                if (client.player.getVehicle() instanceof EcsLinkedEntity) {
                    ClientPlayNetworking.send(new HornPayload());
                }
            }
        });
    }
}
