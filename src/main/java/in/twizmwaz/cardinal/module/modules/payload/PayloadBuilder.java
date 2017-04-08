package in.twizmwaz.cardinal.module.modules.payload;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.ModuleBuilder;
import in.twizmwaz.cardinal.module.ModuleCollection;
import in.twizmwaz.cardinal.module.modules.regions.RegionModule;
import in.twizmwaz.cardinal.module.modules.regions.RegionModuleBuilder;
import in.twizmwaz.cardinal.module.modules.regions.parsers.PointParser;
import in.twizmwaz.cardinal.module.modules.regions.type.PointRegion;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.Parser;
import org.jdom2.Element;

public class PayloadBuilder implements ModuleBuilder {

    public ModuleCollection<Payload> load(Match match) {
        ModuleCollection<Payload> results = new ModuleCollection<>();
        for (Element payloads : match.getDocument().getRootElement().getChildren("payloads")) {
            for (Element payload : payloads.getChildren("payload")) {
                results.add(parsePayload(payload, payloads));
            }
            for (Element payloads2 : payloads.getChildren("payloads")) {
                for (Element payload : payloads2.getChildren("payload")) {
                    results.add(parsePayload(payload, payloads2, payloads));
                }
            }
        }
        return results;
    }

    private Payload parsePayload(Element... elements) {
        return new Payload(
                RegionModuleBuilder.getAttributeOrChild("payload", elements),
                new PointRegion(new PointParser(elements[0])),
                Numbers.parseInt(Parser.getOrderedAttribute("rate", elements), 100),
                Numbers.parseDouble(Parser.getOrderedAttribute("distance", elements), 0.1)
                );
    }


}
