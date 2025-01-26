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

import org.incendo.cloud.suggestion.Suggestion;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.arguments.variables.CustomStringParser;
import rocks.gravili.notquests.paper.structs.FailedQuest;
import rocks.gravili.notquests.paper.structs.Quest;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This variable is true if the amount of times the player has previously failed this Quest is
 * equal or higher than the Quests max fails
 */
public class QuestReachedMaxFailsVariable extends Variable<Boolean> {
    public QuestReachedMaxFailsVariable(NotQuests main) {
        super(main);
        addRequiredString(CustomStringParser.customStringParser("Quest to check", null, (context, lastString) -> {
            main.getUtilManager().sendFancyCommandCompletion(context.sender(), lastString.input().split(" "), "[Quest Name]", "[...]");
            ArrayList<Suggestion> suggestions = new ArrayList<>();
            for (Quest quest : main.getQuestManager().getAllQuests()) {
                suggestions.add(Suggestion.suggestion(quest.getIdentifier()));
            }
            return CompletableFuture.completedFuture(suggestions);
        }));
    }

    @Override
    public Boolean getValueInternally(QuestPlayer questPlayer, Object... objects) {
        final Quest quest = main.getQuestManager().getQuest(getRequiredStringValue("Quest to check"));

        if (quest == null || questPlayer == null) {
            return false;
        }

        if (quest.getMaxFails() <= -1) {
            return false;
        } else if (quest.getMaxFails() == 0) {
            return true;
        }

        int failedAmount = 0; // only needed for maxFails

        for (final FailedQuest failedQuest : questPlayer.getFailedQuests()) {
            if (failedQuest.getQuest().equals(quest)) {
                failedAmount += 1;
            }
        }

        return failedAmount >= quest.getMaxFails();
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
        return "Quest reached max fails";
    }

    @Override
    public String getSingular() {
        return "Quest reached max fails";
    }
}
