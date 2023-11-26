package fr.dynamx.addons.basics.client;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.utils.GuiTextureSprite;
import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.client.handlers.hud.HudIcons;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.modules.engines.CarEngineModule;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Collections;

public class BasicsAddonHudIcons implements HudIcons {
    private final BasicsAddonModule module;
    private final BaseVehicleEntity<?> entity;
    private boolean wasLocked;

    public BasicsAddonHudIcons(BasicsAddonModule module, BaseVehicleEntity<?> entity) {
        this.module = module;
        this.entity = entity;
        wasLocked = module.isLocked();
        //todo fuel counter FuelTankModule module1 = entity.getModuleByType(FuelTankModule.class);
    }

    @Override
    public void tick(GuiComponent<?>[] components) {
        if (wasLocked != module.isLocked()) {
            wasLocked = module.isLocked();
            components[2].getStyle().refreshCss(false, "bas_lock");
        }
    }

    @Override
    public boolean isVisible(int componentId) {
        switch (componentId) {
            case 0:
                return module.isHeadLightsOn();
            case 1:
                //TODO CLEAN
                if (entity.getModuleByType(CarEngineModule.class) != null) {
                    return entity.getModuleByType(CarEngineModule.class).getSpeedLimit() != Float.MAX_VALUE;
                }
            case 3:
                //TODO THIS ICON
                return false;
            case 4:
                return module.isTurnSignalLeftOn() && entity.ticksExisted % 20 < 10;
            case 5:
                return module.isTurnSignalRightOn() && entity.ticksExisted % 20 < 10;
            default:
                return true;
        }
    }

    @Override
    public int iconCount() {
        return 7;
    }

    @Override
    public void initIcon(int componentId, GuiComponent<?> component) {
        if (componentId == 2) {
            component.getStyle().addAutoStyleHandler(new AutoStyleHandler<ComponentStyleManager>() {
                @Override
                public boolean handleProperty(EnumCssStyleProperties property, EnumSelectorContext context, ComponentStyleManager target) {
                    if (property == EnumCssStyleProperties.TEXTURE) {
                        if (module.isLocked()) {
                            target.setTexture(new GuiTextureSprite(new ResourceLocation(BasicsAddon.ID, "textures/lock.png")));
                        } else {
                            target.setTexture(new GuiTextureSprite(new ResourceLocation(BasicsAddon.ID, "textures/unlock.png")));
                        }
                        return true;
                    }
                    return false;
                }

                @Override
                public Collection<EnumCssStyleProperties> getModifiedProperties(ComponentStyleManager target) {
                    return Collections.singletonList(EnumCssStyleProperties.TEXTURE);
                }
            });
        }
    }
}
