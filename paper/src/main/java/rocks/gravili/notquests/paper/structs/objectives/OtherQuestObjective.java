/*
 * NotQuests - A Questing plugin for Minecraft Servers
 * Copyright (C) 2021-2022 Alessio Gravili
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

package rocks.gravili.notquests.paper.structs.objectives;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.Command;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.arguments.variables.NumberVariableValueArgument;
import rocks.gravili.notquests.paper.structs.ActiveObjective;
import rocks.gravili.notquests.paper.structs.Quest;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.Map;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static rocks.gravili.notquests.paper.commands.arguments.QuestParser.questParser;

public class OtherQuestObjective extends Objective {
    private String otherQuestName = "";
    private boolean countPreviousCompletions = false;

    public OtherQuestObjective(NotQuests main) {
        super(main);
    }

    public static void handleCommands(
            NotQuests main,
            LegacyPaperCommandManager<CommandSender> manager,
            Command.Builder<CommandSender> addObjectiveBuilder,
            final int level) {
        manager.command(addObjectiveBuilder
                .required("other quest name", questParser(main), Description.of("Name of the other Quest the player has to complete"))
                .required("amount", integerParser(1), Description.of("Amount of times the Quest needs to be completed"))
                .flag(manager.flagBuilder("countPreviouslyCompletedQuests").withDescription(Description.of("Makes it so quests completed before this OtherQuest objective becomes active will be counted towards the progress too.")))
                .handler((context) -> {
                    final Quest otherQuest = context.get("other quest name");
                    final String amountExpression = context.get("amount");
                    final boolean countPreviouslyCompletedQuests =
                            context.flags().isPresent("countPreviouslyCompletedQuests");

                    OtherQuestObjective otherQuestObjective = new OtherQuestObjective(main);

                    otherQuestObjective.setOtherQuestName(otherQuest.getIdentifier());
                    otherQuestObjective.setCountPreviousCompletions(countPreviouslyCompletedQuests);
                    otherQuestObjective.setProgressNeededExpression(amountExpression);

                    main.getObjectiveManager().addObjective(otherQuestObjective, context, level);
                }));
    }

    @Override
    public String getTaskDescriptionInternal(
            final QuestPlayer questPlayer, final @Nullable ActiveObjective activeObjective) {
        return main.getLanguageManager()
                .getString(
                        "chat.objectives.taskDescription.otherQuest.base",
                        questPlayer,
                        activeObjective,
                        Map.of("%OTHERQUESTNAME%", getOtherQuest().getDisplayNameOrIdentifier()));
    }

    @Override
    public void save(FileConfiguration configuration, String initialPath) {
        configuration.set(initialPath + ".specifics.otherQuestName", getOtherQuestName());
        configuration.set(
                initialPath + ".specifics.countPreviousCompletions", isCountPreviousCompletions());
    }

    @Override
    public void onObjectiveUnlock(
            final ActiveObjective activeObjective,
            final boolean unlockedDuringPluginStartupQuestLoadingProcess) {
    }

    @Override
    public void onObjectiveCompleteOrLock(
            final ActiveObjective activeObjective,
            final boolean lockedOrCompletedDuringPluginStartupQuestLoadingProcess,
            final boolean completed) {
    }

    public final String getOtherQuestName() {
        return otherQuestName;
    }

    public void setOtherQuestName(final String otherQuestName) {
        this.otherQuestName = otherQuestName;
    }

    public final Quest getOtherQuest() {
        return main.getQuestManager().getQuest(otherQuestName);
    }

    public final boolean isCountPreviousCompletions() {
        return countPreviousCompletions;
    }

    public void setCountPreviousCompletions(final boolean countPreviousCompletions) {
        this.countPreviousCompletions = countPreviousCompletions;
    }

    @Override
    public void load(FileConfiguration configuration, String initialPath) {
        otherQuestName = configuration.getString(initialPath + ".specifics.otherQuestName");
        countPreviousCompletions =
                configuration.getBoolean(initialPath + ".specifics.countPreviousCompletions");
    }
}
