package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.common.infos.ImmatriculationPlateInfos;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.network.sync.EntityVariable;
import fr.dynamx.api.network.sync.SynchronizationRules;
import fr.dynamx.api.network.sync.SynchronizedEntityVariable;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.PackPhysicsEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SynchronizedEntityVariable.SynchronizedPhysicsModule
public class ImmatriculationPlateModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>> {

    private final List<ImmatriculationPlateInfos> info = new ArrayList<>();

    @SynchronizedEntityVariable(name = "plate")
    private final EntityVariable<String> plate = new EntityVariable<>(SynchronizationRules.SERVER_TO_CLIENTS, "");

    public ImmatriculationPlateModule(ImmatriculationPlateInfos info) {
        this.info.add(info);

        String pattern = info.getPattern();
        StringBuilder builder = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '%') {
                builder.append(r.nextInt(10));
            } else if (c == '@') {
                builder.append((char) (r.nextInt(26) + 65));
            } else {
                builder.append(c);
            }
        }
        setPlate(builder.toString());
    }

    public List<ImmatriculationPlateInfos> getInfo() {
        return info;
    }

    public void addInformation(ImmatriculationPlateInfos info) {
        this.info.add(info);
    }

    public String getPlate() {
        return plate.get();
    }

    public void setPlate(String plate) {
        this.plate.set(plate);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setString("bas_immat_plate", getPlate());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("bas_immat_plate", Constants.NBT.TAG_STRING)) {
            setPlate(tag.getString("bas_immat_plate"));
        }
    }

}