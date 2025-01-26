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

package rocks.gravili.notquests.paper.structs.variables;


import org.bukkit.entity.Player;
import org.incendo.cloud.suggestion.Suggestion;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.crunch.functional.EvaluationEnvironment;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.arguments.variables.CustomStringParser;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConditionVariable extends Variable<Boolean> {

    private final EvaluationEnvironment env = new EvaluationEnvironment();
    Variable<?> cachedVariable = null;
    private CompiledExpression exp;
    private int variableCounter = 0;
    private Player playerToEvaluate = null;
    private QuestPlayer questPlayerToEvaluate = null;

    public ConditionVariable(NotQuests main) {
        super(main);

        addRequiredString(CustomStringParser.customStringParser("Conditions", null, (context, lastString) -> {
            main.getUtilManager().sendFancyCommandCompletion(context.sender(), lastString.input().split(" "), "[Conditions(s) expression]", "[...]");

            ArrayList<Suggestion> suggestions = new ArrayList<>();
            for (String conditionIdentifier :
                    main.getConditionsYMLManager().getConditionsAndIdentifiers().keySet()) {
                if (lastString.input().endsWith(conditionIdentifier)) {
                    suggestions.add(Suggestion.suggestion(lastString + "&"));
                    suggestions.add(Suggestion.suggestion(lastString + "|"));
                } else {
                    suggestions.add(Suggestion.suggestion(conditionIdentifier));
                }
            }
            return CompletableFuture.completedFuture(suggestions);
        }));
    }

    public final String getExpression() {
        return getRequiredStringValue("Conditions");
    }

    @Override
    public Boolean getValueInternally(QuestPlayer questPlayer, Object... objects) {
        this.playerToEvaluate = questPlayer.getPlayer();
        this.questPlayerToEvaluate = questPlayer;
        initializeExpressionAndCachedVariable();

        return exp.evaluate() >= 0.98d;
    }

    public final String getExpressionAndGenerateEnv(String expressions) {
        boolean foundOne = false;
        for (final String conditionIdentifier :
                main.getConditionsYMLManager().getConditionsAndIdentifiers().keySet()) {
            if (!expressions.contains(conditionIdentifier)) {
                continue;
            }
            foundOne = true;

            variableCounter++;
            expressions = expressions.replace(conditionIdentifier, "var" + variableCounter);
            env.addLazyVariable(
                    "var" + variableCounter,
                    () -> main.getConditionsYMLManager()
                            .getCondition(conditionIdentifier)
                            .check(questPlayerToEvaluate)
                            .fulfilled()
                            ? 1
                            : 0);
        }
        if (!foundOne) {
            return expressions;
        }

        return getExpressionAndGenerateEnv(expressions);
    }

    public void initializeExpressionAndCachedVariable() {
        if (exp == null) {
            String expression = getExpressionAndGenerateEnv(getExpression());
            exp = Crunch.compileExpression(expression, env);
        }
    }

    @Override
    public boolean setValueInternally(Boolean newValue, QuestPlayer questPlayer, Object... objects) {
        return false;
    }

    @Override
    public List<Suggestion> getPossibleValues(QuestPlayer questPlayer, Object... objects) {
        return null;
    }

    @Override
    public String getPlural() {
        return "Conditions";
    }

    @Override
    public String getSingular() {
        return "Condition";
    }
}
