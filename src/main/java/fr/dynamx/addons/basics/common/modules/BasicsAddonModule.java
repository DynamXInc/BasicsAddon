package fr.dynamx.addons.basics.common.modules;

import com.sun.istack.internal.Nullable;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.client.BasicsAddonController;
import fr.dynamx.addons.basics.common.LightHolder;
import fr.dynamx.addons.basics.common.infos.BasicsAddonInfos;
import fr.dynamx.addons.basics.common.network.SoundsSynchronizedVariable;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.api.network.sync.SimulationHolder;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BasicsAddonModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
    private BasicsAddonController controller;
    private final BaseVehicleEntity<?> entity;
    private boolean locked;

    private final BasicsAddonInfos infos;
    private boolean sirenOn;
    private boolean playKlaxon;
    private int fuelLevel;

    public BasicsAddonModule(BaseVehicleEntity<?> entity, BasicsAddonInfos infos) {
        this.entity = entity;
        this.infos = infos;
        if (entity.world.isRemote && (hasSiren() || hasKlaxon()))
            controller = new BasicsAddonController(entity, this, fr.dynamx.addons.basics.BasicsAddon.betterLightsLoaded ? new LightHolder() : null);
    }

    public boolean hasKlaxon() {
        return infos != null && infos.klaxonSound != null;
    }

    public boolean hasSiren() {
        return infos != null && infos.sirenSound != null;
    }

    @Override
    public boolean listenEntityUpdates(Side side) {
        return side.isClient() && (hasKlaxon() || hasSiren());
    }

    @Override
    public void updateEntity() {
        if (playKlaxon) {
            //test playKlaxon = false;
            if (entity.world.isRemote && hasKlaxon()) {
                entity.world.playSound(entity.posX, entity.posY, entity.posZ, BasicsAddon.soundMap.get(infos.klaxonSound), SoundCategory.PLAYERS, 1, 1, true);
            }
        }
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

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(int fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public BasicsAddonInfos getInfos() {
        return infos;
    }

    @Override
    public void addSynchronizedVariables(Side side, SimulationHolder simulationHolder, List<ResourceLocation> variables) {
        if (simulationHolder == SimulationHolder.SERVER_SP ? side.isClient() : side.isServer() || simulationHolder.isMe(side))
            variables.add(SoundsSynchronizedVariable.NAME);
    }
}
