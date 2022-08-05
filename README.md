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
ImmatriculationPlate_back{
  Position: 0 0 0
  Size: 0 0 0
  Rotation: 0 0 0
  Pattern: @@ \n @@ %%%%
  Spacing: 0
  Color: 0 0 0
}
```

#### Speed-Display
```
SpeedDisplay{
  Position: 0 0 0
  Size: 1 1 1
  Rotation: 0 0 0
  Color: 0 0 0
}
```

## Links
DynamX website: https://dynamx.fr  
Addon download: https://dynamx.fr/packs
DynamX wiki: https://dynamx.fr/wiki/
Discord: https://discord.gg/y53KGzD 
