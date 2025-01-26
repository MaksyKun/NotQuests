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

package rocks.gravili.notquests.paper.commands.arguments.variables;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import rocks.gravili.notquests.paper.NotQuests;

@Getter
public class CustomStringParser implements ArgumentParser<CommandSender, String> {
    private final NotQuests main;

    private String identifier;
    private ArgumentParseResult<@NonNull String> parseResult;
    private SuggestionProvider<CommandSender> suggestionProvider;

    protected CustomStringParser(String identifier, ArgumentParseResult<@NonNull String> parseResult, SuggestionProvider<CommandSender> suggestionProvider) {
        this.main = NotQuests.getInstance();
        this.identifier = identifier;
        this.parseResult = parseResult;
        this.suggestionProvider = suggestionProvider;
    }

    public static @NonNull CustomStringParser customStringParser(String identifier, ArgumentParseResult<@NonNull String> parseResult, SuggestionProvider<CommandSender> suggestionProvider) {
        return new CustomStringParser(identifier, parseResult, suggestionProvider);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull String> parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull CommandInput commandInput) {
        if(parseResult == null) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid CustomString: " + commandContext));
        }
        return parseResult;
    }


    @Override
    public @NonNull SuggestionProvider<CommandSender> suggestionProvider() {
        if(suggestionProvider == null) {
            return SuggestionProvider.noSuggestions();
        }
        return suggestionProvider;
    }
}