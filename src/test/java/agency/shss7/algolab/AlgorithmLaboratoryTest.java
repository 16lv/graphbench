package agency.shss7.algolab;

import agency.shss7.algolab.dp.DynamicProgramming;
import agency.shss7.algolab.graph.GraphAlgorithms;
import agency.shss7.algolab.graph.GraphAlgorithms.Edge;
import agency.shss7.algolab.sorting.SortingAndSelection;
import agency.shss7.algolab.structures.DisjointSetUnion;
import agency.shss7.algolab.structures.IndexedMinHeap;
import agency.shss7.algolab.structures.LruCache;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmLaboratoryTest {

    @Test
    void lruCacheEvictsLeastRecentlyUsedAndPromotesAccessedKeys() {
        LruCache<String, Integer> cache = new LruCache<>(3);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        assertEquals(Optional.of(1), cache.get("A"));

        cache.put("D", 4);
        assertEquals(Optional.empty(), cache.get("B"));
        assertEquals(Optional.of(1), cache.get("A"));
        assertEquals(Optional.of(3), cache.get("C"));
        assertEquals(Optional.of(4), cache.get("D"));
        assertEquals(3, cache.size());
    }

    @Test
    void indexedMinHeapSupportsDecreaseKeyAndPollsInAscendingPriorityOrder() {
        IndexedMinHeap<String> heap = new IndexedMinHeap<>();
        heap.insertOrDecreaseKey("node-a", 15.0);
        heap.insertOrDecreaseKey("node-b", 8.0);
        heap.insertOrDecreaseKey("node-c", 22.0);

        heap.insertOrDecreaseKey("node-c", 3.5);

        assertEquals("node-c", heap.pollMin().key());
        assertEquals("node-b", heap.pollMin().key());
        assertEquals("node-a", heap.pollMin().key());
        assertTrue(heap.isEmpty());
    }

    @Test
    void bfsAndDfsTraverseConnectedGraphCorrectly() {
        Map<String, List<String>> graph = Map.of(
            "A", List.of("B", "C"),
            "B", List.of("D"),
            "C", List.of("E"),
            "D", List.of(),
            "E", List.of()
        );

        List<String> bfs = GraphAlgorithms.breadthFirstSearch(graph, "A");
        assertEquals(List.of("A", "B", "C", "D", "E"), bfs);

        List<String> dfs = GraphAlgorithms.depthFirstSearch(graph, "A");
        assertEquals(List.of("A", "B", "D", "C", "E"), dfs);
    }

    @Test
    void dijkstraFindsShortestWeightedPathAndHandlesDecreaseKey() {
        Map<String, List<Edge<String>>> graph = new HashMap<>();
        graph.put("S", List.of(new Edge<>("S", "A", 10.0), new Edge<>("S", "B", 3.0)));
        graph.put("B", List.of(new Edge<>("B", "A", 4.0), new Edge<>("B", "C", 8.0)));
        graph.put("A", List.of(new Edge<>("A", "C", 2.0)));
        graph.put("C", List.of());

        var result = GraphAlgorithms.dijkstra(graph, "S", "C");
        assertEquals(9.0, result.totalDistance(), 1e-6);
        assertEquals(List.of("S", "B", "A", "C"), result.path());
    }

    @Test
    void topologicalSortOrdersDagAndDetectsCycles() {
        Map<String, List<String>> dag = Map.of(
            "compile", List.of("test", "lint"),
            "test", List.of("package"),
            "lint", List.of("package"),
            "package", List.of()
        );
        List<String> order = GraphAlgorithms.topologicalSort(dag);
        assertEquals("compile", order.get(0));
        assertEquals("package", order.get(3));

        Map<String, List<String>> cyclic = Map.of(
            "A", List.of("B"),
            "B", List.of("C"),
            "C", List.of("A")
        );
        assertThrows(IllegalStateException.class, () -> GraphAlgorithms.topologicalSort(cyclic));
    }

    @Test
    void kruskalAndDisjointSetProduceMinimumSpanningTree() {
        List<Edge<Integer>> edges = List.of(
            new Edge<>(0, 1, 4.0),
            new Edge<>(0, 2, 1.0),
            new Edge<>(2, 1, 2.0),
            new Edge<>(1, 3, 5.0),
            new Edge<>(2, 3, 8.0)
        );
        List<Edge<Integer>> mst = GraphAlgorithms.kruskalMinimumSpanningTree(4, edges);
        assertEquals(3, mst.size());
        double totalWeight = mst.stream().mapToDouble(Edge::weight).sum();
        assertEquals(8.0, totalWeight, 1e-6);

        DisjointSetUnion dsu = new DisjointSetUnion(4);
        assertTrue(dsu.union(0, 1));
        assertFalse(dsu.union(0, 1));
        assertTrue(dsu.connected(0, 1));
        assertEquals(3, dsu.componentCount());
    }

    @Test
    void mergeSortQuickSortBinarySearchAndQuickSelectMatchSortedOrder() {
        int[] raw = {42, 7, 19, -3, 88, 14, 0, 19, 5};
        int[] expected = raw.clone();
        Arrays.sort(expected);

        int[] mergeSorted = raw.clone();
        SortingAndSelection.mergeSort(mergeSorted);
        assertArrayEquals(expected, mergeSorted);

        int[] quickSorted = raw.clone();
        SortingAndSelection.quickSort(quickSorted);
        assertArrayEquals(expected, quickSorted);

        assertEquals(4, SortingAndSelection.binarySearch(expected, 14));
        assertEquals(-1, SortingAndSelection.binarySearch(expected, 999));
        assertEquals(5, SortingAndSelection.lowerBound(expected, 19));

        for (int k = 0; k < raw.length; k++) {
            assertEquals(expected[k], SortingAndSelection.quickSelect(raw, k));
        }
    }

    @Test
    void levenshteinAndKnapsackProduceExactOptimalValues() {
        assertEquals(3, DynamicProgramming.levenshteinDistance("kitten", "sitting"));
        assertEquals(0, DynamicProgramming.levenshteinDistance("cardiff", "cardiff"));

        int[] weights = {2, 3, 4, 5};
        int[] values = {3, 4, 5, 8};
        assertEquals(11, DynamicProgramming.knapsack01(weights, values, 7));
        assertThrows(IllegalArgumentException.class, () -> DynamicProgramming.knapsack01(new int[]{-1}, new int[]{5}, 3));
    }

    @Test
    void lruCacheRemovalAndGraphBoundaryValidationBehaveCorrectly() {
        LruCache<String, Integer> cache = new LruCache<>(2);
        cache.put("x", 10);
        assertTrue(cache.containsKey("x"));
        assertTrue(cache.remove("x"));
        assertFalse(cache.containsKey("x"));
        assertEquals(0, cache.size());

        Map<String, List<Edge<String>>> disconnected = Map.of("S", List.of(), "T", List.of());
        var unreachable = GraphAlgorithms.dijkstra(disconnected, "S", "T");
        assertEquals(Double.POSITIVE_INFINITY, unreachable.totalDistance());
        assertTrue(unreachable.path().isEmpty());

        assertThrows(IllegalArgumentException.class, () -> GraphAlgorithms.dijkstra(disconnected, "MISSING", "T"));
        assertThrows(
            IllegalArgumentException.class,
            () -> GraphAlgorithms.kruskalMinimumSpanningTree(3, List.of(new Edge<>(0, 5, 1.0)))
        );
    }
}
