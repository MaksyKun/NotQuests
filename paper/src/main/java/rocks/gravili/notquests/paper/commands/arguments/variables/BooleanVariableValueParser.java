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
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.managers.expressions.NumberExpression;
import rocks.gravili.notquests.paper.structs.variables.Variable;

import static rocks.gravili.notquests.paper.commands.arguments.variables.StringVariableParser.stringVariableParser;

@Getter
public class BooleanVariableValueParser<C> implements ArgumentParser<C, String> {
    private final NotQuests main;

    private final String identifier;
    private final StringVariableParser<C> variableParser;

    protected BooleanVariableValueParser(String identifier, StringVariableParser<C> variableParser) {
        this.main = NotQuests.getInstance();
        this.identifier = identifier;
        this.variableParser = variableParser;
    }

    public static <C> @NonNull StringVariableParser<C> booleanStringVariableParser(String identifier, Variable<?> variable) {
        return stringVariableParser(identifier, variable);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull String> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("No input provided"));
        }
        final String input = commandInput.peekString();
        try {
            final NumberExpression numberExpression = new NumberExpression(main, input);

            if (commandContext.sender() instanceof Player player) {
                try {
                    numberExpression.calculateBooleanValue(main.getQuestPlayerManager().getOrCreateQuestPlayer(player.getUniqueId()));
                } catch (Exception e) {
                    if (main.getConfiguration().isDebug()) {
                        e.printStackTrace();
                    }
                    return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Expression: " + input + ". Error: " + e));
                }
            }
        } catch (Exception e) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Erroring Expression: " + input + ". Error: " + e));
        }
        return ArgumentParseResult.success(input);
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        if(variableParser == null) {
            return SuggestionProvider.noSuggestions();
        }
        return variableParser.suggestionProvider();
    }
}