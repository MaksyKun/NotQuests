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
import rocks.gravili.notquests.paper.commands.arguments.wrappers.ItemStackSelection;
import rocks.gravili.notquests.paper.structs.ActiveObjective;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.Map;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static rocks.gravili.notquests.paper.commands.arguments.ItemStackSelectionParser.itemStackSelectionParser;

public class BreakBlocksObjective extends Objective {
    private ItemStackSelection itemStackSelection;
    private boolean deductIfBlockIsPlaced = true;

    public BreakBlocksObjective(NotQuests main) {
        super(main);
    }

    public static void handleCommands(
            NotQuests main,
            LegacyPaperCommandManager<CommandSender> manager,
            Command.Builder<CommandSender> addObjectiveBuilder,
            final int level) {
        manager.command(addObjectiveBuilder
                .required("materials", itemStackSelectionParser(main), Description.description("Material of the block which needs to be broken"))
                .required("amount", integerParser(1), Description.description("Amount of blocks which need to be broken"))
                .flag(manager.flagBuilder("doNotDeductIfBlockIsPlaced").withDescription(Description.of("Makes it so Quest progress is not removed if the block is placed")))
                .handler(
                        (context) -> {
                            final String amountExpression = context.get("amount");
                            final boolean deductIfBlockIsPlaced =
                                    !context.flags().isPresent("doNotDeductIfBlockIsPlaced");

                            final ItemStackSelection itemStackSelection = context.get("materials");

                            BreakBlocksObjective breakBlocksObjective = new BreakBlocksObjective(main);
                            breakBlocksObjective.setItemStackSelection(itemStackSelection);

                            breakBlocksObjective.setProgressNeededExpression(amountExpression);
                            breakBlocksObjective.setDeductIfBlockIsPlaced(deductIfBlockIsPlaced);

                            main.getObjectiveManager().addObjective(breakBlocksObjective, context, level);
                        }));
    }

    public final ItemStackSelection getItemStackSelection() {
        return itemStackSelection;
    }

    public void setItemStackSelection(final ItemStackSelection itemStackSelection) {
        this.itemStackSelection = itemStackSelection;
    }

    @Override
    public String getTaskDescriptionInternal(
            final QuestPlayer questPlayer, final @Nullable ActiveObjective activeObjective) {
        return main.getLanguageManager()
                .getString(
                        "chat.objectives.taskDescription.breakBlocks.base",
                        questPlayer,
                        activeObjective,
                        Map.of("%BLOCKTOBREAK%", getItemStackSelection().getAllMaterialsListedTranslated("main")));
    }

    public void setDeductIfBlockIsPlaced(final boolean deductIfBlockIsPlaced) {
        this.deductIfBlockIsPlaced = deductIfBlockIsPlaced;
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

    public final boolean isDeductIfBlockPlaced() {
        return deductIfBlockIsPlaced;
    }

    @Override
    public void save(FileConfiguration configuration, String initialPath) {
        getItemStackSelection()
                .saveToFileConfiguration(configuration, initialPath + ".specifics.itemStackSelection");

        configuration.set(initialPath + ".specifics.deductIfBlockPlaced", isDeductIfBlockPlaced());
    }

    @Override
    public void load(FileConfiguration configuration, String initialPath) {
        this.itemStackSelection = new ItemStackSelection(main);
        itemStackSelection.loadFromFileConfiguration(
                configuration, initialPath + ".specifics.itemStackSelection");

        // Convert old to new
        if (configuration.contains(initialPath + ".specifics.nqitem")
                || configuration.contains(initialPath + ".specifics.blockToBreak.material")) {
            final String nqItemName = configuration.getString(initialPath + ".specifics.nqitem", "");

            if (nqItemName.isBlank()) {
                itemStackSelection.addMaterialName(
                        configuration.getString(initialPath + ".specifics.blockToBreak.material", ""));
            } else {
                itemStackSelection.addNqItemName(nqItemName);
            }
            itemStackSelection.saveToFileConfiguration(
                    configuration, initialPath + ".specifics.itemStackSelection");
            configuration.set(initialPath + ".specifics.nqitem", null);
            configuration.set(initialPath + ".specifics.blockToBreak.material", null);

            main.getLogManager().info("Converting old BreakBlocksObjective to new one... New itemStackSelection: " + itemStackSelection.getAllMaterialsListed());
            // Let's hope it saves somewhere, else conversion will happen again...
        }

        deductIfBlockIsPlaced =
                configuration.getBoolean(initialPath + ".specifics.deductIfBlockPlaced", true);
    }
}
