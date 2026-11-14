package agency.shss7.algolab.graph;

import agency.shss7.algolab.structures.DisjointSetUnion;
import agency.shss7.algolab.structures.IndexedMinHeap;

import java.util.*;

// Graph search and shortest-path algorithms (BFS, DFS, Dijkstra, Topological Sort, Kruskal MST).
public final class GraphAlgorithms {

    public record Edge<V>(V from, V to, double weight) {
        public Edge {
            if (weight < 0) {
                throw new IllegalArgumentException("Edge weight must be non-negative: " + weight);
            }
        }
    }

    public record PathResult<V>(List<V> path, double totalDistance, int visitedNodes) {}

    private GraphAlgorithms() {
    }

    /**
     * Breadth-First Search (BFS) traversal from a starting vertex in O(V + E) time.
     */
    public static <V> List<V> breadthFirstSearch(Map<V, List<V>> adjacency, V start) {
        if (adjacency == null || start == null) {
            throw new IllegalArgumentException("Adjacency map and start vertex must not be null");
        }
        List<V> order = new ArrayList<>();
        Set<V> visited = new LinkedHashSet<>();
        Deque<V> queue = new ArrayDeque<>();

        visited.add(start);
        queue.addLast(start);

        while (!queue.isEmpty()) {
            V current = queue.removeFirst();
            order.add(current);
            for (V neighbor : adjacency.getOrDefault(current, List.of())) {
                if (visited.add(neighbor)) {
                    queue.addLast(neighbor);
                }
            }
        }
        return order;
    }

    /**
     * Depth-First Search (DFS) traversal from a starting vertex in O(V + E) time.
     */
    public static <V> List<V> depthFirstSearch(Map<V, List<V>> adjacency, V start) {
        if (adjacency == null || start == null) {
            throw new IllegalArgumentException("Adjacency map and start vertex must not be null");
        }
        List<V> order = new ArrayList<>();
        Set<V> visited = new HashSet<>();
        Deque<V> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            V current = stack.pop();
            if (!visited.add(current)) {
                continue;
            }
            order.add(current);
            List<V> neighbors = adjacency.getOrDefault(current, List.of());
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                V neighbor = neighbors.get(i);
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                }
            }
        }
        return order;
    }

    public static <V> PathResult<V> dijkstra(
        Map<V, List<Edge<V>>> adjacency,
        V source,
        V target
    ) {
        if (adjacency == null || source == null || target == null) {
            throw new IllegalArgumentException("Adjacency map, source, and target must not be null");
        }
        if (!adjacency.containsKey(source)) {
            throw new IllegalArgumentException("Source vertex is missing from adjacency map: " + source);
        }
        Map<V, Double> dist = new HashMap<>();
        Map<V, V> prev = new HashMap<>();
        IndexedMinHeap<V> pq = new IndexedMinHeap<>();

        dist.put(source, 0.0);
        pq.insertOrDecreaseKey(source, 0.0);
        int visited = 0;

        while (!pq.isEmpty()) {
            var current = pq.pollMin();
            V u = current.key();
            visited++;

            if (u.equals(target)) {
                return new PathResult<>(reconstructPath(prev, source, target), current.priority(), visited);
            }

            for (Edge<V> edge : adjacency.getOrDefault(u, List.of())) {
                double candidate = dist.get(u) + edge.weight();
                if (candidate < dist.getOrDefault(edge.to(), Double.POSITIVE_INFINITY)) {
                    dist.put(edge.to(), candidate);
                    prev.put(edge.to(), u);
                    pq.insertOrDecreaseKey(edge.to(), candidate);
                }
            }
        }

        return new PathResult<>(List.of(), Double.POSITIVE_INFINITY, visited);
    }

    public static <V> List<V> topologicalSort(Map<V, List<V>> dag) {
        if (dag == null) {
            throw new IllegalArgumentException("Adjacency map must not be null");
        }
        Map<V, Integer> inDegree = new LinkedHashMap<>();
        for (var entry : dag.entrySet()) {
            inDegree.putIfAbsent(entry.getKey(), 0);
            for (V neighbor : entry.getValue()) {
                inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);
            }
        }

        Deque<V> queue = new ArrayDeque<>();
        for (var entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.addLast(entry.getKey());
            }
        }

        List<V> ordering = new ArrayList<>(inDegree.size());
        while (!queue.isEmpty()) {
            V vertex = queue.removeFirst();
            ordering.add(vertex);
            for (V neighbor : dag.getOrDefault(vertex, List.of())) {
                int updated = inDegree.compute(neighbor, (k, v) -> v - 1);
                if (updated == 0) {
                    queue.addLast(neighbor);
                }
            }
        }

        if (ordering.size() != inDegree.size()) {
            throw new IllegalStateException("Graph contains at least one cycle; topological ordering impossible");
        }
        return ordering;
    }

    public static List<Edge<Integer>> kruskalMinimumSpanningTree(int vertexCount, List<Edge<Integer>> edges) {
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("Vertex count must be positive: " + vertexCount);
        }
        if (edges == null) {
            throw new IllegalArgumentException("Edge list must not be null");
        }
        for (Edge<Integer> edge : edges) {
            if (edge == null || edge.from() == null || edge.to() == null) {
                throw new IllegalArgumentException("Edge and its endpoints must not be null");
            }
            if (edge.from() < 0 || edge.from() >= vertexCount || edge.to() < 0 || edge.to() >= vertexCount) {
                throw new IllegalArgumentException(
                    "Edge endpoint out of bounds [0, " + vertexCount + "): " + edge
                );
            }
        }

        List<Edge<Integer>> sorted = new ArrayList<>(edges);
        sorted.sort(Comparator.comparingDouble(Edge::weight));

        DisjointSetUnion dsu = new DisjointSetUnion(vertexCount);
        List<Edge<Integer>> mst = new ArrayList<>();

        for (Edge<Integer> edge : sorted) {
            if (dsu.union(edge.from(), edge.to())) {
                mst.add(edge);
                if (mst.size() == vertexCount - 1) {
                    break;
                }
            }
        }
        return mst;
    }

    private static <V> List<V> reconstructPath(Map<V, V> prev, V source, V target) {
        LinkedList<V> path = new LinkedList<>();
        V curr = target;
        while (curr != null) {
            path.addFirst(curr);
            if (curr.equals(source)) {
                break;
            }
            curr = prev.get(curr);
        }
        return path;
    }
}
