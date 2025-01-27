package rocks.gravili.notquests.paper.structs.actions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.ArrayList;

import static rocks.gravili.notquests.paper.commands.arguments.CommandParser.commandParser;

public class PlayerCommandAction extends Action {
  private String playerCommand = "";

  public PlayerCommandAction(final NotQuests main) {
    super(main);
  }

  public static void handleCommands(
      NotQuests main,
      LegacyPaperCommandManager<CommandSender> manager,
      Command.Builder<CommandSender> builder,
      ActionFor actionFor) {
    manager.command(builder.required("Player Command", commandParser(main), Description.of("Command which will be executed from the player's perspective. A '/' at the beginning is not required."))
            .handler(
                (context) -> {
                  final String playerCommand =
                      String.join(" ", (String[]) context.get("Player Command"));

                  PlayerCommandAction playerCommandAction = new PlayerCommandAction(main);
                  playerCommandAction.setPlayerCommand(playerCommand);

                  main.getActionManager().addAction(playerCommandAction, context, actionFor);
                }));
  }

  @Override
  public void executeInternally(final QuestPlayer questPlayer, Object... objects) {
    if (playerCommand.isBlank()) {
      main.getLogManager()
          .warn("Tried to execute PlayerCommand action with invalid player command.");
      return;
    }

    final Player player = questPlayer.getPlayer();
    if(player == null) {
      main.getLogManager()
          .warn("Tried to execute PlayerCommand action with invalid player.");
      return;
    }

    final String rewardPlayerCommand =
        main.getUtilManager()
            .applyPlaceholders(
                playerCommand, questPlayer.getPlayer(), questPlayer, getObjectiveHolder(), objects);


    if (Bukkit.isPrimaryThread()) {
      player.performCommand(playerCommand);
    } else {
      Bukkit.getScheduler()
          .runTask(main.getMain(), () -> player.performCommand(playerCommand));
    }
  }

  @Override
  public void save(FileConfiguration configuration, String initialPath) {
    configuration.set(initialPath + ".specifics.playerCommand", getPlayerCommand());
  }

  @Override
  public void load(final FileConfiguration configuration, String initialPath) {
    this.playerCommand = configuration.getString(initialPath + ".specifics.playerCommand");
  }

  @Override
  public void deserializeFromSingleLineString(ArrayList<String> arguments) {
    this.playerCommand = String.join(" ", arguments);
  }

  public final String getPlayerCommand() {
    return playerCommand;
  }

  public void setPlayerCommand(final String playerCommand) {
    this.playerCommand = playerCommand;
  }

  @Override
  public String getActionDescription(final QuestPlayer questPlayer, final Object... objects) {
    return "Player Command: " + getPlayerCommand();
  }
}
