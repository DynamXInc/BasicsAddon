package fr.dynamx.addons.basics.common.infos;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.SpeedDisplayModule;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoType;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.api.contentpack.registry.DefinitionType;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.entities.modules.ModuleListBuilder;
import fr.dynamx.common.contentpack.loader.ModularVehicleInfoBuilder;
import fr.dynamx.common.entities.BaseVehicleEntity;

public class SpeedDisplayInfos implements ISubInfoType<ModularVehicleInfoBuilder> {

    private final ISubInfoTypeOwner<ModularVehicleInfoBuilder> owner;

    @PackFileProperty(configNames = "Position", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F_INVERSED_Y, description = "common.position")
    protected Vector3f position;

    @PackFileProperty(configNames = "Size", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.size")
    protected Vector3f size;

    @PackFileProperty(configNames = "Rotation", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.rotation")
    protected Vector3f rotation;

    @PackFileProperty(configNames = "Font", type = DefinitionType.DynamXDefinitionTypes.STRING, required = false)
    protected String font = BasicsAddon.ID + ":e";

    @PackFileProperty(configNames = "Color", type = DefinitionType.DynamXDefinitionTypes.INT_ARRAY, description = "common.color", required = false)
    protected int[] color = new int[] {10, 10, 10};

    public SpeedDisplayInfos(ISubInfoTypeOwner<ModularVehicleInfoBuilder> owner) {
        this.owner = owner;
    }

    @Override
    public void appendTo(ModularVehicleInfoBuilder owner) {
        owner.addSubProperty(this);
    }

    @Override
    public String getName() {
        return owner.getPackName() + " of " + owner.getName();
    }

    @Override
    public String getPackName() {
        return owner.getPackName();
    }

    public ISubInfoTypeOwner<ModularVehicleInfoBuilder> getOwner() {
        return owner;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getSize() {
        return size;
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
}