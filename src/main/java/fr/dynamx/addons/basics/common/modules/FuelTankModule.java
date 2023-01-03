package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.client.FuelTankController;
import fr.dynamx.addons.basics.common.infos.FuelTankInfos;
import fr.dynamx.api.entities.VehicleEntityProperties;
import fr.dynamx.api.entities.modules.IEngineModule;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.api.network.sync.SimulationHolder;
import fr.dynamx.api.network.sync.v3.SynchronizationRules;
import fr.dynamx.api.network.sync.v3.SynchronizedEntityVariable;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.modules.EngineModule;
import fr.dynamx.common.entities.vehicles.CarEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class FuelTankModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
    private final BaseVehicleEntity<?> entity;
    private final FuelTankInfos info;
    private FuelTankController controller;
    public static final ResourceLocation NAME = new ResourceLocation(BasicsAddon.ID, "fuel");
    private final SynchronizedEntityVariable<Float> fuel= new SynchronizedEntityVariable<>(SynchronizationRules.SERVER_TO_CLIENTS, null, Float.MAX_VALUE, "fuel");

    public FuelTankModule(BaseVehicleEntity<?> entity, FuelTankInfos info) {
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
    public void addSynchronizedVariables(Side side, SimulationHolder simulationHolder) {
        entity.getSynchronizer().registerVariable(NAME,fuel);
    }

    @Override
    public void updateEntity() {
        if (entity instanceof CarEntity && getFuel() > 0) {
            CarEntity<?> carEntity = (CarEntity<?>) entity;
            IEngineModule<?> engine = carEntity.getEngine();
            if (engine instanceof EngineModule) {
                //Rpm is capped between 0 and 1
                float RPM = ((EngineModule) engine).getEngineProperty(VehicleEntityProperties.EnumEngineProperties.REVS);
                setFuel((float) (getFuel() - (RPM * (getInfo().getFuelConsumption() / 120) *
                        ((((EngineModule) engine).isAccelerating() ? 1.1 : 0.9)))));
            }
        }
    }
}