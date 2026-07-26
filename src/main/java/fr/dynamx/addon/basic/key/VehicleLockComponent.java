package fr.dynamx.addon.basic.key;

import fr.dynamx.api.ecs.Component;

public record VehicleLockComponent(String keyId, boolean locked) implements Component {

    public VehicleLockComponent {
        if (keyId == null || keyId.isBlank()) {
            throw new IllegalArgumentException("keyId must not be blank");
        }
    }

    public VehicleLockComponent withLocked(boolean newLocked) {
        return new VehicleLockComponent(keyId, newLocked);
    }
}
