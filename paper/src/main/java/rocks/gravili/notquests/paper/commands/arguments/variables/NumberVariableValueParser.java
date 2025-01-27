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

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.managers.expressions.NumberExpression;

@Getter
public class NumberVariableValueParser<C> implements ArgumentParser<C, NumberVariableValue> {
    private final NotQuests main;

    private final String identifier;
    private final StringVariableParser variableParser;
    private final SuggestionProvider<C> suggestionProvider;

    protected NumberVariableValueParser(String identifier, StringVariableParser variableParser, SuggestionProvider<CommandSender> suggestionProvider) {
        this.main = NotQuests.getInstance();
        this.identifier = identifier;
        this.variableParser = variableParser;
        this.suggestionProvider = suggestionProvider;
    }

    public static @NonNull NumberVariableValueParser numberVariableValueParser(String identifier, StringVariableParser variableParser, SuggestionProvider<CommandSender> suggestionProvider) {
        return new NumberVariableValueParser(identifier, variableParser, suggestionProvider);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull NumberVariableValue> parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull CommandInput commandInput) {
        if(commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("No input provided"));
        }
        final String input = commandInput.input();

        try{
            final NumberExpression numberExpression = new NumberExpression(main, input);
            if (commandContext.sender() instanceof Player player) {
                try {
                    numberExpression.calculateValue(main.getQuestPlayerManager().getOrCreateQuestPlayer(player.getUniqueId()));
                } catch (Exception e) {
                    if (main.getConfiguration().isDebug()) {
                        e.printStackTrace();
                    }
                    return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Expression: " + input + ". Error: " + e.toString()));
                }
            }
        } catch (Exception e){
            return ArgumentParseResult.failure(new IllegalArgumentException("Erroring Expression: " + input + ". Error: " + e.toString()));
        }


        return ArgumentParseResult.success(Pair.of(Double.parseDouble(input), input));
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        if (suggestionProvider == null) {
            return SuggestionProvider.noSuggestions();
        }
        return suggestionProvider;
    }
}