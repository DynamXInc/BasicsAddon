package fr.dynamx.addon.basic;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.dynamx.addon.basic.fuel.FuelComponent;
import fr.dynamx.addon.basic.key.VehicleLockComponent;
import fr.dynamx.addon.basic.plate.PlateComponent;
import fr.dynamx.api.addon.DynamXApiContext;
import fr.dynamx.api.ecs.EcsLinkedEntity;
import fr.dynamx.api.pack.PackEntityInfo;
import fr.dynamx.api.pack.PackType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public final class BasicsCommands {

    private static final double SEARCH_RADIUS = 8.0;

    private BasicsCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dynamxbasics")
                .then(Commands.literal("fuel").executes(ctx -> inspect(ctx, ref -> {
                    FuelComponent fuel = ref.get(FuelComponent.class);
                    return fuel == null ? "Ce véhicule n'a pas de réservoir."
                            : String.format("Réservoir : %.1f / %.0f L", fuel.fuel(), fuel.tankSize());
                })))
                .then(Commands.literal("plate").executes(ctx -> inspect(ctx, ref -> {
                    PlateComponent plate = ref.get(PlateComponent.class);
                    return plate == null ? "Ce véhicule n'a pas de plaque."
                            : "Plaque : " + plate.plate();
                })))
                .then(Commands.literal("spawn")
                        .requires(source -> source.permissions()
                                .hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER))
                        .then(Commands.argument("pack", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    DynamXApiContext dynamx = BasicAddon.apiContext();
                                    if (dynamx != null) {
                                        dynamx.packEntities(PackType.VEHICLE).stream()
                                                .map(PackEntityInfo::packId)
                                                .distinct()
                                                .forEach(builder::suggest);
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("vehicle", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            DynamXApiContext dynamx = BasicAddon.apiContext();
                                            if (dynamx != null) {
                                                String pack = StringArgumentType.getString(ctx, "pack");
                                                dynamx.packEntities(PackType.VEHICLE).stream()
                                                        .filter(info -> info.packId().equals(pack))
                                                        .map(PackEntityInfo::typeId)
                                                        .forEach(builder::suggest);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(BasicsCommands::spawnWithKey)))));
    }

    /**
     * Parité {@code /basicsspawn} 1.12 : spawn le véhicule devant le joueur via l'API DynamX,
     * pose une serrure verrouillée, et donne au joueur la clé liée.
     */
    private static int spawnWithKey(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) {
            ctx.getSource().sendFailure(Component.literal("Réservé aux joueurs."));
            return 0;
        }
        DynamXApiContext dynamx = BasicAddon.apiContext();
        if (dynamx == null) {
            ctx.getSource().sendFailure(Component.literal("DynamX n'est pas initialisé."));
            return 0;
        }
        String packId = StringArgumentType.getString(ctx, "pack");
        String typeId = StringArgumentType.getString(ctx, "vehicle");

        // 5 blocs devant le joueur, 1 bloc au-dessus du sol (même convention que le spawn par item)
        Vec3 look = player.getLookAngle();
        Vec3 pos = player.position().add(new Vec3(look.x, 0, look.z).normalize().scale(5)).add(0, 1, 0);
        String dimensionId = player.level().dimension().identifier().toString();

        int ecsId = dynamx.spawnPackEntity(PackType.VEHICLE, packId, typeId,
                (float) pos.x, (float) pos.y, (float) pos.z, player.getYRot(), dimensionId);
        if (ecsId < 0) {
            ctx.getSource().sendFailure(Component.literal(
                    "Spawn impossible pour " + packId + ":" + typeId + " (voir logs)."));
            return 0;
        }

        // Serrure verrouillée + clé liée pour le joueur
        String keyId = UUID.randomUUID().toString();
        dynamx.withEntity(ecsId, ref -> ref.set(new VehicleLockComponent(keyId, true)));
        ItemStack key = new ItemStack(BasicAddonMod.VEHICLE_KEY);
        key.set(BasicAddonMod.KEY_ID, keyId);
        key.set(BasicAddonMod.KEY_VEHICLE_NAME, typeId);
        if (!player.addItem(key)) {
            player.drop(key, false);
        }
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Véhicule " + packId + ":" + typeId + " spawné (verrouillé), clé donnée."), false);
        return 1;
    }

    private static int inspect(CommandContext<CommandSourceStack> ctx,
                               Function<fr.dynamx.api.ecs.EntityRef, String> reader) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) {
            ctx.getSource().sendFailure(Component.literal("Réservé aux joueurs."));
            return 0;
        }
        DynamXApiContext dynamx = BasicAddon.apiContext();
        if (dynamx == null) {
            ctx.getSource().sendFailure(Component.literal("DynamX n'est pas initialisé."));
            return 0;
        }
        List<Entity> nearby = player.level().getEntities(player,
                player.getBoundingBox().inflate(SEARCH_RADIUS), e -> e instanceof EcsLinkedEntity);
        var nearest = nearby.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)));
        if (nearest.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Aucun véhicule DynamX à proximité."));
            return 0;
        }
        String[] message = { null };
        dynamx.withEntity(((EcsLinkedEntity) nearest.get()).ecsEntityId(),
                ref -> message[0] = reader.apply(ref));
        if (message[0] == null) {
            ctx.getSource().sendFailure(Component.literal("Véhicule introuvable côté physique."));
            return 0;
        }
        String result = message[0];
        ctx.getSource().sendSuccess(() -> Component.literal(result), false);
        return 1;
    }
}
