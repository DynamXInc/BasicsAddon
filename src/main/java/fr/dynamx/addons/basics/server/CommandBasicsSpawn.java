package fr.dynamx.addons.basics.server;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.addons.basics.utils.VehicleKeyUtils;
import fr.dynamx.common.contentpack.DynamXObjectLoaders;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.vehicles.CarEntity;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandBasicsSpawn extends CommandBase
{
    @Override
    public String getName() {
        return "basicsspawn";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/basicspawn <vehicle> <x> <y> <z> <yaw_angle> [metadata] [owner[s]]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length >= 5) {
            String id = args[0];
            if (DynamXObjectLoaders.WHEELED_VEHICLES.findInfo(id) == null) {
                throw new WrongUsageException("Unknown vehicle name "+id);
            }
            BlockPos pos = parseBlockPos(sender, args, 1, true);
            int meta = 0;
            if(args.length >= 6) {
                meta = parseInt(args[5]);
            }
            //TODO SUPPORT TRAILERS ETC
            CarEntity<?> e = new CarEntity<>(id.replace("car:", ""), sender.getEntityWorld(), new Vector3f(pos.getX(), pos.getY(), pos.getZ()), parseInt(args[4]), meta);
            if(args.length == 7) {
                e.setInitCallback((modularEntity, modules) -> {
                    ItemStack key = VehicleKeyUtils.getKeyForVehicle((BaseVehicleEntity<?>) modularEntity);
                    try {
                        List<EntityPlayerMP> players = getPlayers(server, sender, args[6]);
                        players.forEach(p -> p.inventory.addItemStackToInventory(key.copy()));
                    } catch (CommandException ex) {
                        sender.sendMessage(new TextComponentString("§r Failed to give key of the vehicle : "+ex.getLocalizedMessage()));
                        ex.printStackTrace();
                    }
                    BasicsAddonModule m = modularEntity.getModuleByType(BasicsAddonModule.class);
                    m.setHasLinkedKey(true);
                    m.setLocked(true);
                    sender.sendMessage(new TextComponentString("Vehicle successfully spawned !"));
                });
            }
            sender.getEntityWorld().spawnEntity(e);
        } else {
            throw new WrongUsageException(getUsage(sender));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if(args.length == 1) {
            return getListOfStringsMatchingLastWord(args, DynamXObjectLoaders.WHEELED_VEHICLES.getInfos().keySet());
        }
        else if(args.length == 2 || args.length == 3 || args.length == 4) {
            return CommandBase.getTabCompletionCoordinate(args, 2, targetPos);
        } else if(args.length == 7) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}