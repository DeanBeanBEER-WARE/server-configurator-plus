# Role: Senior Folia Plugin Developer
# Task: Implement Nickname and Item-Formatting System

Implement a custom formatting system in 'ServerConfiguratorPlus' based on legacy '&' codes, including a special '&u' tag to disable italics.

## Technical Requirements
- Use **Adventure API** (Component, LegacyComponentSerializer) for all text processing.
- Use **Paper Brigadier API** (Lifecycle Events) for command registration.
- **Folia Compatibility:** Ensure all player/item modifications happen on the correct thread (Entity Scheduler).
- **Persistence:** Save/load nicknames in `player-cache.yml`.

## Feature: The Formatting Engine
- Implement a utility that converts String input with `&` codes to `Component`.
- **Special Code `&u`**: This must explicitly set `Decoration.ITALIC` to `FALSE`. In modern Adventure terms: `component.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)`.
- Support standard colors (`&0-&f`), hex (`&x...`), and formats (`&l`, `&o`, `&k`, `&m`, `&n`, `&r`).

## Commands to Implement
1. **/nickname <text>** (Aliases: `/nick`)
   - Applies formatting to the player's DisplayName.
   - Saves the raw formatted string to `player-cache.yml` and reapplies it on `PlayerJoinEvent`.
   
2. **/itemname <text>** (Aliases: `/in`)
   - Updates the name of the item in the player's main hand using the formatting engine.
   - Ensure it handles empty hands gracefully.

3. **Style Shortcuts (Optional/Contextual):**
   - Create logic for `/nc` (NameColor), `/ns`/`/nf` (NameStyle/Format), `/ic` (ItemColor), `/is`/`/if` (ItemStyle/Format). These should append or wrap the current name with the respective codes.

## Integration Steps
1. Update `PlayerCacheManager.java` to handle `nickname` data.
2. Create a new command class (e.g., `FormattingCommands.java`) for the logic.
3. Register commands in `ServerConfiguratorPlus.java` using the existing Brigadier setup.
4. Update `JoinListener.java` to apply nicknames from cache on join.

Refactor existing code if necessary to keep the 'Zero-warning' and 'Strict-lint' standard.