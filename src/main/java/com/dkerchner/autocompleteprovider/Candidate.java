package com.dkerchner.autocompleteprovider;

import java.util.Objects;

/**
 * Candidate - Represents a coupling of a word with a confidence score. The
 * confidence score is determined by the number of times the word appears in the
 * passages provided by the user.
 * 
 * If two words appear equally, they will have the same confidence. If one
 * appears more often, it will have a higher confidence.
 */
public class Candidate implements Comparable<Candidate> {

    private final String word;
    private final Integer confidence;

    public Candidate(String word, Integer confidence) {
        this.word = word;
        this.confidence = confidence;
    }

    /**
     * @return the word
     */
    public String getWord() {
        return word;
    }

    /**
     * @return the confidence
     */
    public Integer getConfidence() {
        return confidence;
    }

    @Override
    public int compareTo(Candidate candidate) {
        // Sort descending by confidence
        return (candidate.confidence - this.confidence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        // Unique based upon the word
        Candidate c = (Candidate) o;
        return this.word.equals(c.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }

    @Override
    public String toString() {
        return "\"" + word + "\"" + " (" + confidence + ")";
    }
}