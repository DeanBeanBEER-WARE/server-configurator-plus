# Session Context: ServerConfiguratorPlus

## Project Overview
- **Name:** ServerConfiguratorPlus
- **Author:** DeanBeanss
- **Namespace:** `dev.dean`
- **Platform:** Folia 1.21.8
- **Java Version:** 21
- **Build Tool:** Gradle 9.2.1

## Technical Details
- **Architecture:** Optimized for Folia's multi-threaded region-based architecture.
- **Command Engine:** Migrated to the modern **Paper Brigadier API** via Lifecycle Events for superior stability and integration.
- **Text Engine:** Fully migrated to **Adventure API** (Kyori) for modern, warning-free text handling.
- **Toolchain:** Configured Gradle to use Java 21 toolchain with strict `-Xlint` checks.
- **Dependencies:** 
    - `dev.folia:folia-api:1.21.8-R0.1-SNAPSHOT`
- **Encoding:** UTF-8

## Features
- **Two-Line MOTD System:**
    - **Permanent Server Name:** Static string configured in `config.yml`.
    - **Rotating MOTD:** Rotates every 5 minutes using Folia's `GlobalRegionScheduler`.
    - Personalization: Replaces `%player%` with the user's name (cached by IP).
- **Server Icons:**
    - Supports `.png`, `.jpg`, `.jpeg`, `.webp`, and `.avif`.
    - **Auto-Processing:** Resizes icons to 64x64 and converts to PNG in-memory.
    - Randomly selects an icon from the `icons/` folder for each ping.
- **Dynamic Slots:**
    - "One More": Always shows 1 more slot than current players.
    - "Custom": Fixed max player count.
    - Custom Version Text: Replaces protocol version text with custom Adventure-based text.
- **Maintenance Mode:**
    - Toggle via `/scp maintenance`.
    - Custom Maintenance MOTD and kick messages stored as Adventure Components.
    - Enforced via asychronous pre-login event for Folia compatibility.
- **Performance:**
    - Concurrent caching for IP->Name mappings.
    - Thread-safe icon management.
    - Zero-warning codebase with optimized modern Java features.
- **Automated Announcements:**
    - Periodic chat broadcasts to all online players.
    - Configurable interval and message list.
    - Supports legacy color codes (`&`) via Adventure's LegacyComponentSerializer.
    - Optimized for Folia using `GlobalRegionScheduler`.

## Commands
- `/motd`: Displays the current active rotating MOTD.
- `/players`: Displays the number of online players and their names.
- `/status`: Displays comprehensive server status information.
- `/kill`: Kills the player who executes the command.
- `/dupe`: Broadcasts the player's current coordinates.
- `/rules`: Broadcasts the player's current coordinates.
- `/nickname` (alias `/nick`): Sets the player's nickname, supporting legacy colors and `&u` to disable italics.
- `/itemname` (alias `/in`): Sets the display name of the item in the main hand with legacy formatting.
- `/scp`: Admin command.
    - `reload`: Reloads all configs and icons.
    - `maintenance`: Toggles maintenance mode.

## File Structure
- `src/main/resources/config.yml`: Main configuration.
- `src/main/resources/player-cache.yml`: IP-Name mapping database.
- `src/main/java/dev/dean/`:
    - `ServerConfiguratorPlus.java`: Main class with Brigadier registration.
    - `MotdManager.java`: Rotation logic using `GlobalRegionScheduler`.
    - `AnnouncementManager.java`: Logic for periodic chat broadcasts.
    - `MaintenanceManager.java`: Maintenance state using Adventure Components.
    - `IconManager.java`: Icon loading and processing.
    - `PlayerCacheManager.java`: Concurrent IP caching.
    - `listeners/`:
        - `PingListener.java`: Customizes server list appearance using modern Paper APIs.
        - `JoinListener.java`: Handles asychronous pre-login and caching.

## Build Command
```bash
gradle build
```
The resulting JAR file can be found in `build/libs/ServerConfiguratorPlus.jar`.

---

## Recent Code Improvements (January 21, 2026)

### 1. Tab-Completion for /scp Command
**Added:** `ScpTabCompleter.java`
- Implemented tab completion for the `/scp` command
- Provides suggestions for `reload` and `maintenance` subcommands
- Improves administrator UX and reduces typos
- Registered in `ServerConfiguratorPlus.java`

### 2. Centralized Reload Mechanism
**Modified:** `ServerConfiguratorPlus.java`
- Added `reloadPluginConfig()` method for centralized configuration reloading
- Reloads all managers in one place:
  - MotdManager
  - IconManager
  - AnnouncementManager
  - MaintenanceManager
- Comprehensive error handling with try-catch
- Detailed logging for each manager reload
- Improves maintainability and ensures no manager is forgotten

### 3. AnnouncementManager Reload Optimization
**Modified:** `AnnouncementManager.java`
- Updated `loadConfig()` method to automatically restart announcements
- Announcements now apply immediately after configuration reload
- No plugin restart required for announcement changes
- Ensures consistent behavior with other managers

### 4. Consistent Message Prefixes
**Modified:** `ScpCommand.java`
- Added `[SCP]` prefix to all ScpCommand messages for better recognition
- Messages include:
  - `[SCP] Usage: /scp <reload|maintenance>`
  - `[SCP] All configurations reloaded successfully!`
  - `[SCP] Maintenance mode is now: ON/OFF`
  - `[SCP] Unknown subcommand...`
- Provides professional appearance and clear plugin message attribution

### 5. LocationCommand Code Cleanup
**Modified:** `LocationCommand.java`
- Improved JavaDoc comments to clarify purpose
- Added inline comments for better code readability
- Extracted coordinate formatting into separate variable
- Clarified usage for `/dupe` and `/rules` commands
- Enhanced maintainability without changing functionality

### Summary of Changes
**New Files:**
- `src/main/java/dev/dean/commands/ScpTabCompleter.java`

**Modified Files:**
- `src/main/java/dev/dean/ServerConfiguratorPlus.java`
- `src/main/java/dev/dean/AnnouncementManager.java`
- `src/main/java/dev/dean/commands/ScpCommand.java`
- `src/main/java/dev/dean/commands/LocationCommand.java`

**Build Status:** BUILD SUCCESSFUL

---

## Nickname and Item-Formatting System (March 27, 2026)

### 1. New Formatting Engine
**Added:** `TextFormatUtil.java`
- Utility class using Adventure API to parse legacy `&` color codes.
- Implements custom `&u` code logic to explicitly set `TextDecoration.ITALIC` to `FALSE`.

### 2. Player Cache Update
**Modified:** `PlayerCacheManager.java`
- Added support for saving and loading player nicknames via UUIDs alongside IPs.

### 3. New Commands
**Added:** `FormattingCommands.java`
- Implemented `/nickname` to set and save a player's display name.
- Implemented `/itemname` to format the name of the item in the player's main hand.
- Ensured item modification runs explicitly on the player's Folia entity scheduler.

### 4. Join Event Integration
**Modified:** `JoinListener.java`
- Applies the formatted nickname to the player on join.

### Summary of Changes
**New Files:**
- `src/main/java/dev/dean/util/TextFormatUtil.java`
- `src/main/java/dev/dean/commands/FormattingCommands.java`

**Modified Files:**
- `src/main/java/dev/dean/PlayerCacheManager.java`
- `src/main/java/dev/dean/ServerConfiguratorPlus.java`
- `src/main/java/dev/dean/listeners/JoinListener.java`
- `src/main/resources/plugin.yml`

**Build Status:** BUILD SUCCESSFUL

---

## Config Reset Issue Fix (February 6, 2026)

### Problem Description
Manual edits to the `config.yml` file were being reset to default values every time the server restarted. This prevented administrators from making persistent configuration changes without modifying the plugin source code.

### Root Cause Analysis
The issue was located in `MotdManager.java` within the `loadConfig()` method. The method was calling `plugin.saveDefaultConfig()` on every execution, which occurred during plugin initialization on each server startup. The Bukkit API method `saveDefaultConfig()` was incorrectly placed in a reload context, causing the configuration file to be overwritten with default values from the plugin resources.

### Solution Implementation

**Modified Files:**
- `src/main/java/dev/dean/MotdManager.java`
- `src/main/java/dev/dean/ServerConfiguratorPlus.java`

**Changes to MotdManager.java:**
- Removed `plugin.saveDefaultConfig()` call from the `loadConfig()` method
- Updated JavaDoc to clarify that the method only reads configuration without modifying the file
- The method now only calls `plugin.reloadConfig()` to load the current file state

**Changes to ServerConfiguratorPlus.java:**
- Added `saveDefaultConfig()` call in the `onEnable()` method before manager initialization
- Added descriptive comments explaining that this call only creates the file if it does not exist
- The Bukkit API method `saveDefaultConfig()` inherently checks for file existence and will not overwrite existing files

### Technical Details
The `saveDefaultConfig()` method from the Bukkit API is designed to be idempotent: it only creates the default configuration file if it does not already exist. By moving this call to the main plugin initialization and removing it from the reload logic, the configuration file is only created once on first startup. Subsequent server restarts will preserve all manual edits made by administrators.

The `player-cache.yml` file was already correctly implemented in `PlayerCacheManager.java` using `plugin.saveResource("player-cache.yml", false)`, where the `false` parameter prevents overwriting existing files.

**Build Status:** BUILD SUCCESSFUL
