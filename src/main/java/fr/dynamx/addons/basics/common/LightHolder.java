package fr.dynamx.addons.basics.common;

import com.jme3.math.Vector3f;
//import fr.betterlights.api.Light;

/**
 * WIP <br>
 * Temporarily disabled
 */
@Deprecated
public class LightHolder {
    //private Light light, light2;

    public void update1(Vector3f pos, Vector3f rot) {
        /*if(light == null)
            light = new Light().color(0,0,255, 0.04f);
        light.pos(pos.x, pos.y, pos.z).direction(rot.x, rot.y, rot.z,1f);
        light.enable();*/

            /*EntityPlayer player = Minecraft.getMinecraft().player;
            light = Light.builder()
                    .color(20, 100, 180, .009F)
                    .pos(player)
                    .direction((float) player.getLookVec().x * 20, (float) player.getLookVec().y * 20, (float) player.getLookVec().z * 20, 1.2f)
                    .build();*/
    }

    public void update2(Vector3f pos, Vector3f rot) {
        /*if(light2 == null)
            light2 = new Light().color(0,0,255, 0.04f);
        light2.pos(pos.x, pos.y, pos.z).direction(rot.x, rot.y, rot.z,1f);
        light2.enable();*/
    }

    public void destroy() {
        /*if(light != null) {
            light.disable();
            light = null;
        }
        if(light2 != null) {
            light2.disable();
            light2 = null;
        }*/
    }
}
