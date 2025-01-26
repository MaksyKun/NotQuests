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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.structs.Quest;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerParser<C> implements ArgumentParser<C, OfflinePlayer> {
    private boolean onlineOnly = false;
    private final NotQuests main;

    protected PlayerParser(NotQuests main, boolean onlineOnly) {
        this.onlineOnly = onlineOnly;
        this.main = main;
    }

    public static <C> @NonNull ParserDescriptor<C, OfflinePlayer> playerParser(final NotQuests main) {
        return ParserDescriptor.of(new PlayerParser<>(main, false), OfflinePlayer.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull OfflinePlayer> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new PlayerParseException(commandContext));
        }
        final OfflinePlayer foundPlayer = Bukkit.getOfflinePlayer(commandInput.input());
        if (foundPlayer.getName() == null) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Player: " + commandInput.input())); // TODO Language
        }
        if (this.onlineOnly && !foundPlayer.isOnline() ) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Only online players are selectable: " + commandInput.input()));
        }
        return ArgumentParseResult.success(foundPlayer);
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            List<Suggestion> playerNames = new java.util.ArrayList<>();
            for(OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                if(this.onlineOnly && !player.isOnline()) continue;
                if(player.getName() == null) continue;
                playerNames.add(Suggestion.suggestion(player.getName()));
            }

            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), context.rawInput().input().split(" "), "[Player Name]", "[...]");
            return CompletableFuture.completedFuture(playerNames);
        };
    }

    public static final class PlayerParseException extends ParserException {

        public PlayerParseException(@Nullable Throwable cause, @NonNull CommandContext<?> context, @NonNull CaptionVariable... captionVariables) {
            super(cause, PlayerParser.class, context, Caption.of(""), captionVariables);
        }

        public PlayerParseException(@NonNull CommandContext<?> context,  @NonNull CaptionVariable... captionVariables) {
            super(PlayerParser.class, context, Caption.of(""), captionVariables);
        }
    }
}