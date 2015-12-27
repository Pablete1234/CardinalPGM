package in.twizmwaz.cardinal.module.modules.fancyTab;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.BuilderData;
import in.twizmwaz.cardinal.module.ModuleBuilder;
import in.twizmwaz.cardinal.module.ModuleCollection;
import in.twizmwaz.cardinal.module.ModuleLoadTime;

@BuilderData(load = ModuleLoadTime.LATEST)
public class FancyTabModuleBuilder implements ModuleBuilder {

    @Override
    public ModuleCollection<FancyTabModule> load(Match match) {
        return new ModuleCollection<>(new FancyTabModule());
    }
}
