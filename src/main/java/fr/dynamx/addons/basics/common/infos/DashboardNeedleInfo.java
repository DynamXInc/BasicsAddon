package fr.dynamx.addons.basics.common.infos;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.utils.TextUtils;
import fr.dynamx.api.contentpack.object.part.BasePart;
import fr.dynamx.api.contentpack.object.part.IDrawablePart;
import fr.dynamx.api.contentpack.registry.DefinitionType;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.contentpack.registry.RegisteredSubInfoType;
import fr.dynamx.api.contentpack.registry.SubInfoTypeRegistries;
import fr.dynamx.api.entities.VehicleEntityProperties;
import fr.dynamx.client.renders.model.renderer.DxModelRenderer;
import fr.dynamx.client.renders.scene.EntityRenderContext;
import fr.dynamx.client.renders.scene.SceneBuilder;
import fr.dynamx.client.renders.scene.SceneGraph;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.modules.engines.CarEngineModule;
import fr.dynamx.utils.DynamXUtils;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

@RegisteredSubInfoType(name = "DashboardNeedle", registries = SubInfoTypeRegistries.WHEELED_VEHICLES, strictName = false)
public class DashboardNeedleInfo extends BasePart<ModularVehicleInfo> implements IDrawablePart<BaseVehicleEntity<?>, ModularVehicleInfo> {
    @PackFileProperty(configNames = "Rotation", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F)
    protected Vector3f rotation;

    @PackFileProperty(configNames = "ObjectName", required = false, defaultValue = "speedneedle")
    protected String objectName;

    @PackFileProperty(configNames = "NeedleMaxTurn", required = false, defaultValue = "140", description = "common.NeedleMaxTurn")
    protected int needleMaxTurn;

    @PackFileProperty(configNames = "DashboardMaxValue", required = false, defaultValue = "140", description = "common.DashboardMaxSpeed")
    protected int dashboardMaxValue;

    @PackFileProperty(configNames = "DependsOnNode", required = false, description = "common.DependsOnNode")
    protected String nodeDependingOnName;

    @PackFileProperty(configNames = "NeedleType", required = false, defaultValue = "SPEED", description = "common.NeedleType")
    protected EnumDashboardNeedleType needleType;

    @Override
    public String getNodeName() {
        return getPartName();
    }

    @Override
    public String getObjectName() {
        return this.objectName;
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
        if (childGraph != null) throw new IllegalArgumentException("DashboardNeedleInfo can't have children parts");
        return new SpeedNeedleNode<>(modelScale, null);
    }

    @Override
    public String getName() {
        return "PartShape named " + getPartName() + " in " + Objects.requireNonNull(getOwner()).getName();
    }

    public int getNeedleMaxTurn() {
        return needleMaxTurn;
    }

    public int getDashboardMaxValue() {
        return dashboardMaxValue;
    }

    public EnumDashboardNeedleType getNeedleType() {
        return needleType;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public String getNodeDependingOnName() {
        return this.nodeDependingOnName;
    }

    public DashboardNeedleInfo(ModularVehicleInfo owner, String partName) {
        super(owner, partName);
    }


    class SpeedNeedleNode<T extends BaseVehicleEntity<?>, A extends ModularVehicleInfo> extends SceneGraph.Node<T, A> {

        public SpeedNeedleNode(Vector3f scale, List<SceneGraph<T, A>> linkedChilds) {
            super(null, null, scale, linkedChilds);
        }

        @Override
        public void render(@Nullable T entity, EntityRenderContext entityRenderContext, A packInfo) {
            if (entity == null) return;
            DxModelRenderer vehicleModel = entityRenderContext.getModel();
            byte b = entityRenderContext.getTextureId();
            if (entityRenderContext.getModel().containsObjectOrNode(DashboardNeedleInfo.this.getObjectName()) && entity.getModuleByType(CarEngineModule.class) != null && DashboardNeedleInfo.this.getPosition() != null) {
                GlStateManager.pushMatrix();
                Vector3f pos = DashboardNeedleInfo.this.getPosition();
                GL11.glTranslatef(pos.x, pos.y, pos.z);
                TextUtils.makeGLRotation(getRotation());
                switch (DashboardNeedleInfo.this.getNeedleType()) {
                    case RPM:
                        int rpms = Math.round(entity.getModuleByType(CarEngineModule.class).getEngineProperty(VehicleEntityProperties.EnumEngineProperties.REVS) * 10000);
                        GlStateManager.rotate((float) DashboardNeedleInfo.this.needleMaxTurn * rpms / DashboardNeedleInfo.this.dashboardMaxValue, 0, 0, 1);
                        break;
                    case SPEED:
                        GlStateManager.rotate((float) DashboardNeedleInfo.this.needleMaxTurn * DynamXUtils.getSpeed(entity) / DashboardNeedleInfo.this.dashboardMaxValue, 0, 0, 1);
                        break;
                }
                vehicleModel.renderGroup(DashboardNeedleInfo.this.getObjectName(), b, entityRenderContext.isUseVanillaRender());
                GlStateManager.popMatrix();
            }
        }
    }

    public enum EnumDashboardNeedleType {
        SPEED, RPM;

        public static EnumDashboardNeedleType fromString(String targetName) {
            for (EnumDashboardNeedleType dashboardNeedleType : values()) {
                if (dashboardNeedleType.name().equalsIgnoreCase(targetName)) {
                    return dashboardNeedleType;
                }
            }
            throw new IllegalArgumentException("Invalid DashboardNeedleType value '" + targetName + "'");
        }
    }

}
