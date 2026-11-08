# GraphBench

![CI Status](https://github.com/16lv/graphbench/actions/workflows/ci.yml/badge.svg)

A structured **Java 21** library and empirical benchmark suite covering core data structures, graph algorithms, order statistics, and space-optimized dynamic programming, verified with **JUnit 5**.

## Overview

Rather than storing isolated single-file university exercises, `java-algorithms-lab` organizes reusable generic data structures and algorithms into a clean Maven package hierarchy with explicit time/space complexity trade-offs and a CLI benchmark harness.

## Modules & Complexity Analysis

| Component | Package | Time Complexity | Space Complexity | Design Notes |
| :--- | :--- | :--- | :--- | :--- |
| `LruCache<K, V>` | `structures` | $O(1)$ `get` / `put` | $O(C)$ capacity | Hash map paired with sentinel-bounded doubly-linked nodes for branch-light eviction. |
| `IndexedMinHeap<K>` | `structures` | $O(\log V)$ `insertOrDecreaseKey` / `pollMin` | $O(V)$ | Maintains an inverse key-to-heap-index map so Dijkstra avoids duplicate stale entries. |
| `DisjointSetUnion` | `structures` | $O(\alpha(N))$ `find` / `union` | $O(N)$ | Iterative path compression combined with union-by-rank. |
| `GraphAlgorithms` | `graph` | Dijkstra: $O((V + E)\log V)$<br>Kahn Toposort: $O(V + E)$<br>Kruskal MST: $O(E \log E)$ | $O(V)$ | Uses `IndexedMinHeap` for shortest-path relaxation and `DisjointSetUnion` for MST cycle checks. |
| `SortingAndSelection` | `sorting` | Bottom-Up MergeSort: $O(N \log N)$<br>QuickSelect: $O(N)$ average | MergeSort: $O(N)$<br>QuickSelect: $O(1)$ aux | Iterative bottom-up merge avoids recursion stack frames; Hoare partition for $k$-th order statistic. |
| `DynamicProgramming` | `dp` | Levenshtein: $O(N \cdot M)$<br>0/1 Knapsack: $O(N \cdot W)$ | Levenshtein: $O(\min(N, M))$<br>Knapsack: $O(W)$ | Uses 1D rolling arrays instead of full 2D matrices to reduce heap pressure. |

## Prerequisites

- **Java 21** (Eclipse Temurin or OpenJDK 21)
- **Maven 3.9+**

## Running Tests & Benchmarks

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
 java-algorithms-lab Â· Empirical Complexity Benchmark Suite (Java 21)
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

