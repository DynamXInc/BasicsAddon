package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.client.InteractionKeyController;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.api.network.sync.EntityVariable;
import fr.dynamx.api.network.sync.SynchronizationRules;
import fr.dynamx.api.network.sync.SynchronizedEntityVariable;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SynchronizedEntityVariable.SynchronizedPhysicsModule(modid = BasicsAddon.ID)
public class InteractionKeyModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
    private final BaseVehicleEntity<?> entity;
    private InteractionKeyController controller;

    @SynchronizedEntityVariable(name = "activate")
    private final EntityVariable<Boolean> activate = new EntityVariable<>(SynchronizationRules.SERVER_TO_CLIENTS, false);


    public InteractionKeyModule(BaseVehicleEntity<?> entity) {
        this.entity = entity;
        if (entity.world.isRemote) {
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
    public void writeToNBT(NBTTagCompound tag) {
        tag.setBoolean("bas_interactionkey", isActivateOn());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        setActivate(tag.getBoolean("bas_interactionkey"));
    }

}