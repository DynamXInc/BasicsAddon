package fr.dynamx.addons.basics.common.infos;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.ImmatriculationPlateModule;
import fr.dynamx.api.contentpack.object.part.BasePart;
import fr.dynamx.api.contentpack.registry.DefinitionType;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.entities.modules.ModuleListBuilder;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfoBuilder;
import fr.dynamx.common.entities.BaseVehicleEntity;

public class ImmatriculationPlateInfos extends BasePart<ModularVehicleInfoBuilder> {
    @PackFileProperty(configNames = "Rotation", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.rotation")
    protected Vector3f rotation;

    @PackFileProperty(configNames = "Pattern", required = false)
    protected String pattern = "aa-111-aa";

    @PackFileProperty(configNames = "Font", required = false)
    protected String font = BasicsAddon.ID + ":e";

    @PackFileProperty(configNames = "Color", description = "common.color", required = false)
    protected int[] color = new int[] {10, 10, 10};

    @PackFileProperty(configNames = "LineSpacing", required = false)
    protected float lineSpacing = 0.0F;

    public ImmatriculationPlateInfos(ModularVehicleInfoBuilder owner, String partName) {
        super(owner, partName);
    }

    @Override
    public void appendTo(ModularVehicleInfoBuilder owner) {
        owner.addSubProperty(this);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public String getFont() {
        return font;
    }

    public int[] getColor() {
        return color;
    }

    public String getPattern() {
        return pattern;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    @Override
    public void addModules(BaseVehicleEntity<?> entity, ModuleListBuilder moduleListBuilder) {
        if (moduleListBuilder.hasModuleOfClass(ImmatriculationPlateModule.class)) { //Module yet added
            moduleListBuilder.getByClass(ImmatriculationPlateModule.class).addInformation(this);
        } else { //Module not yet added
            moduleListBuilder.add(new ImmatriculationPlateModule(this));
        }
    }

    @Override
    public String getName() {
        return "PartShape named " + getPartName() + " in " + getOwner().getName();
    }
}