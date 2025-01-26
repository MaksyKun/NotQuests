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

import org.incendo.cloud.description.Description;
import org.incendo.cloud.suggestion.Suggestion;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.List;

public class QuestPointsVariable extends Variable<Long> {
  public QuestPointsVariable(NotQuests main) {
    super(main);
    setCanSetValue(true);
    addRequiredBooleanFlag(
        main.getCommandManager()
            .getPaperCommandManager()
            .flagBuilder("notifyPlayer")
            .withDescription(
                    Description.of(
                    "Notifies the player for when their QuestPoints are changed or set"))
            .build() // TODO: setOnlyRequiredValues once implemented
        );
  }

  @Override
  public Long getValueInternally(QuestPlayer questPlayer, Object... objects) {
    if (questPlayer == null) {
      return 0L;
    }
    return questPlayer.getQuestPoints();
  }

  @Override
  public boolean setValueInternally(Long newValue, QuestPlayer questPlayer, Object... objects) {
    if (questPlayer == null) {
      return false;
    }
    questPlayer.setQuestPoints(newValue, getRequiredBooleanValue("notifyPlayer", questPlayer));
    return true;
  }

  @Override
  public List<Suggestion> getPossibleValues(QuestPlayer questPlayer, Object... objects) {
    return null;
  }

  @Override
  public String getPlural() {
    return "Quest Points";
  }

  @Override
  public String getSingular() {
    return "Quest Point";
  }
}
