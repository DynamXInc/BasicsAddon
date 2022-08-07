package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.client.InteractionKeyController;
import fr.dynamx.addons.basics.common.network.InteractionKeySV;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.api.network.sync.SimulationHolder;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class InteractionKeyModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
    private final BaseVehicleEntity<?> entity;
    private InteractionKeyController controller;
    private boolean activate;

    public InteractionKeyModule(BaseVehicleEntity<?> entity) {
        this.entity = entity;
        if(entity.world.isRemote){
            controller = new InteractionKeyController(entity, this);
        }
    }

    public boolean isActivateOn() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public IVehicleController createNewController() {
        return controller;
    }

    @Override
    public void addSynchronizedVariables(Side side, SimulationHolder simulationHolder, List<ResourceLocation> variables) {
        if (simulationHolder.ownsControls(side))
            variables.add(InteractionKeySV.NAME);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setBoolean("bas_interactionkey", activate);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        activate = tag.getBoolean("bas_interactionkey");
    }

}