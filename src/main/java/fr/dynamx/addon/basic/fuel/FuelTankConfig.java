package fr.dynamx.addon.basic.fuel;

public record FuelTankConfig(float tank_size, float fuel_consumption) {

    public float tankSizeOrDefault() {
        return tank_size > 0 ? tank_size : 100f;
    }

    public float consumptionOrDefault() {
        return fuel_consumption > 0 ? fuel_consumption : 1f;
    }
}
