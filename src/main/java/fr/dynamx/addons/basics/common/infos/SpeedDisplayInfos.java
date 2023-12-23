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
import fr.dynamx.client.renders.scene.EntityRenderContext;
import fr.dynamx.client.renders.scene.SceneBuilder;
import fr.dynamx.client.renders.scene.SceneGraph;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.utils.DynamXUtils;
import fr.dynamx.utils.client.DynamXRenderUtils;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

@RegisteredSubInfoType(
        name = "SpeedDisplay",
        registries = {SubInfoTypeRegistries.WHEELED_VEHICLES, SubInfoTypeRegistries.HELICOPTER},
        strictName = false
)
public class SpeedDisplayInfos extends BasePart<ModularVehicleInfo> implements IDrawablePart<BaseVehicleEntity<?>, ModularVehicleInfo> {
    @PackFileProperty(configNames = "Rotation", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.rotation")
    protected Vector3f rotation;

    @PackFileProperty(configNames = "Font", required = false)
    protected String font = BasicsAddon.ID + ":e";

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
    public void addToSceneGraph(ModularVehicleInfo packInfo, SceneBuilder<BaseVehicleEntity<?>, ModularVehicleInfo> sceneBuilder) {
        if (nodeDependingOnName != null) {
            sceneBuilder.addNode(this, nodeDependingOnName, getNodeName());
        } else {
            sceneBuilder.addNode(this, getNodeName());
        }
    }

    @Override
    public SceneGraph<BaseVehicleEntity<?>, ModularVehicleInfo> createSceneGraph(Vector3f modelScale, List<SceneGraph<BaseVehicleEntity<?>, ModularVehicleInfo>> childGraph) {
        if(childGraph != null)
            throw new IllegalArgumentException("SpeedDisplayInfos can't have children parts");
        return new SpeedDisplayNode<>(modelScale, null);
    }

    class SpeedDisplayNode<T extends BaseVehicleEntity<?>, A extends ModularVehicleInfo> extends SceneGraph.Node<T, A> {
        public SpeedDisplayNode(Vector3f scale, List<SceneGraph<T, A>> linkedChilds) {
            super(null, null, scale, linkedChilds);
        }

        @Override
        public void render(@Nullable T entity, EntityRenderContext entityRenderContext, A packInfo) {
            if (entity == null)
                return;
            String speed = "" + DynamXUtils.getSpeed(entity);
            if(SpeedDisplayInfos.this.getRotation() != null) {
                TextUtils.drawText(SpeedDisplayInfos.this.getPosition(), SpeedDisplayInfos.this.getScale(), SpeedDisplayInfos.this.getRotation(), speed, getColor(), getFont());
            }
        }
    }
}