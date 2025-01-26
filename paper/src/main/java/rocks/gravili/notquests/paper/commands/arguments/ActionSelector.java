/*
 * NotQuests - A Questing plugin for Minecraft Servers
 * Copyright (C) 2022 Alessio Gravili
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

package rocks.gravili.notquests.paper.commands.arguments;

import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.jetbrains.annotations.NotNull;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.structs.actions.Action;

public class ActionsParser<C> implements ArgumentParser<C, Action> {

  private final NotQuests main;

  /** Constructs a new PluginsParser. */
  public ActionsParser(NotQuests main) {
    this.main = main;
  }

  @NotNull
  @Override
  public List<String> suggestions(@NotNull CommandContext<C> context, @NotNull String input) {
    List<String> actionNames =
            new java.util.ArrayList<>(
                    main.getActionsYMLManager().getActionsAndIdentifiers().keySet());
    final List<String> allArgs = context.rawInput();

    main.getUtilManager()
            .sendFancyCommandCompletion(
                    (CommandSender) context.sender(),
                    allArgs.toArray(new String[0]),
                    "[Action Name]",
                    "[...]");

    return actionNames;
  }

  @Override
  public @NonNull ArgumentParseResult<Action> parse(
          @NonNull CommandContext<@NonNull C> context, @NonNull Queue<@NonNull String> inputQueue) {
    if (inputQueue.isEmpty()) {
      return ArgumentParseResult.failure(
              new NoInputProvidedException(ActionSelector.ActionsParser.class, context));
    }
    final String input = inputQueue.peek();
    final Action foundAction = main.getActionsYMLManager().getAction(input);
    inputQueue.remove();

    if (foundAction == null) {
      return ArgumentParseResult.failure(
              new IllegalArgumentException("Action '" + input + "' does not exist!"));
    }

    return ArgumentParseResult.success(foundAction);
  }

  @Override
  public boolean isContextFree() {
    return true;
  }

  @Override
  public @NonNull ArgumentParseResult<@NonNull Action> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
    if (commandInput.isEmpty()) {
      return ArgumentParseResult.failure(
              new NoInputProvidedException(ActionSelector.ActionsParser.class, commandContext));
    }
    final String input = String.valueOf(commandInput.peek());
    final Action foundAction = main.getActionsYMLManager().getAction(input);
    inputQueue.remove();

    if (foundAction == null) {
      return ArgumentParseResult.failure(
              new IllegalArgumentException("Action '" + input + "' does not exist!"));
    }

    return ArgumentParseResult.success(foundAction);
  }
}