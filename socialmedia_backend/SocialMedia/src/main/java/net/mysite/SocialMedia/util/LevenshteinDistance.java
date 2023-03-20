package net.mysite.SocialMedia.util;

public class LevenshteinDistance {
    public static int calculate(String str1, String str2) {
        if (str1 == null || str2 == null) {
            throw new IllegalArgumentException("The strings must not be null");
        }

        int len1 = str1.length();
        int len2 = str2.length();
        int[][] distances = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            distances[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            distances[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1;

                distances[i][j] = Math.min(Math.min(
                                distances[i - 1][j] + 1,
                                distances[i][j - 1] + 1),
                        distances[i - 1][j - 1] + cost);
            }
        }

        return distances[len1][len2];
    }
}
