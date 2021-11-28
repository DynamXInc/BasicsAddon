package fr.dynamx.addons.basics.common.infos;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.ImmatriculationPlateModule;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoType;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.api.contentpack.registry.DefinitionType;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.common.contentpack.loader.ModularVehicleInfoBuilder;
import fr.dynamx.common.entities.BaseVehicleEntity;

import java.awt.*;
import java.util.List;
import java.util.function.Predicate;

public class ImmatriculationPlateInfos implements ISubInfoType<ModularVehicleInfoBuilder> {

    private final ISubInfoTypeOwner<ModularVehicleInfoBuilder> owner;

    @PackFileProperty(configNames = "ImmatriculationPosition", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F_INVERSED_Y, description = "common.position")
    protected Vector3f immatriculationPosition;

    @PackFileProperty(configNames = "ImmatriculationSize", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.size")
    protected Vector3f immatriculationSize;

    @PackFileProperty(configNames = "ImmatriculationRotation", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.rotation")
    protected Vector3f immatriculationRotation;

    @PackFileProperty(configNames = "ImmatriculationPattern", required = false)
    protected String immatriculationPattern = "aa-111-aa";

    @PackFileProperty(configNames = "ImmatriculationFont", type = DefinitionType.DynamXDefinitionTypes.STRING, required = false)
    protected String font = BasicsAddon.ID + ":e";

    @PackFileProperty(configNames = "ImmatriculationColor", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.color", required = false)
    protected Vector3f ImmatriculationColor = new Vector3f(Color.black.getRed(), Color.black.getGreen(), Color.black.getBlue());

    public ImmatriculationPlateInfos(ISubInfoTypeOwner<ModularVehicleInfoBuilder> owner) {
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

    public Vector3f getImmatriculationPosition() {
        return immatriculationPosition;
    }

    public Vector3f getImmatriculationSize() {
        return immatriculationSize;
    }

    public Vector3f getImmatriculationRotation() {
        return immatriculationRotation;
    }

    public String getFont() {
        return font;
    }

    public Vector3f getImmatriculationColor() {
        return ImmatriculationColor;
    }

    public String getImmatriculationPattern() {
        return immatriculationPattern;
    }

    @Override
    public void addModules(BaseVehicleEntity<?> entity, List<IPhysicsModule<?>> modules, Predicate<Class<? extends IPhysicsModule<?>>> containsModule) {
        if (containsModule.test(ImmatriculationPlateModule.class)) { //Module yet added
            ((ImmatriculationPlateModule) modules.stream().filter(iPhysicsModule -> iPhysicsModule.getClass() == ImmatriculationPlateModule.class).
                    findFirst().get()).addInformation(this);
        } else { //Module not yet added
            modules.add(new ImmatriculationPlateModule(this));
        }
    }
}