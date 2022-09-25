package fr.dynamx.addons.basics.common.infos;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.SpeedDisplayModule;
import fr.dynamx.api.contentpack.object.part.BasePart;
import fr.dynamx.api.contentpack.registry.DefinitionType;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.entities.modules.ModuleListBuilder;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfoBuilder;
import fr.dynamx.common.entities.BaseVehicleEntity;

public class SpeedDisplayInfos extends BasePart<ModularVehicleInfoBuilder> {
    @PackFileProperty(configNames = "Rotation", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.rotation")
    protected Vector3f rotation;

    @PackFileProperty(configNames = "Font", required = false)
    protected String font = BasicsAddon.ID + ":e";

    @PackFileProperty(configNames = "Color", description = "common.color", required = false)
    protected int[] color = new int[] {10, 10, 10};

    public SpeedDisplayInfos(ModularVehicleInfoBuilder owner, String partName) {
        super(owner, partName);
    }

    @Override
    public void appendTo(ModularVehicleInfoBuilder owner) {
        owner.addSubProperty(this);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public int[] getColor() {
        return color;
    }

    public String getFont() {
        return font;
    }

    @Override
    public void addModules(BaseVehicleEntity<?> entity, ModuleListBuilder moduleListBuilder) {
        if (moduleListBuilder.hasModuleOfClass(SpeedDisplayModule.class)) { //Module yet added
            moduleListBuilder.getByClass(SpeedDisplayModule.class).addInformation(this);
        } else { //Module not yet added
            moduleListBuilder.add(new SpeedDisplayModule(this));
        }
    }

    @Override
    public String getName() {
        return "PartShape named " + getPartName() + " in " + getOwner().getName();
    }
}