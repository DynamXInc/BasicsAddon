package fr.dynamx.addon.basic.horn;

import fr.dynamx.addon.basic.BasicAddonMod;
import fr.dynamx.api.ecs.EcsLinkedEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class HornServerHandler {

    private static final int COOLDOWN_TICKS = 20;

    /** Dernier coup de klaxon par id d'entité ECS ; vidé à l'arrêt du serveur (les ids ECS sont
     *  process-locaux et réattribués à chaque monde — sans purge la map fuirait entre les parties). */
    private static final Map<Integer, Long> LAST_HORN = new ConcurrentHashMap<>();

    private HornServerHandler() {}

    public static void register() {
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> LAST_HORN.clear());
        ServerPlayNetworking.registerGlobalReceiver(HornPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            Entity vehicle = player.getVehicle();
            if (!(vehicle instanceof EcsLinkedEntity linked) || linked.ecsEntityId() < 0) {
                return;
            }
            long now = player.level().getGameTime();
            Long last = LAST_HORN.get(linked.ecsEntityId());
            if (last != null && now - last < COOLDOWN_TICKS) {
                return;
            }
            LAST_HORN.put(linked.ecsEntityId(), now);
            player.level().playSound(null, vehicle.getX(), vehicle.getY(), vehicle.getZ(),
                    BasicAddonMod.HORN_SOUND, SoundSource.NEUTRAL, 3.0f, 1.0f);
        });
    }
}
