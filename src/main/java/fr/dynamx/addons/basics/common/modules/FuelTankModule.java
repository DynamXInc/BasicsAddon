package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.client.FuelTankController;
import fr.dynamx.addons.basics.common.infos.FuelTankInfos;
import fr.dynamx.api.entities.VehicleEntityProperties;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.api.network.sync.EntityVariable;
import fr.dynamx.api.network.sync.SynchronizationRules;
import fr.dynamx.api.network.sync.SynchronizedEntityVariable;
import fr.dynamx.common.entities.PackPhysicsEntity;
import fr.dynamx.common.entities.modules.engines.CarEngineModule;
import fr.dynamx.common.entities.vehicles.CarEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SynchronizedEntityVariable.SynchronizedPhysicsModule(modid = BasicsAddon.ID)
public class FuelTankModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
    private final PackPhysicsEntity<?, ?> entity;
    private final FuelTankInfos info;
    private FuelTankController controller;
    @SynchronizedEntityVariable(name = "fuel")
    private final EntityVariable<Float> fuel = new EntityVariable<>(SynchronizationRules.SERVER_TO_CLIENTS, Float.MAX_VALUE);

    public FuelTankModule(PackPhysicsEntity<?, ?> entity, FuelTankInfos info) {
        this.info = info;
        this.entity = entity;
        if (entity.world.isRemote) {
            controller = new FuelTankController(this);
        }
        setFuel(info.getTankSize());
    }

    public float getFuel() {
        return fuel.get();
    }

    public void setFuel(float fuel) {
        this.fuel.set(Math.max(Math.min(fuel, info.getTankSize()), 0));
    }

    public FuelTankInfos getInfo() {
        return info;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public IVehicleController createNewController() {
        return controller;
    }

    @Override
    public void updateEntity() {
        if (entity instanceof CarEntity && getFuel() > 0) {
            CarEntity<?> carEntity = (CarEntity<?>) entity;
            CarEngineModule engine = carEntity.getModuleByType(CarEngineModule.class);
            if (engine != null) {
                //Rpm is capped between 0 and 1
                float RPM = engine.getEngineProperty(VehicleEntityProperties.EnumEngineProperties.REVS);
                setFuel((float) (getFuel() - (RPM * (getInfo().getFuelConsumption() / 120) *
                        ((engine.isAccelerating() ? 1.1 : 0.9)))));
            }
        }
    }
}