package fr.dynamx.addons.basics.utils;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.infos.BasicsItemInfo;
import fr.dynamx.common.contentpack.type.objects.ItemObject;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.items.DynamXItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class VehicleKeyUtils {
    public static boolean isKeyItem(ItemStack stack) {
        return stack.getItem() instanceof DynamXItem && ((DynamXItem<?>) stack.getItem()).getInfo() instanceof ItemObject && ((ItemObject<?>) ((DynamXItem<?>) stack.getItem()).getInfo()).getSubPropertyByType(BasicsItemInfo.class) != null &&
                ((ItemObject<?>) ((DynamXItem<?>) stack.getItem()).getInfo()).getSubPropertyByType(BasicsItemInfo.class).isKey();
    }

    public static boolean hasLinkedVehicle(ItemStack key) {
        return key.hasTagCompound() && key.getTagCompound().hasKey("VehicleId", Constants.NBT.TAG_STRING);
    }

    public static void setLinkedVehicle(ItemStack key, BaseVehicleEntity<?> vehicle) {
        if (!key.hasTagCompound()) {
            key.setTagCompound(new NBTTagCompound());
        }
        key.getTagCompound().setString("VehicleId", vehicle.getPersistentID().toString());
        key.getTagCompound().setString("VehicleName", vehicle.getPackInfo().getName());
    }

    public static UUID getLinkedVehicle(ItemStack key) {
        return UUID.fromString(key.getTagCompound().getString("VehicleId"));
    }

    public static ItemStack getKeyForVehicle(BaseVehicleEntity<?> entity) {
        ItemStack stack = new ItemStack(BasicsAddon.keysItem);
        setLinkedVehicle(stack, entity);
        return stack;
    }
}
