package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache;
    private int callsMade = 0;
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
        this.cache = new HashMap<>();

    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // Check if breed is in cache
        if (cache.containsKey(breed)) {
            return cache.get(breed);
        }

        // If not in cache, make the call
        callsMade++;
        try {
            List<String> subBreeds = fetcher.getSubBreeds(breed);
            // Only cache successful results (no exception thrown)
            cache.put(breed, subBreeds);
            return subBreeds;
        } catch (BreedNotFoundException e) {
            // Don't cache exceptions, just rethrow
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}
