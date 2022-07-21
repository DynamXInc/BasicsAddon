package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.common.infos.ImmatriculationPlateInfos;
import fr.dynamx.addons.basics.common.network.ImmatriculationPlateSynchronizedVariable;
import fr.dynamx.addons.basics.utils.TextUtils;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.network.sync.SimulationHolder;
import fr.dynamx.client.renders.RenderPhysicsEntity;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImmatriculationPlateModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IDrawableModule<BaseVehicleEntity<?>> {

    private final List<ImmatriculationPlateInfos> info = new ArrayList<>();
    private String plate = "";

    public ImmatriculationPlateModule(ImmatriculationPlateInfos info) {
        this.info.add(info);
        // Thanks to Kerlan
        /*String firstNumber = RandomStringUtils.random(2, 97, 122, true, false);
        String secondNumber = RandomStringUtils.randomNumeric(3);
        String thirdNumber = RandomStringUtils.random(2, 97, 122, true, false);*/

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
        this.plate = builder.toString();
    }

    public List<ImmatriculationPlateInfos> getInfo() {
        return info;
    }

    public void addInformation(ImmatriculationPlateInfos info) {
        this.info.add(info);
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    @Override
    public void addSynchronizedVariables(Side side, SimulationHolder simulationHolder, List variables) {
        if (side.isServer()) {
            variables.add(ImmatriculationPlateSynchronizedVariable.NAME);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setString("bas_immat_plate", plate);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("bas_immat_plate", Constants.NBT.TAG_STRING)) {
            plate = tag.getString("bas_immat_plate");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawParts(RenderPhysicsEntity<?> render, float partialTicks, BaseVehicleEntity<?> entity) {
        for (ImmatriculationPlateInfos immatriculationPlateInfos : getInfo()) {
            TextUtils.drawText(
                    immatriculationPlateInfos.getPosition(),
                    immatriculationPlateInfos.getScale(),
                    immatriculationPlateInfos.getRotation(),
                    getPlate(),
                    immatriculationPlateInfos.getColor(),
                    immatriculationPlateInfos.getFont());
        }
    }
}