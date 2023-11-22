package fr.dynamx.addons.basics.client;

import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.api.audio.IDynamXSound;
import fr.dynamx.client.handlers.ClientEventHandler;
import fr.dynamx.client.sound.DynamXSoundHandler;
import fr.dynamx.common.entities.BaseVehicleEntity;

public class SirenSound implements IDynamXSound {
    private final BaseVehicleEntity<?> entity;
    private final BasicsAddonModule module;

    public SirenSound(BaseVehicleEntity<?> entity, BasicsAddonModule module) {
        this.entity = entity;
        this.module = module;
    }

    @Override
    public void update(DynamXSoundHandler handler) {
        handler.setPosition(this, (float) entity.posX, (float) entity.posY, (float) entity.posZ);

        if (entity.isDead || !module.isSirenOn()) {
            handler.stopSound(this);
        }
    }

    @Override
    public String getSoundUniqueName() {
        return entity.getEntityId() + "_" + module.getInfos().sirenSound;
    }

    @Override
    public void onStarted() {
    }

    @Override
    public boolean tryStop() {
        return true;
    }

    @Override
    public float getVolume() {
        return 1.0F;
    }

    @Override
    public float getDistanceToPlayer() {
        return entity.getDistance(ClientEventHandler.MC.player);
    }

    @Override
    public void onMuted() {
    }
}
