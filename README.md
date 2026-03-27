# ServerConfiguratorPlus

ServerConfiguratorPlus is a comprehensive, high-performance Folia-compatible plugin built for Minecraft 1.21.8. It provides server administrators with powerful configuration tools, chat moderation, nickname and item formatting utilities, automated announcements, and dynamic server list customizations.

## Core Features

- **Optimized for Folia:** Fully designed to embrace Folia's multi-threaded region-based architecture. Actions are executed on the appropriate thread (e.g., Entity Scheduler for item modifications).
- **Adventure API Integration:** Warning-free text processing utilizing the modern Adventure component API (Kyori).
- **Two-Line MOTD System:** Configurable static server name and a dynamically rotating MOTD (rotates every 5 minutes). Personalizes messages for returning players (IP caching).
- **Dynamic Player Slots:** Options to display fixed slots or dynamically show "one more" than the current online count. Custom Adventure-based protocol version texts.
- **Server Icons Manager:** Auto-processes `.png`, `.jpg`, `.jpeg`, `.webp`, and `.avif` icons to 64x64. Selects a random icon per ping.
- **Maintenance Mode:** Easy toggle (`/scp maintenance`) to secure the server. Enforced via asynchronous pre-login events.
- **Automated Announcements:** Configurable periodic broadcasts managed efficiently using the `GlobalRegionScheduler`.
- **Nickname & Item Formatting System:** Allows players to set stylized nicknames and item names utilizing legacy color codes (`&`) and a special `&u` tag to explicitly disable italics.
- **Chat Moderation:** Complete mute and unmute functionality with an organized permission structure.

## Command Reference

### Player Commands
- `/motd` - Displays the current active rotating MOTD.
- `/players` - Lists the number of online players and their names.
- `/status` - Shows comprehensive server status information (players, MOTD, version).
- `/kill` - Instantly defeats the executor.
- `/dupe` - Broadcasts the player's current coordinates.
- `/rules` - Broadcasts the player's current coordinates.
- `/nickname <text>` (Alias: `/nick`) - Sets a personalized display name supporting color and style codes.
- `/itemname <text>` (Alias: `/in`) - Formats the name of the item currently held in the main hand.

### Admin Commands
- `/scp` - Base admin command.
    - `/scp reload` - Centralized reload command for configurations and icons.
    - `/scp maintenance` - Toggles the maintenance status of the server.
- `/say <message>` - Broadcasts a custom server message.
- `/mute <player>` - Mutes a specified player.
- `/unmute <player>` - Unmutes a specified player.

## Directory Structure

```text
server-configurator-plus/
|-- build.gradle
|-- settings.gradle
|-- src/
|   |-- main/
|       |-- java/
|       |   |-- dev/
|       |       |-- dean/
|       |           |-- ServerConfiguratorPlus.java
|       |           |-- AnnouncementManager.java
|       |           |-- ConsoleFilter.java
|       |           |-- IconManager.java
|       |           |-- MaintenanceManager.java
|       |           |-- MotdManager.java
|       |           |-- MuteManager.java
|       |           |-- PlayerCacheManager.java
|       |           |-- commands/
|       |           |   |-- FormattingCommands.java
|       |           |   |-- KillCommand.java
|       |           |   |-- LocationCommand.java
|       |           |   |-- MotdCommand.java
|       |           |   |-- MuteCommand.java
|       |           |   |-- PlayersCommand.java
|       |           |   |-- SayCommand.java
|       |           |   |-- ScpCommand.java
|       |           |   |-- ScpTabCompleter.java
|       |           |   |-- StatusCommand.java
|       |           |   |-- UnmuteCommand.java
|       |           |-- listeners/
|       |           |   |-- JoinListener.java
|       |           |   |-- MuteListener.java
|       |           |   |-- PingListener.java
|       |           |-- util/
|       |               |-- TextFormatUtil.java
|       |-- resources/
|           |-- config.yml
|           |-- mutes.yml
|           |-- player-cache.yml
|           |-- plugin.yml
```

## Compilation and Build

This project utilizes Gradle 9.2.1 and requires a Java 21 toolchain.

To build the project:
```sh
gradle build
```
The compiled artifact will be located in `build/libs/ServerConfiguratorPlus.jar`.

## Author
**DeanBeanss**
Namespace: `dev.dean`
