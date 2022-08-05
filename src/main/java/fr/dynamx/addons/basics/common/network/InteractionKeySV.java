package fr.dynamx.addons.basics.common.network;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.InteractionKeyModule;
import fr.dynamx.api.network.sync.PhysicsEntityNetHandler;
import fr.dynamx.api.network.sync.SyncTarget;
import fr.dynamx.api.network.sync.SynchronizedVariable;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.network.sync.MessagePhysicsEntitySync;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

public class InteractionKeySV<A extends BaseVehicleEntity<?>> implements SynchronizedVariable<A> {

    public static final ResourceLocation NAME = new ResourceLocation(BasicsAddon.ID, "interactionkey");

    private boolean activate;

    @Override
    public SyncTarget getValueFrom(A entity, PhysicsEntityNetHandler<A> network, Side side, int syncTick) {
        InteractionKeyModule module = entity.getModuleByType(InteractionKeyModule.class);
        if (module.isActivateOn() != activate) {
            activate = module.isActivateOn();
            return SyncTarget.spectatorForSide(side);
        }
        return SyncTarget.NONE;
    }

    @Override
    public void setValueTo(A entity, PhysicsEntityNetHandler<A> network, @Nullable MessagePhysicsEntitySync msg, Side side) {
        InteractionKeyModule module = entity.getModuleByType(InteractionKeyModule.class);
        module.setActivate(activate);
    }

    @Override
    public void interpolate(A entity, PhysicsEntityNetHandler<A> network, @Nullable MessagePhysicsEntitySync msg, int step) {
        setValueTo(entity, network, msg, Side.CLIENT);
    }

    @Override
    public void write(ByteBuf buf, boolean compress) {
        buf.writeBoolean(activate);
    }

    @Override
    public void writeEntityValues(A entity, ByteBuf buf) {
        InteractionKeyModule module = entity.getModuleByType(InteractionKeyModule.class);
        buf.writeBoolean(module.isActivateOn());
    }

    @Override
    public void read(ByteBuf buf) {
        activate = buf.readBoolean();
    }

}
