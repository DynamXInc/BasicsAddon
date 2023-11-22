package fr.dynamx.addons.basics.common.event;

import fr.dynamx.api.events.VehicleEntityEvent;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.PackPhysicsEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.relauncher.Side;

public class BasicsAddonEvent
{
    @Cancelable
    public static class EventLockVehicle extends VehicleEntityEvent
    {
        private final EntityPlayer player;
        private final EnumLockAction action;

        public EventLockVehicle(Side side, PackPhysicsEntity<?, ?> vehicleEntity, EntityPlayer player, EnumLockAction action) {
            super(side, vehicleEntity);
            this.player = player;
            this.action = action;
        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public EnumLockAction getAction() {
            return action;
        }

        public enum EnumLockAction {
            ASSOCIATE, LOCK, UNLOCK
        }
    }
}
