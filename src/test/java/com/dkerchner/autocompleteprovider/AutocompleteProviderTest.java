package com.dkerchner.autocompleteprovider;

import java.util.List;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import com.dkerchner.autocompleteprovider.AutocompleteProvider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class AutocompleteProviderTest {

    private AutocompleteProvider ac = new AutocompleteProvider();

    private final String testPassage1 = "Asymmetrik is the best";
    private final String testPassage2 = "PiZZa is also the Best";
    private final String testPassage3 = "Bobby is just the worst";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    /***
     * Test the processLineOfInput method
     ***/
    @Test
    public void testEmptyInput() {
        ac.processLineOfInput(" \n");
        assertThat(outContent.toString(), containsString("Error: empty input provided."));
    }

    @Test
    public void testExit() {
        exit.expectSystemExit();
        ac.processLineOfInput("exit!");
        assertThat(outContent.toString(), containsString("Goodbye!"));
    }

    @Test
    public void testNoTraining() {
        ac.processLineOfInput("hi");
        assertThat(outContent.toString(),
                containsString("Error: You must train the application by entering a passage first."));
    }

    @Test
    public void testProcessInput() {
        ac.processLineOfInput(testPassage1);
        assertThat(outContent.toString(), containsString("Enter a passage or partial word (type \"exit!\" to quit):"));
        ac.processLineOfInput("i");
        assertThat(outContent.toString(), containsString("Suggestion(s): \"is\" (1)"));
        ac.processLineOfInput(testPassage2);
        ac.processLineOfInput("i");
        assertThat(outContent.toString(), containsString("Suggestion(s): \"is\" (2)"));
        System.out.println(outContent);
        assertThat(outContent.toString(), containsString("Enter a passage or partial word (type \"exit!\" to quit):"));
        ac.processLineOfInput(testPassage3);
        ac.processLineOfInput("i");
        assertThat(outContent.toString(), containsString("Suggestion(s): \"is\" (3)"));
        ac.processLineOfInput("w");
        assertThat(outContent.toString(), containsString("Suggestion(s): \"worst\" (1)"));
    }

    @Test
    public void testCaseInsensitiveInput() {
        ac.processLineOfInput(testPassage1);
        assertThat(outContent.toString(), containsString("Enter a passage or partial word (type \"exit!\" to quit):"));
        ac.processLineOfInput("a");
        assertThat(outContent.toString(), containsString("Suggestion(s): \"asymmetrik\" (1)"));
        ac.processLineOfInput(testPassage2);
        ac.processLineOfInput("be");
        assertThat(outContent.toString(), containsString("Suggestion(s): \"best\" (2)"));
    }

    /***
     * End test the processLineOfInput method
     ***/

    /***
     * Test the getWords method
     ***/
    @Test
    public void testGetWordsEmptyString() {
        ac.processLineOfInput(testPassage1);
        ac.processLineOfInput(testPassage2);
        ac.processLineOfInput(testPassage3);
        List<Candidate> list = ac.getWords(" ");
        assertThat(outContent.toString(), containsString("Error: empty fragment provided."));
        assertNull(list);
    }

    @Test
    public void testGetWordsEmptyResult() {
        ac.processLineOfInput(testPassage1);
        ac.processLineOfInput(testPassage2);
        ac.processLineOfInput(testPassage3);
        List<Candidate> list = ac.getWords("hoo");
        assertNull(list);
    }

    @Test
    public void testGetWords() {
        ac.processLineOfInput(testPassage1);
        ac.processLineOfInput(testPassage2);
        ac.processLineOfInput(testPassage3);
        List<Candidate> list = ac.getWords("i");
        Candidate c = new Candidate("is", 3);
        assertTrue(list.contains(c));
    }

    /***
     * End test the getWords method
     ***/

    /***
     * Test the train method
     ***/
    @Test
    public void testTrainEmptyString() {
        ac.train("");
        assertThat(outContent.toString(), containsString("Error: empty passage provided."));
    }

    @Test
    public void testTrainOneWord() {
        ac.train("the");
        assertThat(outContent.toString(), containsString("Error: more than one word is needed for training."));
    }

    @Test
    public void testTrainPassageWordCount() {
        ac.train(testPassage1);
        assertTrue(ac.GetWordCount().size() == 4);
        ac.train(testPassage2);
        assertTrue(ac.GetWordCount().size() == 6);
        ac.train(testPassage3);
        assertTrue(ac.GetWordCount().size() == 9);
    }

    @Test
    public void testTrainPassageAutocompleteMap() {
        ac.train(testPassage1);
        assertTrue(ac.GetAutocompleteMap().get("is").size() == 1);
        assertNull(ac.GetAutocompleteMap().get("bo"));
        assertTrue(ac.GetAutocompleteMap().get("a").size() == 1);
        ac.train(testPassage2);
        assertTrue(ac.GetAutocompleteMap().get("is").size() == 1); // still just one
        assertNull(ac.GetAutocompleteMap().get("bo"));
        assertTrue(ac.GetAutocompleteMap().get("a").size() == 2);
        assertTrue(ac.GetAutocompleteMap().get("piz").size() == 1);
        ac.train(testPassage3);
        assertTrue(ac.GetAutocompleteMap().get("is").size() == 1); // still just one
        assertTrue(ac.GetAutocompleteMap().get("is").iterator().next().getConfidence() == 3); // test the confidence
        assertNotNull(ac.GetAutocompleteMap().get("bo"));
        assertTrue(ac.GetAutocompleteMap().get("a").size() == 2);
        assertTrue(ac.GetAutocompleteMap().get("piz").size() == 1);
    }
    /***
     * End test the train method
     ***/
}