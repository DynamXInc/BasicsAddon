package fr.dynamx.addons.basics.common.infos;

import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.api.contentpack.object.subinfo.SubInfoType;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.common.contentpack.type.objects.ItemObject;

public class BasicsItemInfo extends SubInfoType<ItemObject<?>> {
    @PackFileProperty(configNames = "IsVehicleKey", required = false, defaultValue = "false")
    protected boolean isKey;
    @PackFileProperty(configNames = "FuelCapacity", required = false, defaultValue = "0")
    protected int fuelCapacity;

    public BasicsItemInfo(ISubInfoTypeOwner<ItemObject<?>> owner) {
        super(owner);
    }

    @Override
    public void appendTo(ItemObject<?> owner) {
        owner.addSubProperty(this);
    }

    @Override
    public String getName() {
        return "BasicsItemInfos of "+getOwner().getFullName();
    }

    public boolean isKey() {
        return isKey;
    }

    public void setKey(boolean key) {
        isKey = key;
    }

    public boolean isFuelContainer() {
        return fuelCapacity > 0;
    }

    public int getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(int fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }
}
