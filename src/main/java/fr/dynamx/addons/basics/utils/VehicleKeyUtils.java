package fr.dynamx.addons.basics.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class VehicleKeyUtils {
    public static boolean hasLinkedVehicle(ItemStack key) {
        return key.hasTagCompound() && key.getTagCompound().hasKey("VehicleId", Constants.NBT.TAG_STRING);
    }

    public static void setLinkedVehicle(ItemStack key, UUID vehicle) {
        if (!key.hasTagCompound())
            key.setTagCompound(new NBTTagCompound());
        key.getTagCompound().setString("VehicleId", vehicle.toString());
    }

    public static UUID getLinkedVehicle(ItemStack key) {
        return UUID.fromString(key.getTagCompound().getString("VehicleId"));
    }
}
