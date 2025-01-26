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

import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.managers.data.Category;
import rocks.gravili.notquests.paper.structs.variables.Variable;
import rocks.gravili.notquests.paper.structs.variables.VariableDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BooleanVariableValueParser<C> implements ArgumentParser<C, Boolean> {
    private final NotQuests main;

    protected BooleanVariableValueParser(NotQuests main) {
        this.main = main;
    }

    public static <C> @NonNull ParserDescriptor<C, Boolean> categoryParser(final NotQuests main) {
        return ParserDescriptor.of(new BooleanVariableValueParser<>(main), Boolean.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Boolean> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.isEmpty()) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid Category: " + commandContext));
        }
        String rawInput = commandInput.input();
        List<Category> entries = main.getDataManager().getCategories();
        Category foundCategory;
        for (Category category : entries) {
            if (category.getCategoryName().equalsIgnoreCase(rawInput)) {
                foundCategory = category;
                return ArgumentParseResult.success(foundCategory);
            }
        }
        return ArgumentParseResult.failure(new IllegalArgumentException("No Category found: " + commandContext));
    }


    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) -> {
            final List<Suggestion> completions = new ArrayList<>();
            completions.add(Suggestion.suggestion("true"));
            completions.add(Suggestion.suggestion("false"));

            String rawInput = input.input();
            for(final String variableString : main.getVariablesManager().getVariableIdentifiers()) {
                final Variable<?> variable = main.getVariablesManager().getVariableFromString(variableString);
                if (variable == null || (variable.getVariableDataType() != VariableDataType.NUMBER && variable.getVariableDataType() != VariableDataType.BOOLEAN )) {
                    continue;
                }
                if(variable.getRequiredStrings().isEmpty() && variable.getRequiredNumbers().isEmpty() && variable.getRequiredBooleans().isEmpty() && variable.getRequiredBooleanFlags().isEmpty()){
                    completions.add(Suggestion.suggestion(variableString));
                }else{
                    if(!rawInput.endsWith(variableString+"(")){
                        if(rawInput.endsWith(",") && rawInput.contains(variableString+"(")){
                            for(StringParser<CommandSender> stringArgument : variable.getRequiredStrings()){
                                if(!rawInput.contains(stringArgument.getName())){
                                    completions.add(Suggestion.suggestion(input + stringArgument.getName() + ":"));
                                }
                            }
                            for(NumberVariableValueArgument<CommandSender> numberVariableValueArgument : variable.getRequiredNumbers()){
                                if(!input.contains(numberVariableValueArgument.getName())){
                                    completions.add(Suggestion.suggestion(input + numberVariableValueArgument.getName() + ":"));
                                }
                            }
                            for(BooleanVariableValueArgument<CommandSender> booleanArgument : variable.getRequiredBooleans()){
                                if(!input.contains(booleanArgument.name())){
                                    completions.add(Suggestion.suggestion(input + booleanArgument.name() + ":"));
                                }
                            }
                            for(CommandFlag<Void> flag : variable.getRequiredBooleanFlags()){
                                if(!input.contains(flag.name())){
                                    completions.add(Suggestion.suggestion(input + "--" + flag.name() + ""));
                                }
                            }
                        }else if(!input.endsWith(")")){
                            if(input.contains(variableString+"(") && (!input.contains(")") || (input.lastIndexOf("(") < input.lastIndexOf(")"))) ){
                                final String subStringAfter = input.substring(input.indexOf(variableString+"("));

                                for(final StringArgument<CommandSender> stringArgument : variable.getRequiredStrings()){
                                    if(subStringAfter.contains(":")){
                                        if(subStringAfter.endsWith(":")){
                                            final List<String> suggestions = stringArgument.getSuggestionsProvider().apply(commandSenderCommandContext, "");

                                            for(final String suggestion : suggestions){
                                                completions.add(Suggestion.suggestion(input + suggestion));
                                            }
                                        }else{
                                            final String[] splitDoubleDots = subStringAfter.split(":");
                                            final String stringAfterLastDoubleDot = splitDoubleDots[splitDoubleDots.length - 1];

                                            final List<String> suggestions = stringArgument.getSuggestionsProvider().apply(commandSenderCommandContext, stringAfterLastDoubleDot);


                                            for(final String suggestion : suggestions){

                                                completions.add(Suggestion.suggestion(input.substring(0, input.length()-stringAfterLastDoubleDot.length()-1) + ":" + suggestion));
                                            }
                                        }
                                    }else{
                                        completions.add(Suggestion.suggestion(variableString+"(" + stringArgument.getName() + ":"));
                                    }
                                }
                                for(final NumberVariableValueArgument<CommandSender> numberVariableValueArgument : variable.getRequiredNumbers()) {
                                    if (subStringAfter.contains(":")) {
                                        if(subStringAfter.endsWith(":")){
                                            final List<String> suggestions = numberVariableValueArgument.getSuggestionsProvider().apply(commandSenderCommandContext, "");

                                            for(final String suggestion : suggestions){
                                                completions.add(Suggestion.suggestion(input + suggestion));
                                            }
                                        }else{

                                            final String[] splitDoubleDots = subStringAfter.split(":");
                                            final String stringAfterLastDoubleDot = splitDoubleDots[splitDoubleDots.length - 1];

                                            final List<String> suggestions = numberVariableValueArgument.getSuggestionsProvider().apply(commandSenderCommandContext, stringAfterLastDoubleDot);


                                            for(final String suggestion : suggestions){

                                                completions.add(Suggestion.suggestion(input.substring(0, input.length()-stringAfterLastDoubleDot.length()-1) + ":" + suggestion));
                                            }
                                        }
                                    } else {
                                        completions.add(Suggestion.suggestion(variableString+"(" + numberVariableValueArgument.getName() + ":"));
                                    }
                                }for(BooleanVariableValueArgument<CommandSender> booleanArgument : variable.getRequiredBooleans()) {
                                    if (subStringAfter.contains(":")) {
                                        if(subStringAfter.endsWith(":")){
                                            final List<String> suggestions = booleanArgument.suggestionProvider().suggestionsFuture(commandSenderCommandContext, "");

                                            for(final String suggestion : suggestions){
                                                completions.add(Suggestion.suggestion(input + suggestion));
                                            }
                                        }else{

                                            final String[] splitDoubleDots = subStringAfter.split(":");
                                            final String stringAfterLastDoubleDot = splitDoubleDots[splitDoubleDots.length - 1];

                                            final List<String> suggestions = booleanArgument.suggestionProvider().apply(commandSenderCommandContext, stringAfterLastDoubleDot);


                                            for(final String suggestion : suggestions){

                                                completions.add(Suggestion.suggestion(input.substring(0, input.length()-stringAfterLastDoubleDot.length()-1) + ":" + suggestion));
                                            }
                                        }
                                    } else {
                                        completions.add(Suggestion.suggestion(variableString+"(" + booleanArgument.getName() + ":"));
                                    }
                                }for(CommandFlag<Void> flag : variable.getRequiredBooleanFlags()){
                                    completions.add(Suggestion.suggestion(variableString+"(--" + flag.getName() + ""));
                                }
                            }else{
                                completions.add(Suggestion.suggestion(variableString+"("));
                            }
                        }
                    }else{//Moree completionss
                        for(StringArgument<CommandSender> stringArgument : variable.getRequiredStrings()){
                            completions.add(Suggestion.suggestion(variableString+"(" + stringArgument.getName() + ":"));
                        }
                        for(NumberVariableValueArgument<CommandSender> numberVariableValueArgument : variable.getRequiredNumbers()){
                            completions.add(Suggestion.suggestion(variableString+"(" + numberVariableValueArgument.getName() + ":"));
                        }
                        for(BooleanVariableValueArgument<CommandSender> booleanArgument : variable.getRequiredBooleans()){
                            completions.add(Suggestion.suggestion(variableString+"(" + booleanArgument.getName() + ":"));
                        }
                        for(CommandFlag<Void> flag : variable.getRequiredBooleanFlags()){
                            completions.add(Suggestion.suggestion(variableString+"(--" + flag.getName() + ""));
                        }

                    }
                }
            }
        };
    }
}