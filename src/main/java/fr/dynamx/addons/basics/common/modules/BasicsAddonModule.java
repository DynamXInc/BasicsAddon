package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.client.BasicsAddonController;
import fr.dynamx.addons.basics.common.LightHolder;
import fr.dynamx.addons.basics.common.infos.BasicsAddonInfos;
import fr.dynamx.addons.basics.common.network.BasicsAddonSV;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.api.network.sync.SimulationHolder;
import fr.dynamx.client.ClientProxy;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import fr.dynamx.utils.optimization.Vector3fPool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BasicsAddonModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
    private BasicsAddonController controller;
    private final BaseVehicleEntity<?> entity;

    private boolean hasLinkedKey;
    private boolean locked;

    private final BasicsAddonInfos infos;
    private boolean sirenOn;
    private boolean beaconsOn;
    private boolean playKlaxon;
    private boolean headLightsOn;
    private boolean turnSignalLeftOn;
    private boolean turnSignalRightOn;

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
        if (playKlaxon && entity.world.isRemote && hasKlaxon())
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
        return playKlaxon;
    }

    public void playKlaxon(boolean playKlaxon) {
        this.playKlaxon = playKlaxon;
    }

    public boolean isSirenOn() {
        return sirenOn;
    }

    public void setSirenOn(boolean sirenOn) {
        this.sirenOn = sirenOn;
    }

    public boolean isBeaconsOn() {
        return beaconsOn;
    }

    public void setBeaconsOn(boolean beaconsOn) {
        this.beaconsOn = beaconsOn;
    }

    public boolean hasLinkedKey() {
        return hasLinkedKey;
    }

    public void setHasLinkedKey(boolean hasLinkedKey) {
        this.hasLinkedKey = hasLinkedKey;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isHeadLightsOn() {
        return headLightsOn;
    }

    public void setHeadLightsOn(boolean headLightsOn) {
        this.headLightsOn = headLightsOn;
    }

    public boolean hasTurnSignals() {
        return infos != null && infos.turnLeftLightSource != 0 && infos.turnRightLightSource != 0;
    }

    public boolean isTurnSignalLeftOn() {
        return turnSignalLeftOn;
    }

    public void setTurnSignalLeftOn(boolean turnSignalLeftOn) {
        this.turnSignalLeftOn = turnSignalLeftOn;
    }

    public boolean isTurnSignalRightOn() {
        return turnSignalRightOn;
    }

    public void setTurnSignalRightOn(boolean turnSignalRightOn) {
        this.turnSignalRightOn = turnSignalRightOn;
    }

    public BasicsAddonInfos getInfos() {
        return infos;
    }

    @Override
    public void addSynchronizedVariables(Side side, SimulationHolder simulationHolder, List<ResourceLocation> variables) {
        if (simulationHolder.ownsControls(side))
            variables.add(BasicsAddonSV.NAME);
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
        if(tag.hasKey("BasAddon.vals", Constants.NBT.TAG_BYTE)) {
            byte vars = tag.getByte("BasAddon.vals");
            setSirenOn((vars & 2) == 2);
            setHeadLightsOn((vars & 4) == 4);
            setTurnSignalLeftOn((vars & 8) == 8);
            setTurnSignalRightOn((vars & 16) == 16);
            setBeaconsOn((vars & 32) == 32);
            setLocked((vars & 64) == 64);
        } else { //backward compatibility
            hasLinkedKey = tag.getBoolean("BasAdd.haskey");
            locked = tag.getBoolean("BasAdd.locked");
        }
    }
}
