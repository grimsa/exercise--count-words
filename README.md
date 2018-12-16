## Requirements

1. The app has to:
    1. Read N text files containing English words.
    1. Count total number of occurrences of each word across all files
    1. Output pairs (word + number of occurrences) into four files:
        1. Words starting with A-G 
        1. Words starting with H-N 
        1. Words starting with O-U 
        1. Words starting with V-Z
2. The implementation must:
    1. Use threads

## Solution

Sample input and output files are included in `sample` folder.

To run the app use something like:

`gradlew run --args="--input-pattern=file:C:/temp/res/*.txt --output-dir=C:/temp/res/out"`

#### Comments

The current solution also sorts the words alphabetically and allows for very cheap creation of ranges once `WordCounts` object is populated.

An alternative `WordCounts` implementation could accepts a list of ranges at construction time and then create one `ConcurrentHashMap` per range.
That way it would be enough to just check the first letter of the word to determine which map this word belongs to when adding to `WordCounts`.
