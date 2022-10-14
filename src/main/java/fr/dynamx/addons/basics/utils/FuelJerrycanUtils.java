package fr.dynamx.addons.basics.utils;

import fr.dynamx.addons.basics.common.infos.BasicsItemInfo;
import fr.dynamx.common.contentpack.type.objects.ItemObject;
import fr.dynamx.common.items.DynamXItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public class FuelJerrycanUtils {

    public static boolean isJerrycanItem(ItemStack stack) {
        return stack.getItem() instanceof DynamXItem && ((DynamXItem<?>) stack.getItem()).getInfo() instanceof ItemObject && ((ItemObject<?>) ((DynamXItem<?>) stack.getItem()).getInfo()).getSubPropertyByType(BasicsItemInfo.class) != null &&
                ((ItemObject<?>) ((DynamXItem<?>) stack.getItem()).getInfo()).getSubPropertyByType(BasicsItemInfo.class).isFuelContainer();
    }

    public static boolean hasFuel(ItemStack key) {
        return key.hasTagCompound() && key.getTagCompound().hasKey("fuel", Constants.NBT.TAG_FLOAT);
    }

    public static void setFuel(ItemStack key, float fuel) {
        if (!key.hasTagCompound())
            key.setTagCompound(new NBTTagCompound());
        key.getTagCompound().setFloat("fuel", Math.max(fuel, 0));
    }

    public static float getFuel(ItemStack key) {
        return key.getTagCompound().getFloat("fuel");
    }
}
