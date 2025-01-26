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

import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import rocks.gravili.notquests.paper.NotQuests;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class IntegerParser<C> implements ArgumentParser<C, Integer> {
    private final NotQuests main;
    private Integer withMin = 1;

    private SuggestionProvider<C> suggestionProvider;

    protected IntegerParser(NotQuests main, Integer withMin, SuggestionProvider<C> suggestionProvider) {
        this.main = main;
        this.withMin = withMin;
        this.suggestionProvider = suggestionProvider;
    }

    public static <C> @NonNull ParserDescriptor<C, Integer> integerParser(final NotQuests main, Integer withMin, SuggestionProvider<C> suggestionProvider) {
        return ParserDescriptor.of(new IntegerParser<>(main, withMin, suggestionProvider), Integer.class);
    }

    public static <C> @NonNull ParserDescriptor<C, Integer> integerParser(final NotQuests main, Integer withMin) {
        return ParserDescriptor.of(new IntegerParser<>(main, withMin, null), Integer.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Integer> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Integer: " + commandContext));
        }
        String rawInput = commandInput.input();

        try {
            int i = Integer.parseInt(rawInput);
            if (i >= withMin) {
                return ArgumentParseResult.success(i);
            }
        } catch (NumberFormatException e) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Integer: " + commandContext));
        }
        return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Integer: " + commandContext));
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return Objects.requireNonNullElseGet(suggestionProvider, () -> (context, input) -> {
            List<Suggestion> entries = new ArrayList<>();
            for (int i = withMin; i <= 100; i++) {
                entries.add(Suggestion.suggestion(String.valueOf(i)));
            }
            main.getUtilManager().sendFancyCommandCompletion((CommandSender) context.sender(), context.rawInput().input().split(" "), "[Player Name]", "[...]");
            return CompletableFuture.completedFuture(entries);
        });
    }
}