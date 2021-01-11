package MailHeavenPackage.other;

import java.util.List;

public class LettersContainer {
    private int lettersTotalCount;
    private List<Letter> letters;

    public LettersContainer(){}

    public LettersContainer(int lettersTotalCount, List<Letter> letters) {
        this.lettersTotalCount = lettersTotalCount;
        this.letters = letters;
    }

    public int getLettersTotalCount() {
        return lettersTotalCount;
    }

    public void setLettersTotalCount(int lettersTotalCount) {
        this.lettersTotalCount = lettersTotalCount;
    }

    public List<Letter> getLetters() {
        return letters;
    }

    public void setLetters(List<Letter> letters) {
        this.letters = letters;
    }
}
