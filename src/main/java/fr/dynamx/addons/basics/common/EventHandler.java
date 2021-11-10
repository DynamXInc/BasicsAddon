package fr.dynamx.addons.basics.common;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.infos.BasicsAddonInfos;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.addons.basics.utils.VehicleKeyUtils;
import fr.dynamx.api.events.PhysicsEntityEvent;
import fr.dynamx.api.events.VehicleEntityEvent;
import fr.dynamx.common.contentpack.parts.PartSeat;
import fr.dynamx.common.contentpack.parts.PartStorage;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.items.DynamXItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BasicsAddon.ID)
public class EventHandler {

    @SubscribeEvent
    //Note : don't add parameter to BaseVehicleEntity : this would break the event on the fml side
    public static void initVehicleModules(PhysicsEntityEvent.CreateEntityModulesEvent<BaseVehicleEntity> event) {
        BaseVehicleEntity<?> entity = event.getEntity();
        BasicsAddonInfos info = entity.getPackInfo().getSubPropertyByType(BasicsAddonInfos.class);
        event.getModuleList().add(new BasicsAddonModule(entity, info)); //don't care of null info, module is used by keys system anyway
    }

    @SubscribeEvent
    public static void interactWithCar(VehicleEntityEvent.VehicleInteractEntityEvent event) {
        if (event.part instanceof PartSeat || event.part instanceof PartStorage) {
            BasicsAddonModule module = event.getEntity().getModuleByType(BasicsAddonModule.class);
            //TODO AJOUT CONFIG SUR LES ITEMS
            if (event.player.getHeldItemMainhand().getItem() instanceof DynamXItem && ((DynamXItem<?>) event.player.getHeldItemMainhand().getItem()).getInfo().getFullName().equalsIgnoreCase("basicsaddon.item_car_keys")) {
                ITextComponent msg;
                if (!VehicleKeyUtils.hasLinkedVehicle(event.player.getHeldItemMainhand())) {
                    VehicleKeyUtils.setLinkedVehicle(event.player.getHeldItemMainhand(), event.getEntity().getPersistentID());
                    msg = new TextComponentString("Clé associée à " + event.getEntity().getPackInfo().getName() + " !");
                    msg.getStyle().setColor(TextFormatting.DARK_BLUE);
                } else if (event.getEntity().getPersistentID().equals(VehicleKeyUtils.getLinkedVehicle(event.player.getHeldItemMainhand()))) {
                    module.setLocked(!module.isLocked());
                    if (module.isLocked()) {
                        msg = new TextComponentString("Véhicule verrouillé !");
                        msg.getStyle().setColor(TextFormatting.DARK_RED);
                    } else {
                        msg = new TextComponentString("Véhicule déverouillé !");
                        msg.getStyle().setColor(TextFormatting.DARK_GREEN);
                    }
                } else {
                    msg = new TextComponentString("Clé invalide !");
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
