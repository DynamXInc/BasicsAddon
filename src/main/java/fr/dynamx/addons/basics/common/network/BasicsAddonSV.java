package fr.dynamx.addons.basics.common.network;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.api.network.sync.PhysicsEntityNetHandler;
import fr.dynamx.api.network.sync.SyncTarget;
import fr.dynamx.api.network.sync.SynchronizedVariable;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.network.sync.MessagePhysicsEntitySync;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

public class BasicsAddonSV<A extends BaseVehicleEntity<?>> implements SynchronizedVariable<A> {
    public static final ResourceLocation NAME = new ResourceLocation(BasicsAddon.ID, "module");

    private byte vars;

    @Override
    public SyncTarget getValueFrom(A entity, PhysicsEntityNetHandler<A> network, Side side, int syncTick) {
        byte vars = encodeState(entity);
        if (vars != this.vars) {
            this.vars = vars;
            return SyncTarget.spectatorForSide(side);
        }
        return SyncTarget.NONE;
    }

    @Override
    public void setValueTo(A entity, PhysicsEntityNetHandler<A> network, @Nullable MessagePhysicsEntitySync msg, Side side) {
        BasicsAddonModule module = entity.getModuleByType(BasicsAddonModule.class);
        module.playKlaxon((vars & 1) == 1);
        module.setSirenOn((vars & 2) == 2);
        module.setHeadLightsOn((vars & 4) == 4);
        module.setTurnSignalLeftOn((vars & 8) == 8);
        module.setTurnSignalRightOn((vars & 16) == 16);
        module.setBeaconsOn((vars & 32) == 32);
        module.setLocked((vars & 64) == 64);
    }

    @Override
    public void interpolate(A entity, PhysicsEntityNetHandler<A> network, @Nullable MessagePhysicsEntitySync msg, int step) {
        setValueTo(entity, network, msg, Side.CLIENT);
    }

    @Override
    public void write(ByteBuf buf, boolean compress) {
        buf.writeInt(vars);
    }

    @Override
    public void writeEntityValues(A entity, ByteBuf buf) {
        byte vars = encodeState(entity);
        buf.writeInt(vars);
    }

    private byte encodeState(A entity) {
        BasicsAddonModule module = entity.getModuleByType(BasicsAddonModule.class);
        byte vars = 0;
        if (module.playKlaxon())
            vars = (byte) (vars | 1);
        if (module.isSirenOn())
            vars = (byte) (vars | 2);
        if (module.isHeadLightsOn())
            vars = (byte) (vars | 4);
        if (module.isTurnSignalLeftOn())
            vars = (byte) (vars | 8);
        if (module.isTurnSignalRightOn())
            vars = (byte) (vars | 16);
        if (module.isBeaconsOn())
            vars = (byte) (vars | 32);
        if (module.isLocked())
            vars = (byte) (vars | 64);
        return vars;
    }

    @Override
    public void read(ByteBuf buf) {
        vars = (byte) buf.readInt();
    }
}
