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

package rocks.gravili.notquests.paper.structs.variables.reflectionVariables;

import org.incendo.cloud.suggestion.Suggestion;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.commands.arguments.variables.StringVariableValueParser;
import rocks.gravili.notquests.paper.structs.QuestPlayer;
import rocks.gravili.notquests.paper.structs.variables.Variable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReflectionStaticFloatVariable extends Variable<Float> {
  public ReflectionStaticFloatVariable(NotQuests main) {
    super(main);
    setCanSetValue(true);

    addRequiredString(StringVariableValueParser.of("Class Path", null, (context, lastString) -> {
        main.getUtilManager().sendFancyCommandCompletion(context.sender(), lastString.input().split(" "), "[Item Slot ID / Equipment Slot Name]", "[...]");
        ArrayList<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(Suggestion.suggestion("<Enter class path>"));
        return CompletableFuture.completedFuture(suggestions);
    }));

    addRequiredString(StringVariableValueParser.of("Field", null, (context, lastString) -> {
                main.getUtilManager().sendFancyCommandCompletion(context.sender(), lastString.input().split(" "), "[Item Slot ID / Equipment Slot Name]", "[...]");
                ArrayList<Suggestion> suggestions = new ArrayList<>();
                suggestions.add(Suggestion.suggestion("<Enter field name>"));
                return CompletableFuture.completedFuture(suggestions);
            }));
  }

  @Override
  public Float getValueInternally(QuestPlayer questPlayer, Object... objects) {
    final String classPath = getRequiredStringValue("Class Path");
    final String fieldName = getRequiredStringValue("Field Name");

    try{
      Class<?> foundClass = Class.forName(classPath);

      Field field = foundClass.getDeclaredField(fieldName);
      field.setAccessible(true);

      return field.getFloat(null);
    }catch (Exception e){
      main.getLogManager().warn("Reflection in ReflectionStaticFloatVariable failed. Error: " + e.getMessage());
    }


    return 0f;
  }

  @Override
  public boolean setValueInternally(Float newValue, QuestPlayer questPlayer, Object... objects) {
    final String classPath = getRequiredStringValue("Class Path");
    final String fieldName = getRequiredStringValue("Field Name");

    try{
      Class<?> foundClass = Class.forName(classPath);

      Field field = foundClass.getDeclaredField(fieldName);
      field.setAccessible(true);

      field.setFloat(null, newValue);
      return true;
    }catch (Exception e){
      main.getLogManager().warn("Reflection in ReflectionStaticFloatVariable failed. Error: " + e.getMessage());
    }
    return false;
  }

  @Override
  public List<Suggestion> getPossibleValues(QuestPlayer questPlayer, Object... objects) {
    return null;
  }

  @Override
  public String getPlural() {
    return "Float from static reflection";
  }

  @Override
  public String getSingular() {
    return "Float from static reflection";
  }
}
