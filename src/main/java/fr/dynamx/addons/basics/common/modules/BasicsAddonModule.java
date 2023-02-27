package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.client.BasicsAddonController;
import fr.dynamx.addons.basics.common.LightHolder;
import fr.dynamx.addons.basics.common.infos.BasicsAddonInfos;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.api.network.sync.EntityVariable;
import fr.dynamx.api.network.sync.SynchronizationRules;
import fr.dynamx.api.network.sync.SynchronizedEntityVariable;
import fr.dynamx.client.ClientProxy;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import fr.dynamx.utils.optimization.Vector3fPool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SynchronizedEntityVariable.SynchronizedPhysicsModule
public class BasicsAddonModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
    private BasicsAddonController controller;
    private final BaseVehicleEntity<?> entity;
    private final BasicsAddonInfos infos;

    @SynchronizedEntityVariable(name = "state")
    private final EntityVariable<Integer> state = new EntityVariable<>(SynchronizationRules.CONTROLS_TO_SPECTATORS, 0);


    public BasicsAddonModule(BaseVehicleEntity<?> entity, BasicsAddonInfos infos) {
        this.entity = entity;
        this.infos = infos;
        if (entity.world.isRemote) {
            controller = new BasicsAddonController(entity, this, fr.dynamx.addons.basics.BasicsAddon.betterLightsLoaded ? new LightHolder() : null);
        }
    }

    public boolean hasKlaxon() {
        return infos != null && infos.klaxonSound != null;
    }

    public boolean hasSiren() {
        return infos != null && (infos.sirenSound != null || infos.sirenLightSource != 0);
    }

    public boolean hasSirenSound() {
        return infos != null && infos.sirenSound != null;
    }

    public boolean hasHeadLights() {
        return infos != null && infos.headLightsSource != 0;
    }

    @Override
    public boolean listenEntityUpdates(Side side) {
        return side.isClient() && (hasKlaxon() || hasSirenSound());
    }

    @Override
    public void updateEntity() {
        if (playKlaxon() && entity.world.isRemote && hasKlaxon())
            ClientProxy.SOUND_HANDLER.playSingleSound(Vector3fPool.get(entity.posX, entity.posY, entity.posZ), infos.klaxonSound, 1, 1);
        if (controller != null)
            controller.updateSiren();
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public IVehicleController createNewController() {
        return controller;
    }

    public boolean playKlaxon() {
        return (state.get() & 1) == 1;
    }

    public void playKlaxon(boolean playKlaxon) {
        state.set(playKlaxon ? state.get() | 1 : state.get() & ~1);
    }

    public boolean isSirenOn() {
        return (state.get() & 2) == 2;
    }

    public void setSirenOn(boolean sirenOn) {
        state.set(sirenOn ? state.get() | 2 : state.get() & ~2);
    }

    public boolean isBeaconsOn() {
        return (state.get() & 4) == 4;
    }

    public void setBeaconsOn(boolean beaconsOn) {
        state.set(beaconsOn ? state.get() | 4 : state.get() & ~4);
    }

    public boolean hasLinkedKey() {
        return (state.get() & 8) == 8;
    }

    public void setHasLinkedKey(boolean hasLinkedKey) {
        state.set(hasLinkedKey ? state.get() | 8 : state.get() & ~8);
    }

    public boolean isLocked() {
        return (state.get() & 16) == 16;
    }

    public void setLocked(boolean locked) {
        state.set(locked ? state.get() | 16 : state.get() & ~16);
    }

    public boolean isHeadLightsOn() {
        return (state.get() & 32) == 32;
    }

    public void setHeadLightsOn(boolean headLightsOn) {
        state.set(headLightsOn ? state.get() | 32 : state.get() & ~32);
    }

    public boolean hasTurnSignals() {
        return infos != null && infos.turnLeftLightSource != 0 && infos.turnRightLightSource != 0;
    }

    public boolean isTurnSignalLeftOn() {
        return (state.get() & 64) == 64;
    }

    public void setTurnSignalLeftOn(boolean turnSignalLeftOn) {
        state.set(turnSignalLeftOn ? state.get() | 64 : state.get() & ~64);
    }

    public boolean isTurnSignalRightOn() {
        return (state.get() & 128) == 128;
    }

    public void setTurnSignalRightOn(boolean turnSignalRightOn) {
        state.set(turnSignalRightOn ? state.get() | 128 : state.get() & ~128);
    }

    public BasicsAddonInfos getInfos() {
        return infos;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        byte vars = 0;
        if (isSirenOn())
            vars = (byte) (vars | 2);
        if (isHeadLightsOn())
            vars = (byte) (vars | 4);
        if (isTurnSignalLeftOn())
            vars = (byte) (vars | 8);
        if (isTurnSignalRightOn())
            vars = (byte) (vars | 16);
        if (isBeaconsOn())
            vars = (byte) (vars | 32);
        if (isLocked())
            vars = (byte) (vars | 64);

        tag.setByte("BasAddon.vals", vars);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("BasAddon.vals", Constants.NBT.TAG_BYTE)) {
            byte vars = tag.getByte("BasAddon.vals");
            setSirenOn((vars & 2) == 2);
            setHeadLightsOn((vars & 4) == 4);
            setTurnSignalLeftOn((vars & 8) == 8);
            setTurnSignalRightOn((vars & 16) == 16);
            setBeaconsOn((vars & 32) == 32);
            setLocked((vars & 64) == 64);
        } else { //backward compatibility
            setHasLinkedKey(tag.getBoolean("BasAdd.haskey"));
            setLocked(tag.getBoolean("BasAdd.locked"));
        }
    }
}
