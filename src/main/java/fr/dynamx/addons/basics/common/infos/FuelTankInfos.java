package fr.dynamx.addons.basics.common.infos;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.FuelTankModule;
import fr.dynamx.addons.basics.common.modules.ImmatriculationPlateModule;
import fr.dynamx.api.contentpack.object.part.InteractivePart;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoType;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.api.contentpack.registry.DefinitionType;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.common.contentpack.loader.ModularVehicleInfoBuilder;
import fr.dynamx.common.contentpack.parts.PartSeat;
import fr.dynamx.common.entities.BaseVehicleEntity;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.List;
import java.util.function.Predicate;

public class FuelTankInfos extends InteractivePart<BaseVehicleEntity<?>, ModularVehicleInfoBuilder> {

    @PackFileProperty(configNames = "TankSize", type = DefinitionType.DynamXDefinitionTypes.FLOAT, defaultValue = "100")
    protected float tankSize;

    @PackFileProperty(configNames = "FuelConsumption", type = DefinitionType.DynamXDefinitionTypes.FLOAT, defaultValue = "1")
    protected float fuelConsumption;


    public FuelTankInfos(ModularVehicleInfoBuilder owner, String partName) {
        super(owner, partName, 0.5f, 0.5f);
    }

    public float getTankSize() {
        return tankSize;
    }

    public float getFuelConsumption() {
        return fuelConsumption;
    }

    @Override
    public void appendTo(ModularVehicleInfoBuilder owner) {
        super.appendTo(owner);
    }

    @Override
    public boolean interact(BaseVehicleEntity<?> entity, EntityPlayer with) {
        //TODO GUI
        return false;
    }

    @Override
    public String getName() {
        return "FuelTank" + " of " + "BlackNite";
    }

    @Override
    public String getPackName() {
        return "FuelTank";
    }

    @Override
    public void addModules(BaseVehicleEntity<?> entity, List<IPhysicsModule<?>> modules, Predicate<Class<? extends IPhysicsModule<?>>> containsModule) {
        if (containsModule.test(FuelTankModule.class)) { //Module yet added
            ((FuelTankModule) modules.stream().filter(iPhysicsModule -> iPhysicsModule.getClass() == FuelTankModule.class).
                    findFirst().get()).setInfo(this);
        } else { //Module not yet added
            modules.add(new FuelTankModule(entity,this));
        }
    }
}