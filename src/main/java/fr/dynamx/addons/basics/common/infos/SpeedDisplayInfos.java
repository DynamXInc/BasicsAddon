package fr.dynamx.addons.basics.common.infos;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.utils.TextUtils;
import fr.dynamx.api.contentpack.object.part.BasePart;
import fr.dynamx.api.contentpack.object.part.IDrawablePart;
import fr.dynamx.api.contentpack.registry.DefinitionType;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.contentpack.registry.RegisteredSubInfoType;
import fr.dynamx.api.contentpack.registry.SubInfoTypeRegistries;
import fr.dynamx.client.renders.scene.BaseRenderContext;
import fr.dynamx.client.renders.scene.IRenderContext;
import fr.dynamx.client.renders.scene.SceneBuilder;
import fr.dynamx.client.renders.scene.node.SceneNode;
import fr.dynamx.client.renders.scene.node.SimpleNode;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.utils.DynamXUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@RegisteredSubInfoType(
        name = "SpeedDisplay",
        registries = {SubInfoTypeRegistries.WHEELED_VEHICLES, SubInfoTypeRegistries.HELICOPTER},
        strictName = false
)
public class SpeedDisplayInfos extends BasePart<ModularVehicleInfo> implements IDrawablePart<ModularVehicleInfo> {
    @Getter
    @Setter
    @PackFileProperty(configNames = "Rotation", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.rotation")
    protected Vector3f rotation;

    @Getter
    @Setter
    @PackFileProperty(configNames = "Font", required = false)
    protected String font = BasicsAddon.ID + ":e";

    @Getter
    @Setter
    @PackFileProperty(configNames = "Color", description = "common.color", required = false)
    protected int[] color = new int[]{10, 10, 10};

    @PackFileProperty(configNames = "DependsOnNode", required = false, description = "common.DependsOnNode")
    protected String nodeDependingOnName;

    public SpeedDisplayInfos(ModularVehicleInfo owner, String partName) {
        super(owner, partName);
    }

    @Override
    public void appendTo(ModularVehicleInfo owner) {
        owner.addSubProperty(this);
    }

    @Override
    public String getName() {
        return "PartShape named " + getPartName() + " in " + getOwner().getName();
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
            throw new IllegalArgumentException("SpeedDisplayInfos can't have children parts");
        return (SceneNode) new SpeedDisplayNode(modelScale, null);
    }

    class SpeedDisplayNode extends SimpleNode<BaseRenderContext.EntityRenderContext, ModularVehicleInfo> {
        public SpeedDisplayNode(Vector3f scale, List<SceneNode<BaseRenderContext.EntityRenderContext, ModularVehicleInfo>> linkedChilds) {
            super(null, null, scale, linkedChilds);
        }

        @Override
        public void render(BaseRenderContext.EntityRenderContext entityRenderContext, ModularVehicleInfo info) {
            if (!(entityRenderContext.getEntity() instanceof BaseVehicleEntity))
                return;
            String speed = "" + DynamXUtils.getSpeed((BaseVehicleEntity<?>) entityRenderContext.getEntity());
            TextUtils.drawText(SpeedDisplayInfos.this.getPosition(), SpeedDisplayInfos.this.getScale(), SpeedDisplayInfos.this.getRotation(), speed, getColor(), getFont());
        }
    }
}