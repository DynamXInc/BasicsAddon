package fr.dynamx.addons.basics.common.network;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.FuelTankModule;
import fr.dynamx.api.network.sync.PhysicsEntityNetHandler;
import fr.dynamx.api.network.sync.SyncTarget;
import fr.dynamx.api.network.sync.SynchronizedVariable;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.network.sync.MessagePhysicsEntitySync;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

public class FuelSynchronizedVariable<A extends BaseVehicleEntity<?>> implements SynchronizedVariable<A> {

    public static final ResourceLocation NAME = new ResourceLocation(BasicsAddon.ID, "fuel");

    private float fuel;

    @Override
    public SyncTarget getValueFrom(A entity, PhysicsEntityNetHandler<A> network, Side side, int syncTick) {
        FuelTankModule module = entity.getModuleByType(FuelTankModule.class);
        if (module.getFuel() != fuel) {
            fuel = module.getFuel();
            return SyncTarget.ALL_CLIENTS;
        }
        return SyncTarget.NONE;
    }

    @Override
    public void setValueTo(A entity, PhysicsEntityNetHandler<A> network, @Nullable MessagePhysicsEntitySync msg, Side side) {
        FuelTankModule module = entity.getModuleByType(FuelTankModule.class);
        module.setFuel(fuel);
    }

    @Override
    public void interpolate(A entity, PhysicsEntityNetHandler<A> network, @Nullable MessagePhysicsEntitySync msg, int step) {
        setValueTo(entity, network, msg, Side.CLIENT);
    }

    @Override
    public void write(ByteBuf buf, boolean compress) {
        buf.writeFloat(fuel);
    }

    @Override
    public void writeEntityValues(A entity, ByteBuf buf) {
        FuelTankModule module = entity.getModuleByType(FuelTankModule.class);
        buf.writeFloat(module.getFuel());
    }

    @Override
    public void read(ByteBuf buf) {
        fuel = buf.readFloat();
    }
}