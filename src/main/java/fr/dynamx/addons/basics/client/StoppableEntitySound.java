package fr.dynamx.addons.basics.client;

import fr.dynamx.common.entities.BaseVehicleEntity;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class StoppableEntitySound extends MovingSound
{
    private final BaseVehicleEntity<?> entity;

    public StoppableEntitySound(SoundEvent soundIn, SoundCategory categoryIn, BaseVehicleEntity<?> entity) {
        super(soundIn, categoryIn);
        this.entity = entity;
        this.volume = 3;
    }

    @Override
    public void update()
    {
        if(entity.isDead)
            stop();
        xPosF = (float) entity.posX;
        yPosF = (float) entity.posY;
        zPosF = (float) entity.posZ;
    }

    public void stop()
    {
        donePlaying = true;
    }
}
