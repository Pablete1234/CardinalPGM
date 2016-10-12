package in.twizmwaz.cardinal.util;

import java.util.Collection;
import java.util.stream.Stream;

public class CollectionUtils {

    /**
     * Updates an old collection, to be the new collection, but keeping the old objects if they equal to true.
     * @param oldCol Old collection,
     * @param newCol The result you want to get.
     * @return Stream newCol items, but replacing them with the ones that are equal to the old ones.
     */
    public static <T> Stream<T> update(Collection<T> oldCol, Collection<T> newCol) {
        return Stream.concat(
                oldCol.stream().filter(newCol::contains),                //Remove no longer present objects from old col
                newCol.stream().filter(newIt -> !oldCol.contains(newIt)));// Remove already present objects from new col
    }

}
