package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.client.BasicsAddonController;
import fr.dynamx.addons.basics.common.LightHolder;
import fr.dynamx.addons.basics.common.infos.BasicsAddonInfos;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.api.network.sync.SimulationHolder;
import fr.dynamx.api.network.sync.v3.SynchronizationRules;
import fr.dynamx.api.network.sync.v3.SynchronizedEntityVariable;
import fr.dynamx.client.ClientProxy;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.network.sync.v3.DynamXSynchronizedVariables;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import fr.dynamx.utils.optimization.Vector3fPool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BasicsAddonModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
    private BasicsAddonController controller;
    private final BaseVehicleEntity<?> entity;
    private final BasicsAddonInfos infos;

    public static final ResourceLocation NAME = new ResourceLocation(BasicsAddon.ID, "state");
    private final SynchronizedEntityVariable<Integer> state = new SynchronizedEntityVariable<>(SynchronizationRules.CONTROLS_TO_SPECTATORS, null, 0, "state");



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
        return (this.state.get()&1)==1;
    }

    public void playKlaxon(boolean playKlaxon) {
        if (playKlaxon)
            this.state.set(this.state.get()|1);
        else
            this.state.set(this.state.get()&~1);

    }

    public boolean isSirenOn() {
        return (this.state.get()&2)==2;
    }

    public void setSirenOn(boolean sirenOn) {
        if (sirenOn){
            this.state.set(this.state.get()|2);
        }else{
            this.state.set(this.state.get()&~2);
        }
    }

    public boolean isBeaconsOn() {
        return (this.state.get()&4)==4;
    }

    public void setBeaconsOn(boolean beaconsOn) {
        if (beaconsOn){
            this.state.set(this.state.get()|4);
        }else{
            this.state.set(this.state.get()&~4);
        }
    }

    public boolean hasLinkedKey() {
        return (this.state.get()&8)==8;
    }

    public void setHasLinkedKey(boolean hasLinkedKey) {
        if (hasLinkedKey){
            this.state.set(this.state.get()|8);
        }else{
            this.state.set(this.state.get()&~8);
        }
    }

    public boolean isLocked() {
        return (this.state.get()&16)==16;
    }

    public void setLocked(boolean locked) {
        if (locked){
            this.state.set(this.state.get()|16);
        }else{
            this.state.set(this.state.get()&~16);
        }
    }

    public boolean isHeadLightsOn() {
        return (this.state.get()&32)==32;
    }

    public void setHeadLightsOn(boolean headLightsOn) {
        if (headLightsOn){
            this.state.set(this.state.get()|32);
        }else{
            this.state.set(this.state.get()&~32);
        }
    }

    public boolean hasTurnSignals() {
        return infos != null && infos.turnLeftLightSource != 0 && infos.turnRightLightSource != 0;
    }

    public boolean isTurnSignalLeftOn() {
        return (this.state.get()&64)==64;
    }

    public void setTurnSignalLeftOn(boolean turnSignalLeftOn) {
        System.out.println("setTurnSignalLeftOn "+turnSignalLeftOn);
        System.out.println(isTurnSignalRightOn());
        System.out.println(isTurnSignalLeftOn());
        if (turnSignalLeftOn){
            this.state.set(this.state.get()|64);
        }else{
            this.state.set(this.state.get()&~64);
        }
    }

    public boolean isTurnSignalRightOn() {
        return (this.state.get()&128)==128;
    }

    public void setTurnSignalRightOn(boolean turnSignalRightOn) {
        System.out.println("setTurnSignalRightOn "+turnSignalRightOn);
        System.out.println(isTurnSignalRightOn());
        System.out.println(isTurnSignalLeftOn());
        if (turnSignalRightOn){
            this.state.set(this.state.get()|128);
        }else{
            this.state.set(this.state.get()&~128);
        }
    }

    public BasicsAddonInfos getInfos() {
        return infos;
    }



    @Override
    public void addSynchronizedVariables(Side side, SimulationHolder simulationHolder) {
        this.entity.getSynchronizer().registerVariable(DynamXSynchronizedVariables.CONTROLS, this.state);
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
            setHasLinkedKey(tag.getBoolean("BasAdd.haskey"));
            setLocked(tag.getBoolean("BasAdd.locked"));
        }
    }
}
