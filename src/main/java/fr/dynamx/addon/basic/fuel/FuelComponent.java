package fr.dynamx.addon.basic.fuel;

import fr.dynamx.api.ecs.Component;

public record FuelComponent(float fuel, float tankSize, float consumptionPerSecond) implements Component {

    public FuelComponent {
        if (tankSize <= 0) {
            throw new IllegalArgumentException("tankSize must be > 0");
        }
        fuel = Math.max(0, Math.min(fuel, tankSize));
    }

    public static FuelComponent full(float tankSize, float consumptionPerSecond) {
        return new FuelComponent(tankSize, tankSize, consumptionPerSecond);
    }

    public FuelComponent withFuel(float newFuel) {
        return new FuelComponent(newFuel, tankSize, consumptionPerSecond);
    }

    public boolean isEmpty() {
        return fuel <= 0;
    }
}
