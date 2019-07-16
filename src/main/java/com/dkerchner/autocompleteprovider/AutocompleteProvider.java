package com.dkerchner.autocompleteprovider;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

/**
 * AutocompleteProvider - Encapsulates an algorithm that learns the words typed
 * by a user over time and then determines a ranked list of autocomplete
 * candidates given a word fragment.
 */
public class AutocompleteProvider {
    // used to store all partial words seen to look up suggestions quickly
    private HashMap<String, HashSet<Candidate>> autocompleteMap;
    private HashMap<String, Integer> wordCount; // used to keep track of the words seen

    public AutocompleteProvider() {
        this.autocompleteMap = new HashMap<String, HashSet<Candidate>>();
        this.wordCount = new HashMap<String, Integer>();
    }

    // Instantiates an AutocompleteProvider and continually processes user input
    public static void main(String[] args) throws IOException {
        AutocompleteProvider ap = new AutocompleteProvider();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        // Prompt the user and then read the input until they exit
        System.out.println("Enter a passage or partial word (type \"exit!\" to quit): ");
        while ((line = br.readLine()) != null) {
            ap.processLineOfInput(line);
        }
    }

    // Gets the list of suggestions from the map of partial words
    List<Candidate> getWords(String fragment) {
        if (fragment.trim().length() == 0) {
            System.out.println("Error: empty fragment provided.");
            return null;
        }

        HashSet<Candidate> autocompleteValues = this.autocompleteMap.get(fragment.toLowerCase());

        if (autocompleteValues == null) {
            return null;
        } else {
            // Return the sorted descending set of suggestions
            List<Candidate> list = new ArrayList<Candidate>(autocompleteValues);
            Collections.sort(list);
            return list;
        }
    }

    // Updates the word count hash map for all words encountered.
    // Writes word fragments to a hashmap with the matching Candidates.
    void train(String passage) {
        if (passage.length() == 0) {
            System.out.println("Error: empty passage provided.");
            return;
        }

        String[] words = passage.toLowerCase().split("\\s+");
        if (words.length <= 1) {
            System.out.println("Error: more than one word is needed for training.");
            return;
        }

        for (String word : words) {
            // if the word already exists in the hash increment the confidence,
            // otherwise add it
            if (this.wordCount.get(word) != null) {
                this.wordCount.put(word, new Integer(this.wordCount.get(word) + 1));
            } else {
                this.wordCount.put(word, 1);
            }

            // Add a record for each partial word into the hash for quick lookup later
            // for specific suggestions
            String partialWord = "";
            Candidate c = new Candidate(word, this.wordCount.get(word));
            for (int i = 0; i <= word.length(); i++) {
                partialWord = word.toLowerCase().substring(0, i);
                // If the candidate exists in the set for the partial word, remove it
                // and then add the updated version. Otherwise, just add it.
                if (this.autocompleteMap.get(partialWord) != null) {
                    this.autocompleteMap.get(partialWord).remove(c);
                    this.autocompleteMap.get(partialWord).add(c);
                } else {
                    HashSet<Candidate> setC = new HashSet<Candidate>();
                    setC.add(c);
                    this.autocompleteMap.put(partialWord, setC);
                }
            }
        }
    }

    // Processes the input from the user and then
    void processLineOfInput(String line) {
        if (line.trim().length() == 0) {
            // Check if any input was provided
            System.out.println("Error: empty input provided.");
        } else if (line.trim().equals("exit!")) {
            // Checking for the safe word
            System.out.println("Goodbye!");
            System.exit(0);
        } else {
            String[] words = line.trim().split("\\s+");

            if (words.length == 1) {
                // This indicates that they are looking for a suggestion. If there are no words
                // yet then display an error.
                if (this.GetWordCount().isEmpty()) {
                    System.out.println("Error: You must train the application by entering a passage first.");
                } else {
                    // Get the list of suggestions and display them. If none are available,
                    // return "No suggestions found."
                    List<Candidate> ac = this.getWords(words[0]);
                    if (ac == null) {
                        System.out.println("No suggestions found.");
                    } else {
                        System.out.print("Suggestion(s): ");
                        // Loop through and display the suggestions
                        for (int i = 0; i < ac.size(); i++) {
                            System.out.print(ac.get(i));
                            if (i != ac.size() - 1) {
                                System.out.print(", ");
                            }
                        }
                        System.out.print("\n");
                    }
                }
            } else {
                // Train the suggestion provider with the input
                this.train(line);
            }
            // Give the prompt again
            System.out.println("Enter a passage or partial word (type \"exit!\" to quit): ");
        }
    }

    // Returns the word count map
    public HashMap<String, Integer> GetWordCount() {
        return this.wordCount;
    }

    // Returns the autocomplete map
    public HashMap<String, HashSet<Candidate>> GetAutocompleteMap() {
        return this.autocompleteMap;
    }
}