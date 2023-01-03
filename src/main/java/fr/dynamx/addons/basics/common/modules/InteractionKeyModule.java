package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.client.InteractionKeyController;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.api.network.sync.SimulationHolder;
import fr.dynamx.api.network.sync.v3.SynchronizationRules;
import fr.dynamx.api.network.sync.v3.SynchronizedEntityVariable;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class InteractionKeyModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
    private final BaseVehicleEntity<?> entity;
    private InteractionKeyController controller;
    public static final ResourceLocation NAME = new ResourceLocation(BasicsAddon.ID, "activate");
    private final SynchronizedEntityVariable<Boolean> activate= new SynchronizedEntityVariable<>(SynchronizationRules.SERVER_TO_CLIENTS, null, false, "activate");

    public InteractionKeyModule(BaseVehicleEntity<?> entity) {
        this.entity = entity;
        if(entity.world.isRemote){
            controller = new InteractionKeyController(entity, this);
        }
    }

    public boolean isActivateOn() {
        return activate.get();
    }

    public void setActivate(boolean activate) {
        this.activate.set(activate);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public IVehicleController createNewController() {
        return controller;
    }

    @Override
    public void addSynchronizedVariables(Side side, SimulationHolder simulationHolder) {
        entity.getSynchronizer().registerVariable(NAME,activate);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setBoolean("bas_interactionkey", isActivateOn());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        setActivate(tag.getBoolean("bas_interactionkey"));
    }

}