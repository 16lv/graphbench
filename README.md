# Java Graph Algorithms (java-graph-algorithms)

![CI Status](https://github.com/16lv/java-graph-algorithms/actions/workflows/ci.yml/badge.svg)

**Java Graph Algorithms** ($(System.Collections.Hashtable.newRepo)) is a **Java 21** library and empirical benchmark suite covering generic data structures, graph algorithms, order statistics, and space-optimised dynamic programming, verified with **JUnit 5**.

## Why I Built It

Rather than leaving Data Structures and Algorithms coursework as disconnected single-file exercises, I built Java Graph Algorithms to organise generic implementations into a clean Maven package structure and verify how theoretical time/space complexity translates to actual JVM execution times after JIT warmup.

## Modules & Complexity Analysis

| Component | Package | Time Complexity | Space Complexity | Implementation Notes |
| :--- | :--- | :--- | :--- | :--- |
| `LruCache<K, V>` | `structures` | $O(1)$ `get` / `put` | $O(C)$ capacity | `HashMap` paired with sentinel-bounded doubly-linked nodes for branch-free eviction. |
| `IndexedMinHeap<K>` | `structures` | $O(\log V)$ `insertOrDecreaseKey` / `pollMin` | $O(V)$ | Maintains an inverse key-to-heap-index map so Dijkstra updates priorities in-place instead of pushing stale duplicates. |
| `DisjointSetUnion` | `structures` | $O(\alpha(N))$ `find` / `union` | $O(N)$ | Iterative path compression combined with union-by-rank. |
| `GraphAlgorithms` | `graph` | Dijkstra: $O((V + E)\log V)$<br>Kahn Toposort: $O(V + E)$<br>Kruskal MST: $O(E \log E)$ | $O(V)$ | Uses `IndexedMinHeap` for shortest-path relaxation and `DisjointSetUnion` for MST cycle detection. |
| `SortingAndSelection` | `sorting` | Bottom-Up MergeSort: $O(N \log N)$<br>QuickSelect: $O(N)$ average | MergeSort: $O(N)$<br>QuickSelect: $O(1)$ aux | Iterative bottom-up merge avoids recursion stack frames; Hoare partition finds the $k$-th order statistic in-place. |
| `DynamicProgramming` | `dp` | Levenshtein: $O(N \cdot M)$<br>0/1 Knapsack: $O(N \cdot W)$ | Levenshtein: $O(\min(N, M))$<br>Knapsack: $O(W)$ | Uses 1D rolling arrays instead of full 2D tables to reduce heap allocations. |

## Project Structure

```text
graphbench/
├── pom.xml
└── src/
    ├── main/java/agency/shss7/algolab/
    │   ├── benchmark/BenchmarkRunner.java
    │   ├── dp/DynamicProgramming.java
    │   ├── graph/GraphAlgorithms.java
    │   ├── sorting/SortingAndSelection.java
    │   └── structures/
    │       ├── DisjointSetUnion.java
    │       ├── IndexedMinHeap.java
    │       └── LruCache.java
    └── test/java/agency/shss7/algolab/
        └── AlgorithmLaboratoryTest.java
```

## Running Tests & Benchmarks

### Prerequisites

- **Java 21** (OpenJDK 21 / Eclipse Temurin)
- **Maven 3.9+**

### Run the JUnit 5 Test Suite

```bash
mvn test
```

### Package & Run the Empirical Benchmark Runner

```bash
mvn package
java -jar target/java-algorithms-lab-1.0.0.jar
```

Example output:

```text
====================================================================
 Java Graph Algorithms · Empirical Complexity Benchmark Suite (Java 21)
====================================================================
Algorithm / Operation        | Input Size   | Elapsed (ms)  
--------------------------------------------------------------------
Bottom-Up MergeSort O(NlgN)  | 10000        |      1.182 ms
Hoare QuickSelect Median O(N)| 10000        |      0.294 ms
Bottom-Up MergeSort O(NlgN)  | 50000        |      6.410 ms
Hoare QuickSelect Median O(N)| 50000        |      1.115 ms
Bottom-Up MergeSort O(NlgN)  | 200000       |     26.840 ms
Hoare QuickSelect Median O(N)| 200000       |      3.920 ms
LRU Cache Put+Get O(1)       | 200000       |     14.310 ms
====================================================================
```

## Known Limitations & Future Improvements

- `BenchmarkRunner` uses `System.nanoTime()` after manual warmup loops to keep the repository dependency-free; migrating the harness to JMH (Java Microbenchmark Harness) would provide stronger isolation against dead-code elimination and GC pauses.
