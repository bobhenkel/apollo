package io.logz.apollo.transformers;

/**
 * Created by roiravhon on 2/7/17.
 */
public class LabelsNormalizer {

    public static String normalize(String input) {

        input = input.replace(" ", "_");
        input = input.replaceAll("[^a-zA-Z0-9-_]", "");

        return input;
    }
}
