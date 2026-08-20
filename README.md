# Rot Timer

![Forge](https://img.shields.io/badge/Forge-47.4.0-FF7000?style=for-the-badge&logo=minecraftforge&logoColor=white)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-62B47A?style=for-the-badge&logo=minecraft&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)
[![Modrinth](https://img.shields.io/badge/Modrinth-Available-1BD96A?style=for-the-badge&logo=modrinth&logoColor=white)](https://modrinth.com/mod/rot-timer)

A Minecraft Forge mod that adds realistic food spoilage, barrel storage bonuses, and long-lasting canned stew for a deeper survival experience.

**Minecraft:** 1.20.1 | **Forge:** 47.4.0 | **License:** MIT

---

## Features

### Food Spoilage System

Every bite counts. All edible food in your inventory will spoil over time and turn into Rotten Flesh.

- **Base spoil time:** 48,000 ticks (~2 in-game days / 40 minutes real time)
- Spoilage only happens on the server side
- Stacks of the same food share the oldest creation time

### Temperature System

Biome temperature affects how fast food spoils:

| Biome Temperature | Effect |
|---|---|
| Cold (< 0.15) | Food lasts **4x longer** |
| Cool (< 0.5) | Food lasts **1.33x longer** |
| Normal (< 1.0) | No change |
| Hot (>= 1.0) | Food spoils **2x faster** |

### Barrel Storage

Wooden barrels slow spoilage by **2x**, extending freshness for longer-term storage. Barrels and chests are scanned periodically for nearby players.

### Canned Food (Tinkan)

Canned foods spoil **5x slower** than regular food — perfect for long adventures:

| Item | Name | Effect |
|---|---|---|
| ![Tinkan](https://raw.githubusercontent.com/Kaster1D/RotTimerr/main/src/main/resources/assets/mrmod/textures/item/tinkan.png) | Survivor's Beef Stew | Heal I |
| ![TinkanFish](https://raw.githubusercontent.com/Kaster1D/RotTimerr/main/src/main/resources/assets/mrmod/textures/item/tinkanfish.png) | Mariner's Fish Stew | Dolphin's Grace |
| ![TinkanChicken](https://raw.githubusercontent.com/Kaster1D/RotTimerr/main/src/main/resources/assets/mrmod/textures/item/tinkanchicken.png) | Pioneer's Chicken Stew | Levitation |
| ![TinkanTomato](https://raw.githubusercontent.com/Kaster1D/RotTimerr/main/src/main/resources/assets/mrmod/textures/item/tinkantomato.png) | Tomato Stew | Fire Resistance |

### New Items

- **Jar** — Glass jar for jams and preserves
- **Tin Can** — Metal can for crafting stews
- **Cheese** — Crafted from milk via smelting
- **Flour** — Ground from wheat
- **Pasta** — Made from flour
- **Forcemeat** — Raw meat preparation
- **Chicken Leg** — Drops a bone when eaten
- **Cherry & Cherry Jam** — Sweet food from cherry trees

### Farming

- **Tomato Crop** — Custom 6-stage crop plantable with Tomato Seeds
- **Tomato Seeds** — Obtained from harvesting tomatoes

### Crafting Chain

```
Iron Ingot x3 → Tin Can
Tin Can + Ingredients → Canned Stew variants
Milk Bucket → Cheese (smelting)
Wheat → Flour → Pasta
Cherry + Jar + Sugar → Cherry Jam
Cooked Chicken → Chicken Leg
```

---

## Installation

1. Install [Minecraft Forge](https://files.minecraftforge.net/) for 1.20.1
2. Download the latest `RotTimer-*.jar` from [Releases](https://github.com/Kaster1D/RotTimerr/releases) or [Modrinth](https://modrinth.com/mod/rot-timer)
3. Place the jar in your `.minecraft/mods/` folder
4. Launch Minecraft with Forge profile

---

## For Modpack Creators

Feel free to include Rot Timer in your modpack! Just make sure to:
- Credit the mod
- Don't claim it as your own creation
- Always read the changelogs — breaking changes may occur between versions

### Configuration

A `Rottimer.toml` config file is generated on first launch. Customize everything:

```toml
[general]
base_spoil_ticks = 48000        # Ticks before food spoils
container_scan_interval = 200   # How often to scan containers
container_scan_radius = 4       # Scan radius around player

[multipliers]
barrel = 2.0    # Barrel storage multiplier
chest = 1.0     # Chest storage multiplier
tinkan = 5.0    # Canned food multiplier

[temperature]
cold = 4.0      # Cold biomes (tundra, snow)
cool = 1.33     # Cool biomes (taiga)
normal = 1.0    # Normal biomes
hot = 2.0       # Hot biomes (desert, nether)

# Custom multipliers for modded food: "modid:itemid=multiplier"
[custom_items]
"farmersdelight:ham" = 0.8
"farmersdelight:dried_kelp" = 3.0

# Items that never spoil: "modid:itemid"
[blacklist]
"minecraft:golden_apple" = true
"minecraft:enchanted_golden_apple" = true
```

---

## Links

- [Modrinth](https://modrinth.com/mod/rot-timer)
- [Ko-fi](https://ko-fi.com/kaster_64)
