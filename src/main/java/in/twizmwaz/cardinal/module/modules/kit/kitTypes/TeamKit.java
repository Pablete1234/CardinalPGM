package in.twizmwaz.cardinal.module.modules.kit.kitTypes;

import in.twizmwaz.cardinal.module.modules.kit.Kit;
import in.twizmwaz.cardinal.module.modules.team.TeamModule;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class TeamKit implements Kit {

    private final TeamModule team;

    public TeamKit(TeamModule team) {
        this.team = team;
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void apply(Player player, Boolean force) {
        if (team != null) {
            team.add(player, force, false);
        }
    }

}
