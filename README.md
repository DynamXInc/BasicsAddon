![DynamX](https://dynamx.fr/img/head-logo.png)

# DynamXInc - BasicsAddon

Port of the BasicsAddon (Forge 1.12) to **DynamX** for Fabric, Minecraft 1.21.x.

This addon adds new features to DynamX:

- Vehicle keys (locking, multi-key support)
- Fuel tank and jerrycan
- Klaxon (horn)
- Immatriculation plates

### How to add modules to your vehicle:

To add a module to your vehicle you need to edit the vehicle file inside your DynamX pack
(`vehicle.yaml`). Here are some examples:

#### Fuel-Tank

```yaml
# 1.12: TankSize / FuelConsumption inside FuelTank{}
fuel_tank:
  tank_size: 80        # liters
  fuel_consumption: 1  # consumption tied to the throttle
```

When the tank is empty the engine cuts off (the vehicle coasts, brakes and steering keep
working) until it is refueled.

#### Licenseplate

```yaml
# 1.12: Pattern inside ImmatriculationPlate{}
license_plate:
  pattern: "@@-%%%-@@"  # @ = letter, % = digit, same semantics as 1.12
```

The plate number is generated on spawn and persisted with the vehicle.

### Keys and jerrycan:

The BasicsAddon includes a key item and a jerrycan item.

- **Vehicle key**: bind it to a vehicle, then lock/unlock it; a locked vehicle cannot be
  entered. One key opens one vehicle by default, but a bound key can be associated with
  other vehicles (multi-key).
- **Jerrycan** (60 L): right-click a vehicle to refuel it (works even when locked, like
  in 1.12), and refill the jerrycan when it is empty.

### Klaxon:

Press **K** (rebindable) to honk. The sound is broadcast by the server with a cooldown.

### Commands:

| Command | Description |
|---|---|
| `/dynamxbasics fuel` | Fuel level of the nearest vehicle |
| `/dynamxbasics plate` | Plate of the nearest vehicle |
| `/dynamxbasics spawn <pack> <vehicle>` | Spawn a locked vehicle and give its key (op) |

### Building from source:

Requires JDK 21+ and a locally built DynamX JAR.

By default the build looks for `../DynamX/build/libs/dynamx-0.1.0.jar`
(sibling project). Otherwise pass the path explicitly:

```
./gradlew build -PdynamxJar=/path/to/dynamx.jar
```

The addon JAR is produced in `build/libs/`.

## Links

DynamX website: https://dynamx.fr  
Addon download: https://dynamx.fr/packs  
DynamX wiki: https://dynamx.fr/wiki/  
Discord: https://discord.gg/y53KGzD
