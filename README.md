![DynamX](https://dynamx.fr/img/head-logo.png)

# DynamXInc - BasicsAddon

This addon adds new features to DynamX :

- Klaxon and siren
- Fuel tank
- Immatriculation plates
- Speed display on the vehicle

### How to add modules:

To add a module to your vehicle you need to edit the vehicle file inside your DynamX pack.
This file have a name like `vehicle_NAME`.

To use modules ingame you have to add specific code blocks.
Here are some examples:

#### Licenseplate

```
ImmatriculationPlate_back#Op{
  Position: 0 0 0
  Scale: 0 0 0
  Rotation: 0 0 0
  Pattern: @@ \n @@ %%%%
  LineSpacing: 0
  Color: 0 0 0
}
```

The `#Op` suffix stands for "Optional" and will remove errors when the BasicsAddon isn't loaded.

#### Lights and sounds

```
BasicsAddon#Op{
  HornSound: horn/truck
  SirenSound: sirens/truck
  TurnSignalLeftLightSource: 1
  TurnSignalRightLightSource: 2
  BrakeLightsSource: 3
  HeadLightsSource: 4
  ReverseLightsSource: 5
  DRLightsSources: 6 //Daytime running lights
}
```

The lights must then be configured accordingly with the part lights of DynamX.

#### Speed-Display

```
SpeedDisplay#Op{
  Position: 0 0 0
  Scale: 1 1 1
  Rotation: 0 0 0
  Color: 0 0 0
}
```

#### Fuel-Tank

```
FuelTank#Op{
  Position: 0 0 0
  Scale: 1 1 1
  TankSize: 80
  FuelConsumption: 1
}
```

## Links

DynamX website: https://dynamx.fr  
Addon download: https://dynamx.fr/packs
DynamX wiki: https://dynamx.fr/wiki/
Discord: https://discord.gg/y53KGzD 
