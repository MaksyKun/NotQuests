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

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.suggestion.Suggestion;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.arguments.variables.StringVariableValueParser;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static rocks.gravili.notquests.paper.commands.arguments.variables.StringVariableValueParser.stringVariableParser;

public class AdvancementVariable extends Variable<Boolean> {
    public AdvancementVariable(NotQuests main) {
        super(main);
        setCanSetValue(true);

        addRequiredString(StringVariableValueParser.of("Advancement", null,
                (context, input) -> {
                    main.getUtilManager().sendFancyCommandCompletion(context.sender(), input.input().split(" "), "[Advancement Name]", "[...]");
                    ArrayList<Suggestion> suggestions = new ArrayList<>();
                    Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();
                    while (advancements.hasNext()) {
                        Advancement advancement = advancements.next();
                        suggestions.add(Suggestion.suggestion(advancement.getKey().getKey()));
                    }
                    return CompletableFuture.completedFuture(suggestions);
                }));
    }

    @Override
    public Boolean getValueInternally(QuestPlayer questPlayer, Object... objects) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(getRequiredStringValue("Advancement"));
        if (namespacedKey == null) {
            return false;
        }
        Advancement advancement = Bukkit.getAdvancement(namespacedKey);
        if (advancement == null) {
            return false;
        }
        return questPlayer != null
                && questPlayer.getPlayer().getAdvancementProgress(advancement).isDone();
    }

    @Override
    public boolean setValueInternally(Boolean newValue, QuestPlayer questPlayer, Object... objects) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(getRequiredStringValue("Advancement"));
        if (namespacedKey == null) {
            return false;
        }
        Advancement advancement = Bukkit.getAdvancement(namespacedKey);
        if (advancement == null) {
            return false;
        }

        AdvancementProgress progress = questPlayer.getPlayer().getAdvancementProgress(advancement);

        if (newValue) {
            for (String criteria : progress.getRemainingCriteria()) {
                progress.awardCriteria(criteria);
            }
        } else {
            for (String criteria : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }

        return true;
    }

    @Override
    public List<Suggestion> getPossibleValues(QuestPlayer questPlayer, Object... objects) {
        return null;
    }

    @Override
    public String getPlural() {
        return "Advancements";
    }

    @Override
    public String getSingular() {
        return "Advancement";
    }
}
