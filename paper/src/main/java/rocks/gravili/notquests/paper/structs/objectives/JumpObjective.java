/*
 * NotQuests - A Questing plugin for Minecraft Servers
 * Copyright (C) 2021-2022 Alessio Gravili
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rocks.gravili.notquests.paper.structs.objectives;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.Command;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.arguments.variables.NumberVariableValueArgument;
import rocks.gravili.notquests.paper.structs.ActiveObjective;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.Map;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class JumpObjective extends Objective {

  public JumpObjective(NotQuests main) {
    super(main);
  }

  public static void handleCommands(
      NotQuests main,
      LegacyPaperCommandManager<CommandSender> manager,
      Command.Builder<CommandSender> addObjectiveBuilder,
      final int level) {
    manager.command(addObjectiveBuilder
            .required("amount", integerParser(1), Description.of("Amount of times the player needs to jump."))
            .handler(
                (context) -> {
                  final String amountExpression = context.get("amount");

                  JumpObjective jumpObjective = new JumpObjective(main);
                  jumpObjective.setProgressNeededExpression(amountExpression);

                  main.getObjectiveManager().addObjective(jumpObjective, context, level);
                }));
  }

  @Override
  public void onObjectiveUnlock(
      final ActiveObjective activeObjective,
      final boolean unlockedDuringPluginStartupQuestLoadingProcess) {}

  @Override
  public void onObjectiveCompleteOrLock(
      final ActiveObjective activeObjective,
      final boolean lockedOrCompletedDuringPluginStartupQuestLoadingProcess,
      final boolean completed) {}

  @Override
  public String getTaskDescriptionInternal(
      final QuestPlayer questPlayer, final @Nullable ActiveObjective activeObjective) {
    return main.getLanguageManager()
        .getString(
            "chat.objectives.taskDescription.jump.base",
            questPlayer,
            activeObjective,
            Map.of(
                "%AMOUNTOFJUMPS%",
                ""
                    + (activeObjective != null
                        ? activeObjective.getProgressNeeded()
                        : getProgressNeededExpression().getRawExpression())));
  }

  @Override
  public void save(FileConfiguration configuration, String initialPath) {}

  @Override
  public void load(FileConfiguration configuration, String initialPath) {}
}
