package fr.dynamx.addons.basics.common.infos;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.LicensePlateModule;
import fr.dynamx.addons.basics.utils.TextUtils;
import fr.dynamx.api.contentpack.object.part.BasePart;
import fr.dynamx.api.contentpack.object.part.IDrawablePart;
import fr.dynamx.api.contentpack.object.render.IModelPackObject;
import fr.dynamx.api.contentpack.registry.*;
import fr.dynamx.api.entities.modules.ModuleListBuilder;
import fr.dynamx.client.renders.scene.BaseRenderContext;
import fr.dynamx.client.renders.scene.IRenderContext;
import fr.dynamx.client.renders.scene.SceneBuilder;
import fr.dynamx.client.renders.scene.node.SceneNode;
import fr.dynamx.client.renders.scene.node.SimpleNode;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.PackPhysicsEntity;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

@RegisteredSubInfoType(
        name = "ImmatriculationPlate",
        registries = {SubInfoTypeRegistries.WHEELED_VEHICLES, SubInfoTypeRegistries.HELICOPTER},
        strictName = false
)
public class LicensePlateInfos extends BasePart<ModularVehicleInfo> implements IDrawablePart<ModularVehicleInfo> {
    @Getter
    @Setter
    @PackFileProperty(configNames = "Rotation", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.rotation")
    protected Vector3f rotation = new Vector3f();

    @Getter
    @Setter
    @PackFileProperty(configNames = "Pattern", required = false)
    protected String pattern = "aa-111-aa";

    @Getter
    @Setter
    @PackFileProperty(configNames = "Font", required = false)
    protected String font = BasicsAddon.ID + ":e";

    @Getter
    @Setter
    @PackFileProperty(configNames = "Color", description = "common.color", required = false)
    protected int[] color = new int[]{10, 10, 10};

    @Getter
    @Setter
    @PackFileProperty(configNames = "LineSpacing", required = false)
    protected float lineSpacing = 0.0F;

    @PackFileProperty(configNames = "DependsOnNode", required = false, description = "common.DependsOnNode")
    protected String nodeDependingOnName;

    @IPackFilePropertyFixer.PackFilePropertyFixer(registries = SubInfoTypeRegistries.WHEELED_VEHICLES)
    public static final IPackFilePropertyFixer PROPERTY_FIXER = (object, key, value) -> {
        if (key.startsWith("Immatriculation")) {
            key = key.replaceAll("Immatriculation", "");
            IPackFilePropertyFixer.FixResult superr = BasePart.PROPERTY_FIXER.fixInputField(object, key, value);
            return superr != null ? superr : new IPackFilePropertyFixer.FixResult(key, true);
        }
        return null;
    };

    public LicensePlateInfos(ModularVehicleInfo owner, String partName) {
        super(owner, partName);
    }

    @Override
    public void appendTo(ModularVehicleInfo owner) {
        owner.addPart(this);
    }

    @Override
    public void addModules(PackPhysicsEntity<?, ?> entity, ModuleListBuilder modules) {
        if (modules.hasModuleOfClass(LicensePlateModule.class)) { //Module yet added
            modules.getByClass(LicensePlateModule.class).addInformation(this);
        } else { //Module not yet added
            modules.add(new LicensePlateModule(this));
        }
    }

    @Override
    public String getName() {
        return "PlateInfo named " + getPartName() + " in " + getOwner().getName();
    }

    @Override
    public String getObjectName() {
        return null;
    }

    @Override
    public String[] getRenderedParts() {
        return new String[0];
    }

    @Override
    public String getNodeName() {
        return getPartName();
    }

    @Override
    public void addToSceneGraph(ModularVehicleInfo packInfo, SceneBuilder<IRenderContext, ModularVehicleInfo> sceneBuilder) {
        if (nodeDependingOnName != null) {
            sceneBuilder.addNode(packInfo, this, nodeDependingOnName);
        } else {
            sceneBuilder.addNode(packInfo, this);
        }
    }

    @Override
    public SceneNode<IRenderContext, ModularVehicleInfo> createSceneGraph(Vector3f modelScale, List<SceneNode<IRenderContext, ModularVehicleInfo>> childGraph) {
        if (childGraph != null)
            throw new IllegalArgumentException("LicensePlateInfos can't have children parts");
        return (SceneNode) new LicensePlateNode(modelScale, null);
    }

    class LicensePlateNode extends SimpleNode<BaseRenderContext.EntityRenderContext, ModularVehicleInfo> {
        public LicensePlateNode(Vector3f scale, List<SceneNode<BaseRenderContext.EntityRenderContext, ModularVehicleInfo>> linkedChilds) {
            super(null, null, scale, linkedChilds);
        }

        @Override
        public void render(BaseRenderContext.EntityRenderContext entityRenderContext, ModularVehicleInfo info) {
            if (entityRenderContext.getEntity() == null)
                return;
            LicensePlateModule module = entityRenderContext.getEntity().getModuleByType(LicensePlateModule.class);
            TextUtils.drawText(
                    LicensePlateInfos.this.getPosition(),
                    LicensePlateInfos.this.getScale(),
                    LicensePlateInfos.this.getRotation(),
                    module.getPlate(),
                    getColor(),
                    getFont(),
                    getLineSpacing());
        }
    }
}