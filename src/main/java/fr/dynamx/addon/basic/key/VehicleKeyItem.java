package fr.dynamx.addon.basic.key;

import fr.dynamx.addon.basic.BasicAddonMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class VehicleKeyItem extends Item {

    public VehicleKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display,
                                Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, display, tooltipAdder, flag);
        String keyId = stack.get(BasicAddonMod.KEY_ID);
        if (keyId == null) {
            tooltipAdder.accept(Component.literal("Non liée — clic droit sur un véhicule pour lier")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            String vehicleName = stack.get(BasicAddonMod.KEY_VEHICLE_NAME);
            String label = vehicleName != null ? vehicleName
                    : (keyId.length() > 8 ? keyId.substring(0, 8) : keyId);
            tooltipAdder.accept(Component.literal("Liée au véhicule " + label)
                    .withStyle(ChatFormatting.GOLD));
        }
    }
}
