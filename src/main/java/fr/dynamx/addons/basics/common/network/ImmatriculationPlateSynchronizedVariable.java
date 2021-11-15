package fr.dynamx.addons.basics.common.network;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.ImmatriculationPlateModule;
import fr.dynamx.api.network.sync.PhysicsEntityNetHandler;
import fr.dynamx.api.network.sync.SyncTarget;
import fr.dynamx.api.network.sync.SynchronizedVariable;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.network.sync.MessagePhysicsEntitySync;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

public class ImmatriculationPlateSynchronizedVariable<A extends BaseVehicleEntity<?>> implements SynchronizedVariable<A> {
    public static final ResourceLocation NAME = new ResourceLocation(BasicsAddon.ID, "plate");

    private String plate;

    @Override
    public SyncTarget getValueFrom(A entity, PhysicsEntityNetHandler<A> network, Side side, int syncTick) {
        ImmatriculationPlateModule module = entity.getModuleByType(ImmatriculationPlateModule.class);
        if (!module.getPlate().equals(plate)) {
            plate = module.getPlate();
            return SyncTarget.ALL_CLIENTS;
        }
        return SyncTarget.NONE;
    }

    @Override
    public void setValueTo(A entity, PhysicsEntityNetHandler<A> network, @Nullable MessagePhysicsEntitySync msg, Side side) {
        ImmatriculationPlateModule module = entity.getModuleByType(ImmatriculationPlateModule.class);
        module.setPlate(plate);
    }

    @Override
    public void interpolate(A entity, PhysicsEntityNetHandler<A> network, @Nullable MessagePhysicsEntitySync msg, int step) {
        setValueTo(entity, network, msg, Side.CLIENT);
    }

    @Override
    public void write(ByteBuf buf, boolean compress) {
        ByteBufUtils.writeUTF8String(buf, plate);
    }

    @Override
    public void writeEntityValues(A entity, ByteBuf buf) {
        ImmatriculationPlateModule module = entity.getModuleByType(ImmatriculationPlateModule.class);
        ByteBufUtils.writeUTF8String(buf, module.getPlate());
    }

    @Override
    public void read(ByteBuf buf) {
        plate = ByteBufUtils.readUTF8String(buf);
    }
}
