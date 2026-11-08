package agency.shss7.algolab.benchmark;

import agency.shss7.algolab.sorting.SortingAndSelection;
import agency.shss7.algolab.structures.LruCache;

import java.util.Arrays;
import java.util.Random;

/**
 * CLI benchmark runner measuring empirical wall-clock timings across input scales
 * after JVM JIT warmup iterations.
 */
public final class BenchmarkRunner {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println(" GraphBench · Empirical Complexity Benchmark Suite (Java 21)");
        System.out.println("====================================================================");

        Random rng = new Random(42L);
        // Warmup JIT
        for (int i = 0; i < 5; i++) {
            int[] warm = rng.ints(10_000, 0, 1_000_000).toArray();
            SortingAndSelection.mergeSort(warm);
        }

        System.out.printf("%-28s | %-12s | %-14s%n", "Algorithm / Operation", "Input Size", "Elapsed (ms)");
        System.out.println("--------------------------------------------------------------------");

        for (int size : new int[]{10_000, 50_000, 200_000}) {
            int[] data = rng.ints(size, 0, 10_000_000).toArray();
            int[] copy = Arrays.copyOf(data, data.length);

            long t0 = System.nanoTime();
            SortingAndSelection.mergeSort(copy);
            double mergeMs = (System.nanoTime() - t0) / 1_000_000.0;
            System.out.printf("%-28s | %-12d | %10.3f ms%n", "Bottom-Up MergeSort O(NlgN)", size, mergeMs);

            long t1 = System.nanoTime();
            SortingAndSelection.quickSelect(data, size / 2);
            double selectMs = (System.nanoTime() - t1) / 1_000_000.0;
            System.out.printf("%-28s | %-12d | %10.3f ms%n", "Hoare QuickSelect Median O(N)", size, selectMs);
        }

        LruCache<Integer, Integer> cache = new LruCache<>(4096);
        long t2 = System.nanoTime();
        for (int i = 0; i < 200_000; i++) {
            cache.put(i % 8192, i);
            cache.get(i % 4096);
        }
        double lruMs = (System.nanoTime() - t2) / 1_000_000.0;
        System.out.printf("%-28s | %-12d | %10.3f ms%n", "LRU Cache Put+Get O(1)", 200_000, lruMs);
        System.out.println("====================================================================");
    }
}
