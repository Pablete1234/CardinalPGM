package in.twizmwaz.cardinal.repository;

import in.twizmwaz.cardinal.module.BuilderData;
import in.twizmwaz.cardinal.util.Contributor;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.List;

public class LoadedMap {

    private String name, version, objective;
    private List<Contributor> authors, contributors;
    private List<String> rules;
    private int maxPlayers;
    private final File folder;
    private int id = -1;

    private static int maxNum = 0;

    /**
     * @param name    The name of the map
     * @param authors The authors of the map
     * @param folder  The folder where the map can be found
     */
    public LoadedMap(String name, String version, String objective, List<Contributor> authors,
                         List<Contributor> contributors, List<String> rules, int maxPlayers, File folder) {
        this.folder = folder;
        update(name, version, objective, authors, contributors, rules, maxPlayers);
    }

    private void update(String name, String version, String objective, List<Contributor> authors,
                        List<Contributor> contributors, List<String> rules, int maxPlayers) {
        this.name = name;
        this.version = version;
        this.objective = objective;
        this.authors = authors;
        this.contributors = contributors;
        this.rules = rules;
        this.maxPlayers = maxPlayers;
    }

    public void update(LoadedMap map) {
        update(map.name, map.version, map.objective, map.authors, map.contributors, map.rules, map.maxPlayers);
    }

    public void load() {
        if (this.id == -1) {
            this.id = maxNum++;
        }
    }

    public int getId() {
        return id;
    }

    /**
     * @return Returns the name of the map
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the map version as a String
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return Returns the objective of the map
     */
    public String getObjective() {
        return objective;
    }

    /**
     * @return Returns the authors of the map with their contributions
     */
    public List<Contributor> getAuthors() {
        return authors;
    }

    /**
     * @return Returns the contributors of the map with their contributions
     */
    public List<Contributor> getContributors() {
        return contributors;
    }

    /**
     * @return Returns the custom map rules
     */
    public List<String> getRules() {
        return rules;
    }

    /**
     * @return Returns the maximum number of players for the map
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * @return Returns the folder where the map can be found
     */
    public File getFolder() {
        return folder;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof LoadedMap && folder.equals(((LoadedMap) other).folder);
    }

}
