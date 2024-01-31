package fr.dynamx.addons.basics.utils;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.infos.BasicsItemInfo;
import fr.dynamx.common.contentpack.type.objects.ItemObject;
import fr.dynamx.common.entities.PackPhysicsEntity;
import fr.dynamx.common.items.DynamXItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class VehicleKeyUtils {
    /**
     * @param stack the item stack to test
     * @return True if the given item is a key (DynamXItem with a BasicsItemInfo and BasicsItemInfo.isKey() == true)
     */
    public static boolean isKeyItem(ItemStack stack) {
        return stack.getItem() instanceof DynamXItem && (((DynamXItem<?>) stack.getItem()).getInfo()).getSubPropertyByType(BasicsItemInfo.class) != null &&
                (((DynamXItem<?>) stack.getItem()).getInfo()).getSubPropertyByType(BasicsItemInfo.class).isKey();
    }

    /**
     * @param stack the key item stack
     * @return the BasicsItemInfo of the given item stack, or null
     */
    public static BasicsItemInfo<?> getKeyInfos(ItemStack stack) {
        if(!(stack.getItem() instanceof DynamXItem<?>))
            return null;
        return ((DynamXItem<?>)stack.getItem()).getInfo().getSubPropertyByType(BasicsItemInfo.class);
    }

    /**
     * @param key the key item stack
     * @return true if some vehicle is linked to this key
     */
    public static boolean hasLinkedVehicle(ItemStack key) {
        return key.hasTagCompound() && key.getTagCompound().hasKey("VehicleId", Constants.NBT.TAG_STRING);
    }

    /**
     * Adds a linked vehicle to this key
     * @param key the key item stack
     * @param vehicle the vehicle
     */
    public static void setLinkedVehicle(ItemStack key, PackPhysicsEntity<?, ?> vehicle) {
        if (!key.hasTagCompound())
            key.setTagCompound(new NBTTagCompound());
        if(!key.getTagCompound().hasKey("VehicleId", Constants.NBT.TAG_STRING)) {
            // Simple backward-compatibility
            key.getTagCompound().setString("VehicleId", vehicle.getPersistentID().toString());
            key.getTagCompound().setString("VehicleName", vehicle.getPackInfo().getName());
        } else {
            int id = 1;
            while(key.getTagCompound().hasKey("VehicleId" + id, Constants.NBT.TAG_STRING))
                id++;
            key.getTagCompound().setString("VehicleId" + id, vehicle.getPersistentID().toString());
            key.getTagCompound().setString("VehicleName" + id, vehicle.getPackInfo().getName());
        }
    }

    /**
     * Returns the first linked vehicle
     *
     * @param key the key item stack
     * @return the first linked vehicle
     * @deprecated Returns only the first linked vehicle, not the others
     */
    @Deprecated
    public static UUID getLinkedVehicle(ItemStack key) {
        if (!key.hasTagCompound())
            return null;
        return UUID.fromString(key.getTagCompound().getString("VehicleId"));
    }

    /**
     * @param key the key item stack
     * @param vehicleId the vehicle id
     * @return true if the given vehicle is linked to this ke
     */
    public static boolean isVehicleLinked(ItemStack key, UUID vehicleId) {
        if(!key.hasTagCompound() || !key.getTagCompound().hasKey("VehicleId", Constants.NBT.TAG_STRING))
            return false;
        if(key.getTagCompound().getString("VehicleId").equals(vehicleId.toString()))
            return true;
        int id = 1;
        String idh = null;
        while(!(idh = key.getTagCompound().getString("VehicleId" + id)).isEmpty() && !idh.equals(vehicleId.toString())) {
            id++;
        }
        return !idh.isEmpty();
    }

    /**
     * Creates a new key for the given vehicle entity
     *
     * @param entity the vehicle entity
     * @return the key item stack linked to the given vehicle
     */
    public static ItemStack getKeyForVehicle(PackPhysicsEntity<?, ?> entity) {
        ItemStack stack = new ItemStack(BasicsAddon.keysItem);
        setLinkedVehicle(stack, entity);
        return stack;
    }
}
