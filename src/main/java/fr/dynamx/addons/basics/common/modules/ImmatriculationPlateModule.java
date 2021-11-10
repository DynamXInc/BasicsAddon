package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.common.infos.ImmatriculationPlateInfos;
import fr.dynamx.addons.basics.common.network.ImmatriculationPlateSynchronizedVariable;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.network.sync.SimulationHolder;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

public class ImmatriculationPlateModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>> {

    private List<ImmatriculationPlateInfos> info = new ArrayList<>();
    private String plate = "TODO RANDOM";//TODO MAKE RANDOM PLATE

    public ImmatriculationPlateModule(ImmatriculationPlateInfos info) {
        this.info.add(info);

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