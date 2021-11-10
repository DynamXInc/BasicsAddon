package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.common.infos.ImmatriculationPlateInfos;
import fr.dynamx.addons.basics.common.network.ImmatriculationPlateSynchronizedVariable;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.network.sync.SimulationHolder;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public class ImmatriculationPlateModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>> {

    private List<ImmatriculationPlateInfos> info = new ArrayList<>();
    private String plate = "";

    public ImmatriculationPlateModule(ImmatriculationPlateInfos info) {
        this.info.add(info);
        // Thanks to Kerlan
        String firstNumber = RandomStringUtils.random(2, 97, 122, true, false);
        String secondNumber = RandomStringUtils.randomNumeric(3);
        String thirdNumber = RandomStringUtils.random(2, 97, 122, true, false);
        this.plate = firstNumber.toUpperCase() + "-" + secondNumber + "-" + thirdNumber.toUpperCase();
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
}