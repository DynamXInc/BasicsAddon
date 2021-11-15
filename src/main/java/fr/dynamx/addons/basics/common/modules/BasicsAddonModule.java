package fr.dynamx.addons.basics.common.modules;

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

import javax.annotation.Nullable;
import java.util.List;

public class BasicsAddonModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
    private BasicsAddonController controller;
    private final BaseVehicleEntity<?> entity;
    private boolean locked;

    private final BasicsAddonInfos infos;
    private boolean sirenOn;
    private boolean playKlaxon;
    private boolean headLightsOn;

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

    public boolean hasHeadLights() {
        return infos != null && infos.headLightsSource != 0;
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
        if (controller != null) {
            controller.updateSiren();
        }
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

    public boolean isHeadLightsOn() {
        return headLightsOn;
    }

    public void setHeadLightsOn(boolean headLightsOn) {
        this.headLightsOn = headLightsOn;
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
