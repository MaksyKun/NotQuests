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

package rocks.gravili.notquests.paper.structs.variables.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.incendo.cloud.suggestion.Suggestion;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.arguments.variables.CustomStringParser;
import rocks.gravili.notquests.paper.structs.QuestPlayer;
import rocks.gravili.notquests.paper.structs.variables.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlaceholderAPIStringVariable extends Variable<String> {

    public PlaceholderAPIStringVariable(NotQuests main) {
        super(main);
        addRequiredString(CustomStringParser.customStringParser("Placeholder", null, (context, lastString) -> {
            main.getUtilManager().sendFancyCommandCompletion(context.sender(), lastString.input().split(" "), "[Quest Name]", "[...]");
            ArrayList<Suggestion> suggestions = new ArrayList<>();
            for (String identifier : PlaceholderAPI.getRegisteredIdentifiers()) {
                suggestions.add(Suggestion.suggestion("%" + identifier + "_"));
            }

            return CompletableFuture.completedFuture(suggestions);
        }));
    }

    @Override
    public String getValueInternally(QuestPlayer questPlayer, Object... objects) {
        if (questPlayer != null) {
            return PlaceholderAPI.setPlaceholders(
                    questPlayer.getPlayer(), getRequiredStringValue("Placeholder"));
        } else {
            return "";
        }
    }

    @Override
    public boolean setValueInternally(String newValue, QuestPlayer questPlayer, Object... objects) {
        return false;
    }

    @Override
    public List<Suggestion> getPossibleValues(QuestPlayer questPlayer, Object... objects) {
        return null;
    }

    @Override
    public String getPlural() {
        return getRequiredStringValue("Placeholder");
    }

    @Override
    public String getSingular() {
        return getRequiredStringValue("Placeholder");
    }
}
