package fr.dynamx.addon.basic.fuel;

import fr.dynamx.addon.basic.BasicAddonMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class JerrycanItem extends Item {

    /** Capacité du jerrican, identique au FuelCapacity=60 de l'addon 1.12. */
    public static final float CAPACITY = 60f;

    public JerrycanItem(Properties properties) {
        super(properties);
    }

    public static float getFuel(ItemStack stack) {
        Float fuel = stack.get(BasicAddonMod.JERRYCAN_FUEL);
        return fuel != null ? fuel : 0f;
    }

    public static void setFuel(ItemStack stack, float fuel) {
        stack.set(BasicAddonMod.JERRYCAN_FUEL, Math.max(0f, Math.min(fuel, CAPACITY)));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, TooltipDisplay display,
                                Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, display, tooltipAdder, flag);
        float fuel = getFuel(stack);
        if (fuel > 0) {
            tooltipAdder.accept(Component.literal(String.format("Carburant : %.1f / %.0f L", fuel, CAPACITY))
                    .withStyle(ChatFormatting.GOLD));
        } else {
            tooltipAdder.accept(Component.literal("Vide — clic droit sur un véhicule pour remplir")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
