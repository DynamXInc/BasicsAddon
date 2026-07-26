package fr.dynamx.addon.basic.key;

import fr.dynamx.addon.basic.BasicAddon;
import fr.dynamx.addon.basic.BasicAddonMod;
import fr.dynamx.api.addon.DynamXApiContext;
import fr.dynamx.api.ecs.EcsLinkedEntity;
import fr.dynamx.api.ecs.EntityRef;
import fr.dynamx.api.pack.PackEntityInfo;
import fr.dynamx.api.pack.PackType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class VehicleKeySystem {

    private VehicleKeySystem() {}

    /** Son joué selon l'action (équivalent du LockSound configurable de l'addon 1.12). */
    private enum KeySound { BIND, LOCK, UNLOCK }

    /** Action décidée sous le verrou ECS, message et son émis hors verrou. */
    private record Outcome(InteractionResult result, @Nullable String message, @Nullable KeySound sound) {
        static final Outcome PASS = new Outcome(InteractionResult.PASS, null, null);

        Outcome(InteractionResult result, @Nullable String message) {
            this(result, message, null);
        }
    }

    public static InteractionResult onUseEntity(Player player, Level level, InteractionHand hand,
                                                Entity entity, @Nullable EntityHitResult hitResult) {
        if (!(entity instanceof EcsLinkedEntity linked)) {
            return InteractionResult.PASS;
        }
        ItemStack stack = player.getItemInHand(hand);
        boolean holdingKey = stack.is(BasicAddonMod.VEHICLE_KEY);

        if (level.isClientSide()) {
            // Serveur autoritaire : on consomme juste le clic quand on tient une clé, pour
            // que l'interaction parte au serveur sans que MC tente autre chose côté client.
            return holdingKey ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        DynamXApiContext dynamx = BasicAddon.apiContext();
        if (dynamx == null) {
            return InteractionResult.PASS;
        }

        // Décision prise sous le verrou ECS (withEntity), message envoyé après.
        Outcome[] outcome = { Outcome.PASS };
        dynamx.withEntity(linked.ecsEntityId(),
                ref -> outcome[0] = handleServerInteraction(ref, stack, holdingKey));

        if (outcome[0].message() != null) {
            player.displayClientMessage(Component.literal(outcome[0].message()), true);
        }
        if (outcome[0].sound() != null) {
            switch (outcome[0].sound()) {
                case BIND -> level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 0.6f, 1.4f);
                case LOCK -> level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.NEUTRAL, 0.8f, 1.4f);
                case UNLOCK -> level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.NEUTRAL, 0.8f, 1.4f);
            }
        }
        return outcome[0].result();
    }

    /** Appelé par {@code withEntity}, donc sous le verrou ECS : composants seulement, pas d'IO. */
    private static Outcome handleServerInteraction(EntityRef ref, ItemStack stack, boolean holdingKey) {
        PackEntityInfo info = ref.packInfo();
        if (info == null || info.type() != PackType.VEHICLE) {
            return Outcome.PASS;
        }
        VehicleLockComponent lock = ref.get(VehicleLockComponent.class);

        if (!holdingKey) {
            if (lock != null && lock.locked()) {
                return new Outcome(InteractionResult.SUCCESS_SERVER, "Le véhicule est verrouillé.");
            }
            return Outcome.PASS; // montée normale
        }

        String keyId = stack.get(BasicAddonMod.KEY_ID);

        if (lock == null) {
            // Véhicule sans serrure : on le lie à la clé (une clé déjà liée peut ouvrir
            // plusieurs véhicules — comportement "IsMultiKey" de l'addon 1.12).
            String id = keyId != null ? keyId : UUID.randomUUID().toString();
            stack.set(BasicAddonMod.KEY_ID, id);
            stack.set(BasicAddonMod.KEY_VEHICLE_NAME, info.typeId());
            ref.set(new VehicleLockComponent(id, false));
            return new Outcome(InteractionResult.SUCCESS_SERVER, "Clé liée au véhicule.", KeySound.BIND);
        }
        if (keyId == null) {
            return new Outcome(InteractionResult.SUCCESS_SERVER, "Ce véhicule a déjà une clé.");
        }
        if (lock.keyId().equals(keyId)) {
            boolean nowLocked = !lock.locked();
            ref.set(lock.withLocked(nowLocked));
            return new Outcome(InteractionResult.SUCCESS_SERVER,
                    nowLocked ? "Véhicule verrouillé." : "Véhicule déverrouillé.",
                    nowLocked ? KeySound.LOCK : KeySound.UNLOCK);
        }
        return new Outcome(InteractionResult.SUCCESS_SERVER, "Cette clé ne correspond pas à ce véhicule.");
    }
}
