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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.incendo.cloud.suggestion.Suggestion;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.arguments.variables.CustomStringParser;
import rocks.gravili.notquests.paper.commands.arguments.variables.NumberVariableValueParser;
import rocks.gravili.notquests.paper.managers.items.NQItem;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class BlockVariable extends Variable<String> {
    public BlockVariable(NotQuests main) {
        super(main);
        setCanSetValue(true);

        addRequiredString(CustomStringParser.customStringParser("world", null, (context, input) -> {
            main.getUtilManager().sendFancyCommandCompletion(context.sender(), input.input().split(" "), "[World Name]", "[...]");
            ArrayList<Suggestion> suggestions = new ArrayList<>();
            for (World world : Bukkit.getWorlds()) {
                suggestions.add(Suggestion.suggestion(world.getName()));
            }
            return CompletableFuture.completedFuture(suggestions);
        }));

        addRequiredNumber(NumberVariableValueParser.numberVariableValueParser("x", null, null));
        addRequiredNumber(NumberVariableValueParser.numberVariableValueParser("y", null, null));
        addRequiredNumber(NumberVariableValueParser.numberVariableValueParser("z", null, null));
    }


    @Override
    public String getValueInternally(QuestPlayer questPlayer, Object... objects) {
        String worldName = getRequiredStringValue("world");
        World world = Bukkit.getWorld(worldName);
        double x = getRequiredNumberValue("x", questPlayer);
        double y = getRequiredNumberValue("y", questPlayer);
        double z = getRequiredNumberValue("z", questPlayer);
        if (world == null) {
            main.getLogManager()
                    .warn(
                            "Error: cannot get value of chest inventory variable, because the world "
                                    + worldName
                                    + " does not exist.");
            return null;
        }

        Location location = new Location(world, x, y, z);
        Block block = location.getBlock();

        return block.getType().name().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean setValueInternally(String newValue, QuestPlayer questPlayer, Object... objects) {
        String worldName = getRequiredStringValue("world");
        World world = Bukkit.getWorld(worldName);
        double x = getRequiredNumberValue("x", questPlayer);
        double y = getRequiredNumberValue("y", questPlayer);
        double z = getRequiredNumberValue("z", questPlayer);
        if (world == null) {
            main.getLogManager()
                    .warn(
                            "Error: cannot set value of chest inventory variable, because the world "
                                    + worldName
                                    + " does not exist.");
            return false;
        }

        Location location = new Location(world, x, y, z);
        Block block = location.getBlock();

        final String materialToBreak;
        if (newValue.equalsIgnoreCase("hand")) { // "hand"
            if (questPlayer != null) {
                materialToBreak =
                        questPlayer.getPlayer().getInventory().getItemInMainHand().getType().name();
            } else {
                return false;
            }
        } else {
            if (!newValue.equalsIgnoreCase("any")) {
                materialToBreak = main.getItemsManager().getMaterial(newValue).name();
            } else {
                int rnd = new Random().nextInt(Material.values().length);
                materialToBreak = Material.values()[rnd].name().toLowerCase(Locale.ROOT);
            }
        }
        block.setType(Material.valueOf(materialToBreak.toUpperCase(Locale.ROOT)));

        return true;
    }

    @Override
    public List<Suggestion> getPossibleValues(QuestPlayer questPlayer, Object... objects) {
        final List<Suggestion> completions = new ArrayList<>();
        for (Material value : Material.values()) {
            completions.add(Suggestion.suggestion(value.name().toLowerCase()));
        }

        for (NQItem nqItem : NotQuests.getInstance().getItemsManager().getItems()) {
            completions.add(Suggestion.suggestion(nqItem.getItemName()));
        }

        completions.add(Suggestion.suggestion("hand"));
        completions.add(Suggestion.suggestion("any"));

        return completions;
    }

    @Override
    public String getPlural() {
        return "Blocks";
    }

    @Override
    public String getSingular() {
        return "Block";
    }
}

