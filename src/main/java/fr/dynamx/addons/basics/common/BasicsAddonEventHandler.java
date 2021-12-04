package fr.dynamx.addons.basics.common;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.infos.BasicsAddonInfos;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.addons.basics.utils.VehicleKeyUtils;
import fr.dynamx.api.events.PhysicsEntityEvent;
import fr.dynamx.api.events.VehicleEntityEvent;
import fr.dynamx.common.contentpack.parts.PartDoor;
import fr.dynamx.common.contentpack.parts.PartSeat;
import fr.dynamx.common.contentpack.parts.PartStorage;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.items.DynamXItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BasicsAddon.ID)
public class BasicsAddonEventHandler {
    @SubscribeEvent
    //Note : don't add parameter to BaseVehicleEntity : this would break the event on the fml side
    public static void initVehicleModules(PhysicsEntityEvent.CreateEntityModulesEvent<BaseVehicleEntity> event) {
        BaseVehicleEntity<?> entity = event.getEntity();
        BasicsAddonInfos info = entity.getPackInfo().getSubPropertyByType(BasicsAddonInfos.class);
        event.getModuleList().add(new BasicsAddonModule(entity, info)); //don't care of null info, module is used by keys system anyway
    }

    @SubscribeEvent
    public static void interactWithCar(VehicleEntityEvent.VehicleInteractEntityEvent event) {
        if (event.part instanceof PartSeat || event.part instanceof PartStorage || event.part instanceof PartDoor) {
            BasicsAddonModule module = event.getEntity().getModuleByType(BasicsAddonModule.class);
            //TODO AJOUT CONFIG SUR LES ITEMS
            if (VehicleKeyUtils.isKeyItem(event.player.getHeldItemMainhand())) {
                ITextComponent msg;
                if (!VehicleKeyUtils.hasLinkedVehicle(event.player.getHeldItemMainhand())) {
                    if(!module.hasLinkedKey()) {
                        VehicleKeyUtils.setLinkedVehicle(event.player.getHeldItemMainhand(), event.getEntity());
                        msg = new TextComponentTranslation("basadd.key.associed", event.getEntity().getPackInfo().getName());
                        msg.getStyle().setColor(TextFormatting.DARK_BLUE);
                        module.setHasLinkedKey(true);
                    } else {
                        msg = new TextComponentTranslation("basadd.key.assoc.error");
                        msg.getStyle().setColor(TextFormatting.DARK_RED);
                    }
                } else if (event.getEntity().getPersistentID().equals(VehicleKeyUtils.getLinkedVehicle(event.player.getHeldItemMainhand()))) {
                    module.setLocked(!module.isLocked());
                    if (module.isLocked()) {
                        msg = new TextComponentTranslation("basadd.key.locked");
                        msg.getStyle().setColor(TextFormatting.DARK_RED);
                    } else {
                        msg = new TextComponentTranslation("basadd.key.unlocked");
                        msg.getStyle().setColor(TextFormatting.DARK_GREEN);
                    }
                } else {
                    msg = new TextComponentTranslation("basadd.key.invalid");
                    msg.getStyle().setColor(TextFormatting.DARK_RED);
                }
                //if(msg != null)
                event.player.sendMessage(msg);
                event.setCanceled(true);
            } else if (module.isLocked()) {
                event.setCanceled(true);
            }
        }
    }
}
