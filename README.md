# Project Name

Mobile Device Keyboard

## Requirements

* Java 8
* Maven 3.6

## Installation

Please run the following commands from inside the project folder:

`mvn package`

## Tests

Tests can be run separate from the package phase by running the following command from inside the project folder:

`mvn test`


## Usage

 To execute the program from inside the project folder please run the following command:
 
  `java -cp target\autocompleteprovider-0.1.0.jar com.dkerchner.autocompleteprovider.AutocompleteProvider`


At the `Enter a passage or partial word (type \"exit!\" to quit): ` prompt enter a passage (more than one word, i.e. "hello how are you").

At the next `Enter a passage or partial word (type \"exit!\" to quit): ` prompt either enter another passage or a word fragment (i.e. "h").

If you've typed in a word fragment, you should get a list of the possible word matches plus a confidence level (i.e. "hello" (1), "how" (1)). 

The confidence level signifies how often you've used that word which makes it likelier to be used again.

Type `exit!` at any time to close the program.

**Note**: All input is converted to lowercase and suggestions are given in lowercase.

## Possible Improvements

* An interactive UI
* Add mockito or something similar to test that methods have been called correctly
* More tests!