package fr.dynamx.addons.basics.common.infos;

import com.jme3.math.Vector3f;
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
public class DashboardNeedleInfo extends BasePart<ModularVehicleInfo> implements IDrawablePart<BaseVehicleEntity<?>,  ModularVehicleInfo> {
    @PackFileProperty(configNames = "Rotation", type = DefinitionType.DynamXDefinitionTypes.VECTOR3F)
    protected Vector3f rotation;

    @PackFileProperty(configNames = "ObjectName", required = false, defaultValue = "speedneedle")
    protected String objectName;

    @PackFileProperty(configNames = "NeedleMaxTurn", required = false, defaultValue = "140", description = "common.NeedleMaxTurn")
    protected int NeedleMaxTurn;

    @PackFileProperty(configNames = "DashboardMaxValue", required = false, defaultValue = "140", description = "common.DashboardMaxSpeed")
    protected int DashboardMaxValue;

    @PackFileProperty(configNames = "DependsOnNode", required = false, description = "common.DependsOnNode")
    protected String nodeDependingOnName;

    @PackFileProperty(configNames = "NeedleType", required = false, defaultValue = "SPEED", description = "common.NeedleType")
    protected String needleType;

    @Override
    public String[] getRenderedParts() {
        return new String[0];
    }

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
        if(childGraph != null)
            throw new IllegalArgumentException("DashboardNeedleInfo can't have children parts");
        return new SpeedNeedleNode<>(modelScale, null);
    }
    @Override
    public String getName() {
        return "PartShape named " + getPartName() + " in " + Objects.requireNonNull(getOwner()).getName();
    }

    public int getNeedleMaxTurn() {
        return NeedleMaxTurn;
    }

    public int getDashboardMaxValue() {
        return DashboardMaxValue;
    }

    public String getNeedleType() {
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
        public void render(@Nullable T t, EntityRenderContext entityRenderContext, A a) {


            if (t == null) return;
            DxModelRenderer vehicleModel = entityRenderContext.getModel();
            byte b = entityRenderContext.getTextureId();

//            if(vehicleModel instanceof ObjModelRenderer) {
//                ObjModelRenderer vehicleModel1 = (ObjModelRenderer) entityRenderContext.getModel();
//                System.out.println(vehicleModel1.getObjObjects());
//            }



            if (entityRenderContext.getModel().containsObjectOrNode(DashboardNeedleInfo.this.getObjectName()) && t.getModuleByType(CarEngineModule.class) != null && DashboardNeedleInfo.this.getPosition() != null) {
                GlStateManager.pushMatrix();


                Vector3f pos = DashboardNeedleInfo.this.getPosition();
                GL11.glTranslatef(pos.x, pos.y, pos.z);

//                DynamXRenderUtils.drawSphere(new Vector3f(0,0,0), 0.1f, Color.YELLOW);

                try {
                    assert getRotation() != null;
                    float rotate = getRotation().x;
                    if (rotate != 0)
                        GlStateManager.rotate(rotate, 1, 0, 0);
                    rotate = getRotation().y;
                    if (rotate != 0)
                        GlStateManager.rotate(rotate, 0, 1, 0);
                    rotate = getRotation().z;
                    if (rotate != 0)
                        GlStateManager.rotate(rotate, 0, 0, 1);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                if(Objects.equals(DashboardNeedleInfo.this.getNeedleType(), "SPEED")) {
                    GlStateManager.rotate((float) DashboardNeedleInfo.this.NeedleMaxTurn * DynamXUtils.getSpeed(t) / DashboardNeedleInfo.this.DashboardMaxValue, 0, 0, 1);
                } else if(Objects.equals(DashboardNeedleInfo.this.getNeedleType(), "RPM")) {
                    int rpms = Math.round(t.getModuleByType(CarEngineModule.class).getEngineProperty(VehicleEntityProperties.EnumEngineProperties.REVS) * 10000 );
                    GlStateManager.rotate((float) DashboardNeedleInfo.this.NeedleMaxTurn * rpms / DashboardNeedleInfo.this.DashboardMaxValue, 0, 0, 1);
                }

                vehicleModel.renderGroup(DashboardNeedleInfo.this.getObjectName(), b, entityRenderContext.isUseVanillaRender());

                GlStateManager.popMatrix();
            }
        }
    }

}
