package fr.dynamx.addon.basic.fuel;

import fr.dynamx.addon.basic.BasicAddon;
import fr.dynamx.addon.basic.BasicAddonMod;
import fr.dynamx.api.addon.DynamXApiContext;
import fr.dynamx.api.ecs.EcsLinkedEntity;
import fr.dynamx.api.ecs.EntityRef;
import fr.dynamx.api.pack.PackEntityInfo;
import fr.dynamx.api.pack.PackType;
import fr.dynamx.api.vehicle.DrivingState;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public final class FuelSystem {

    /** Cause de coupure moteur propre à cet addon (composable avec celles d'autres addons). */
    private static final String CAUSE_NO_FUEL = "dynamx_basic_addon:no_fuel";

    private FuelSystem() {}

    /** Consommation, appelé chaque fin de tick serveur ; ne travaille qu'une fois par seconde. */
    public static void onServerTick(MinecraftServer server) {
        if (server.getTickCount() % 20 != 0) {
            return;
        }
        DynamXApiContext dynamx = BasicAddon.apiContext();
        if (dynamx == null) {
            return;
        }
        dynamx.withEntities(ref -> {
            FuelComponent fuel = ref.get(FuelComponent.class);
            if (fuel == null) {
                return;
            }
            if (fuel.isEmpty()) {
                // Coupure moteur (non persistée : la cause est reposée ici après un reload) —
                // idempotent, et sans écraser les causes d'autres addons.
                ref.disableEngine(CAUSE_NO_FUEL);
                return;
            }
            DrivingState input = ref.drivingState();
            float throttle = input != null ? input.throttle() : 0f;
            if (throttle > 0f) {
                FuelComponent updated = fuel.withFuel(fuel.fuel() - fuel.consumptionPerSecond() * throttle);
                ref.set(updated);
                if (updated.isEmpty()) {
                    ref.disableEngine(CAUSE_NO_FUEL);
                }
            }
        }, FuelComponent.class);
    }

    /** Résultat du remplissage, décidé sous le verrou ECS et affiché après. */
    private record RefillOutcome(InteractionResult result, @Nullable String message) {
        static final RefillOutcome PASS = new RefillOutcome(InteractionResult.PASS, null);
    }

    /**
     * Clic droit au jerrican sur un véhicule (branché sur UseEntityCallback AVANT le système
     * de clé, pour que le remplissage passe même sur un véhicule verrouillé — même
     * comportement que la FuelTank InteractivePart 1.12).
     */
    public static InteractionResult onUseEntity(Player player, Level level, InteractionHand hand,
                                                Entity entity, @Nullable EntityHitResult hitResult) {
        if (!(entity instanceof EcsLinkedEntity linked)) {
            return InteractionResult.PASS;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(BasicAddonMod.JERRYCAN)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        DynamXApiContext dynamx = BasicAddon.apiContext();
        if (dynamx == null) {
            return InteractionResult.PASS;
        }

        RefillOutcome[] outcome = { RefillOutcome.PASS };
        boolean found = dynamx.withEntity(linked.ecsEntityId(), ref -> outcome[0] = refill(ref, stack));
        if (!found) {
            return InteractionResult.PASS;
        }
        if (outcome[0].message() != null) {
            player.displayClientMessage(Component.literal(outcome[0].message()), true);
        }
        return outcome[0].result();
    }

    /** Appelé sous le verrou ECS : transvase jerrican → réservoir, ou remplit le jerrican vide. */
    private static RefillOutcome refill(EntityRef ref, ItemStack jerrycan) {
        PackEntityInfo info = ref.packInfo();
        if (info == null || info.type() != PackType.VEHICLE) {
            // Pas un véhicule (prop, ...) : ne pas avaler le clic, laisser l'interaction suivre.
            return RefillOutcome.PASS;
        }
        FuelComponent tank = ref.get(FuelComponent.class);
        if (tank == null) {
            return new RefillOutcome(InteractionResult.SUCCESS_SERVER, "Ce véhicule n'a pas de réservoir.");
        }
        float inJerrycan = JerrycanItem.getFuel(jerrycan);
        if (inJerrycan > 0f) {
            float transferred = Math.min(inJerrycan, tank.tankSize() - tank.fuel());
            if (transferred <= 0f) {
                return new RefillOutcome(InteractionResult.SUCCESS_SERVER, "Réservoir plein.");
            }
            JerrycanItem.setFuel(jerrycan, inJerrycan - transferred);
            ref.set(tank.withFuel(tank.fuel() + transferred));
            ref.enableEngine(CAUSE_NO_FUEL); // le moteur repart (sauf autre cause de coupure)
            return new RefillOutcome(InteractionResult.SUCCESS_SERVER,
                    String.format("+%.1f L — réservoir : %.1f / %.0f L",
                            transferred, tank.fuel() + transferred, tank.tankSize()));
        }
        // Jerrican vide : il se remplit (comportement 1.12 — la "pompe" n'existe pas encore)
        JerrycanItem.setFuel(jerrycan, JerrycanItem.CAPACITY);
        return new RefillOutcome(InteractionResult.SUCCESS_SERVER, "Jerrican rempli.");
    }
}
