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
import rocks.gravili.notquests.paper.commands.arguments.variables.StringVariableValueParser;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PermissionVariable extends Variable<Boolean> {
    public PermissionVariable(NotQuests main) {
        super(main);
        if (main.getIntegrationsManager().isLuckpermsEnabled()) {
            setCanSetValue(true);
        }

        addRequiredString(StringVariableValueParser.of("Permission", null, (context, lastString) -> {
            main.getUtilManager().sendFancyCommandCompletion(context.sender(), lastString.input().split(" "), "[Permission Node]", "[...]");
            ArrayList<Suggestion> suggestions = new ArrayList<>();
            suggestions.add(Suggestion.suggestion("<Enter Permission node>"));
            return CompletableFuture.completedFuture(suggestions);
        }));
    }

    @Override
    public Boolean getValueInternally(QuestPlayer questPlayer, Object... objects) {
        return questPlayer != null
                && questPlayer.getPlayer().hasPermission(getRequiredStringValue("Permission"));
    }

    @Override
    public boolean setValueInternally(Boolean newValue, QuestPlayer questPlayer, Object... objects) {
        if (!main.getIntegrationsManager().isLuckpermsEnabled()) {
            return false;
        }

        if (newValue) {
            main.getIntegrationsManager()
                    .getLuckPermsManager()
                    .givePermission(questPlayer.getUniqueId(), getRequiredStringValue("Permission"));
        } else {
            main.getIntegrationsManager()
                    .getLuckPermsManager()
                    .denyPermission(questPlayer.getUniqueId(), getRequiredStringValue("Permission"));
        }

        return true;
    }

    @Override
    public List<Suggestion> getPossibleValues(QuestPlayer questPlayer, Object... objects) {
        return null;
    }

    @Override
    public String getPlural() {
        return "Permissions";
    }

    @Override
    public String getSingular() {
        return "Permission";
    }
}
