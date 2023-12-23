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
import fr.dynamx.api.entities.VehicleEntityProperties;
import fr.dynamx.client.renders.scene.EntityRenderContext;
import fr.dynamx.client.renders.scene.SceneBuilder;
import fr.dynamx.client.renders.scene.SceneGraph;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.modules.engines.CarEngineModule;
import fr.dynamx.utils.DynamXUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

@RegisteredSubInfoType(
        name = "TextInfo",
        registries = {SubInfoTypeRegistries.WHEELED_VEHICLES},
        strictName = false
)
public class DashboardTextInfos extends BasePart<ModularVehicleInfo> implements IDrawablePart<BaseVehicleEntity<?>, ModularVehicleInfo> {
    @PackFileProperty(configNames = "Rotation", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.rotation")
    protected Vector3f rotation;

    @PackFileProperty(configNames = "Font", required = false)
    protected String font = BasicsAddon.ID + ":e";

    @PackFileProperty(configNames = "DetailToShow", required = false, defaultValue = "GEAR")
    protected String DetailToShow;
    @PackFileProperty(configNames = "CarStartedReact", required = false, defaultValue = "false", type = DefinitionType.DynamXDefinitionTypes.BOOL)
    protected boolean CarStartedReact;

    @PackFileProperty(configNames = "Color", description = "common.color", required = false)
    protected int[] color = new int[]{10, 10, 10};

    @PackFileProperty(configNames = "DependsOnNode", required = false, description = "common.DependsOnNode")
    protected String nodeDependingOnName;

    public DashboardTextInfos(ModularVehicleInfo owner, String partName) {
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

    public boolean isCarStartedReact() {
        return CarStartedReact;
    }

    public String getFont() {
        return font;
    }

    public String getDetailToShow() {
        return DetailToShow;
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
            throw new IllegalArgumentException("TextInfo can't have children parts");
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
            if(DashboardTextInfos.this.getRotation() != null) {
                if(entity.getModuleByType(CarEngineModule.class).getPhysicsHandler().getEngine().isStarted() && DashboardTextInfos.this.isCarStartedReact()) {
                    if(Objects.equals(DashboardTextInfos.this.getDetailToShow(), "GEAR")) {
                        String value = "";
                        float gear = entity.getModuleByType(CarEngineModule.class).getEngineProperty(VehicleEntityProperties.EnumEngineProperties.ACTIVE_GEAR);
                        if(gear == 0) {
                            value = "N";
                        } else if(gear == -1) {
                            value = "R" + (int) Math.abs(gear);
                        } else {
                            value = "D" + (int) gear;
                        }
                        TextUtils.drawText(DashboardTextInfos.this.getPosition(), DashboardTextInfos.this.getScale(), DashboardTextInfos.this.getRotation(), value, getColor(), getFont());
                    }
                    if(Objects.equals(DashboardTextInfos.this.getDetailToShow(), "SPEEDLIMITOR")) {
                        int value = Math.round(entity.getModuleByType(CarEngineModule.class).getSpeedLimit());
                        if(value != 0 & value < 10000000) {
                            TextUtils.drawText(DashboardTextInfos.this.getPosition(), DashboardTextInfos.this.getScale(), DashboardTextInfos.this.getRotation(), String.valueOf(value), getColor(), getFont());
                        }
                    }
                    if(Objects.equals(DashboardTextInfos.this.getDetailToShow(), "SPEED")) {
                        int value = DynamXUtils.getSpeed(entity);
                        TextUtils.drawText(DashboardTextInfos.this.getPosition(), DashboardTextInfos.this.getScale(), DashboardTextInfos.this.getRotation(), String.valueOf(value), getColor(), getFont());
                    }
                }
            }
        }
    }
}