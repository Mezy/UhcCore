# UhcCore
Automates UHC games on a dedicated 1.8 - 1.15 server

## List of commands
Command | Description | Permission
:---: | --- | :---:
/uhccore reload | This command is used to reload the config files. | uhc-core.commands.reload
/uhccore update | This command is used to update the plugin to the latest version. | uhc-core.commands.update
/uhccore version | This command is used to check the plugin version. | -
/chat | This command is used to toggle between global and team chat. | -
/teleport <player> | This command can be used by spectating players. | Enabled for all spectators by default. (When disabled use the permission 'uhc-core.commands.teleport-admin')
/teleport \<x> \<y> \<z> | This command can be used to teleport to coordinates. (If you want to use the vanilla /tp command use '/minecraft:tp') | uhc-core.commands.teleport-admin
/spectate | You might use this command to toggle spectator mode, when in spectator mode you will automatically be put in spectator mode at the start of a game. | uhc-core.commands.spectate
/start | This command is used to force start games. | uhc-core.commands.start
/scenarios | This command is used to view active / edit scenarios. | uhc-core.scenarios.edit (To edit)
/teaminventory | This command can be used when the team inventory scenario is enabled. | -
/hub | This command can be used to return to the bungeecord lobby server (When enabled). | -
/iteminfo | This command can be used to generate the item json used in your config files for kits / crafts. | uhc-core.commands.iteminfo
/upload \<file> | This command can be used to upload a config file to haste bin, you might be requested to do this when you ask for support. | uhc-core.commands.iteminfo
/revive \<player> \[clear] | This command can be used to revive death players. You can optionally use clear as 2nd argument to revive the player without spawning back in their items. | uhc-core.commands.revive
/crafts | This command can be used to view custom crafts or to create them. | uhc-core.commands.crafts.create (Only needed for creating custom crafts)
 /top | This command teleports players to the highest block above their head. | uhc-core.commands.top 

## Using the API
You can add the UhcCore API to your own plugin using maven
```
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```
```
<dependency>
  <groupId>com.github.Mezy</groupId>
  <artifactId>UhcCore</artifactId>
  <version>v1.15.10</version>
</dependency>
```
