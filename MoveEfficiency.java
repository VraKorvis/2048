package com.javarush.task.task35.task3513;

public class MoveEfficiency implements Move, Comparable<MoveEfficiency> {

    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public void move() {

    }

    @Override
    public int compareTo(MoveEfficiency o) {
        return Integer.compare(numberOfEmptyTiles, o.numberOfEmptyTiles) !=0 ?
                Integer.compare(numberOfEmptyTiles, o.numberOfEmptyTiles) :
                Integer.compare(score, o.score);
    }
}
