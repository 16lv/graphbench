package agency.shss7.algolab.dp;

/**
 * Dynamic programming implementations with space-optimized rolling arrays.
 */
public final class DynamicProgramming {

    private DynamicProgramming() {
    }

    /**
     * Wagner-Fischer Levenshtein edit distance using O(min(N, M)) auxiliary space.
     */
    public static int levenshteinDistance(String source, String target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target strings must not be null");
        }
        if (source.equals(target)) {
            return 0;
        }
        if (source.length() < target.length()) {
            return levenshteinDistance(target, source);
        }
        int m = target.length();
        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];

        for (int j = 0; j <= m; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= source.length(); i++) {
            curr[0] = i;
            char sc = source.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                int cost = (sc == target.charAt(j - 1)) ? 0 : 1;
                curr[j] = Math.min(
                    Math.min(curr[j - 1] + 1, prev[j] + 1),
                    prev[j - 1] + cost
                );
            }
            int[] swap = prev;
            prev = curr;
            curr = swap;
        }
        return prev[m];
    }

    /**
     * 0/1 Knapsack solver using a 1D rolling DP table in O(N * W) time and O(W) space.
     */
    public static int knapsack01(int[] weights, int[] values, int capacity) {
        if (weights == null || values == null || weights.length != values.length || capacity < 0) {
            throw new IllegalArgumentException("Invalid weights/values or negative capacity");
        }
        int[] dp = new int[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            int w = weights[i];
            int v = values[i];
            if (w < 0) {
                throw new IllegalArgumentException("Item weight cannot be negative: " + w);
            }
            for (int cap = capacity; cap >= w; cap--) {
                dp[cap] = Math.max(dp[cap], dp[cap - w] + v);
            }
        }
        return dp[capacity];
    }
}
