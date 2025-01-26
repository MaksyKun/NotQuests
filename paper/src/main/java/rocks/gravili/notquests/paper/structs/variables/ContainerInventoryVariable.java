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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.suggestion.Suggestion;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.arguments.variables.CustomStringParser;
import rocks.gravili.notquests.paper.commands.arguments.variables.NumberVariableValueParser;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ContainerInventoryVariable extends Variable<ItemStack[]> {
    public ContainerInventoryVariable(NotQuests main) {
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

        addRequiredBooleanFlag(main.getCommandManager().getPaperCommandManager().flagBuilder("skipItemIfInventoryFull")
                .withDescription(Description.of("Does not drop the item if inventory full if flag set")).build()
        );
    }

    @Override
    public final ItemStack[] getValueInternally(final QuestPlayer questPlayer, final Object... objects) {
        final String worldName = getRequiredStringValue("world");
        final World world = Bukkit.getWorld(worldName);
        final double x = getRequiredNumberValue("x", questPlayer);
        final double y = getRequiredNumberValue("y", questPlayer);
        final double z = getRequiredNumberValue("z", questPlayer);
        if (world == null) {
            main.getLogManager().warn("Error: cannot get value of chest inventory variable, because the world " + worldName + " does not exist.");
            return null;
        }

        final Location location = new Location(world, x, y, z);
        final Block block = location.getBlock();

        if (block.getState() instanceof final Container container) {
            return container.getInventory().getStorageContents();

        } else {
            main.getLogManager().warn("Error: cannot get value of chest inventory variable, because the location does not have a container block. Real type: " + block.getType().name());
            return new ItemStack[0];
        }

    }

    @Override
    public boolean setValueInternally(final ItemStack[] newValue, final QuestPlayer questPlayer, final Object... objects) {
        final String worldName = getRequiredStringValue("world");
        final World world = Bukkit.getWorld(worldName);
        final double x = getRequiredNumberValue("x", questPlayer);
        final double y = getRequiredNumberValue("y", questPlayer);
        final double z = getRequiredNumberValue("z", questPlayer);
        if (world == null) {
            main.getLogManager().warn("Error: cannot set value of chest inventory variable, because the world " + worldName + " does not exist.");
            return false;
        }

        final Location location = new Location(world, x, y, z);
        final Block block = location.getBlock();

        if (block.getState() instanceof final Container container) {
            if (getRequiredBooleanValue("add", questPlayer)) {

                final HashMap<Integer, ItemStack> left = container.getInventory().addItem(newValue);
                if (!getRequiredBooleanValue("skipItemIfInventoryFull", questPlayer)) {
                    for (ItemStack leftItemStack : left.values()) {
                        world.dropItem(location, leftItemStack);
                    }
                }
            } else if (getRequiredBooleanValue("remove", questPlayer)) {
                container.getInventory().removeItemAnySlot(newValue);
            } else {
                container.getInventory().setContents(newValue);
            }
        } else {
            main.getLogManager().warn("Error: cannot set value of chest inventory variable, because the location does not have a container block. Real type: " + block.getType().name());
            return false;
        }

        return true;
    }


    @Override
    public final List<Suggestion> getPossibleValues(final QuestPlayer questPlayer, final Object... objects) {
        return null;
    }

    @Override
    public final String getPlural() {
        return "Container Inventory";
    }

    @Override
    public final String getSingular() {
        return "Container Inventory";
    }
}
