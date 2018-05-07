package com.javarush.task.task35.task3513;

import java.util.*;

//будет содержать игровую логику и хранить игровое поле.
//ответственен за все манипуляции производимые с игровым полем.
public class Model {
    private static final int FIELD_WIDTH = 4; //ширину игрового поля.
    private Tile[][] gameTiles; //состоящий из объектов класса Tile.

    // след поля будут реальизованы в методе merge
    int score;  //текущий счет
    int maxTile; //максимальный вес плитки

    boolean isSaveNeeded = true;

    // предыдущее состояние игры
    private Stack<Tile[][]> previousStates;
    // предыдущий счет
    private Stack<Integer> previousScores;

    public Model() {
        resetGameTiles();
        previousStates = new Stack<>();
        previousScores = new Stack<>();
    }

    // сохраняем в стек
    private void saveState(Tile[][] tiles){
//        Tile[][] tmp = new Tile[FIELD_WIDTH][FIELD_WIDTH];//gameTiles.clone();
//        for (int i = 0; i < gameTiles.length; i++) {
//            for (int j = 0; j <gameTiles[i].length ; j++) {
//                try {
//                    tmp[i][j] = (Tile) gameTiles[i][j].clone();
//                } catch (CloneNotSupportedException e) {
////                    e.printStackTrace();
//                }
//            }
//        }

        Tile[][] tmp = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                tmp[i][j] = new Tile(gameTiles[i][j].value);
            }
        }

        previousStates.push(tmp);
        int scoreToSave = score;
        previousScores.push(scoreToSave);
        isSaveNeeded = false;
    }

    // прошлое состояние
    public void rollback(){
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            this.score = previousScores.pop();
            this.gameTiles = previousStates.pop();
        }
    }

// выбор программы хода
    public void randomMove(){
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n){
            case 0: left();break;
            case 1: right();break;
            case 2: up();break;
            case 3: down();break;
        }
    }

    // меняется ли счет
    boolean hasBoardChanged(){

        int weightGT=0;
        int weightSt=0;

        for (int i = 0; i <gameTiles.length ; i++) {
            for (int j = 0; j <gameTiles[i].length ; j++) {
                weightGT+=gameTiles[i][j].value;
                weightSt+=previousStates.peek()[i][j].value;
            }
        }
        return weightGT!=weightSt;

    }

    // будет выбирать лучший из возможных ходов и выполнять его
    void autoMove(){
        PriorityQueue<MoveEfficiency> pq = new PriorityQueue<>(4, Collections.reverseOrder());
        pq.offer(getMoveEfficiency(this::left));
        pq.offer(getMoveEfficiency(this::up));
        pq.offer(getMoveEfficiency(this::down));
        pq.offer(getMoveEfficiency(this::right));
        MoveEfficiency mef = pq.peek();
        mef.getMove().move();

    }

    MoveEfficiency getMoveEfficiency(Move move){
        MoveEfficiency mef=null;
        move.move();
        if (hasBoardChanged()){
            mef = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }
        else mef = new MoveEfficiency(-1, 0, move);

        rollback();
        return mef;
    }

    //Меняет вес одной из пустых клеток (добавляет плитку)
    private void addTile() {
        List<Tile> listT = getEmptyTiles();
        if (!listT.isEmpty()) {
            listT.get((int) (listT.size() * Math.random())).value = (Math.random() < 0.9 ? 2 : 4);
        }
    }

    // выбор пустых клеток
    private List<Tile> getEmptyTiles() {
        List<Tile> list = new ArrayList<>();
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                if (gameTiles[i][j].isEmpty()) {
                    list.add(gameTiles[i][j]);
                }
            }
        }
        return list;
    }
    public void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
        score = 0;
        maxTile = 2;
    }
//    1. Если выполняется условие слияния плиток, проверяем является ли новое значения больше максимального и
//       при необходимости меняем значение поля maxTile.
//    2. Увеличиваем значение поля score на величину веса плитки образовавшейся в результате слияния.
//       P.S. Когда мы будем реализовывать методы движения, сжатие будет всегда выполнено перед слиянием,
//    таким образом можешь считать, что в метод mergeTiles всегда передается массив плиток без пустых в середине.
    //а) Сжатие плиток, таким образом, чтобы все пустые плитки были справа, т.е. ряд {4, 2, 0, 4} становится рядом {4, 2, 4, 0}
    private boolean compressTiles(Tile[] tiles) {
        int count = 0;
        boolean isCompress = false;
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].value == 0) {
                count++;
            } else for (int k = 0; k < count; k++) {
                tiles[i - 1].value = tiles[i].value;
                tiles[i] = new Tile();
                i--;
                if (k == count - 1) {
                    count = 0;
                }
                isCompress = true;
            }
        }
        return isCompress;
    }

//    б) Слияние плиток одного номинала, т.е. ряд {4, 4, 2, 0} становится рядом {8, 2, 0, 0}.
//    ряд {4, 4, 4, 4} превратится в {8, 8, 0, 0}, а {4, 4, 4, 0} в {8, 4, 0, 0}.
    private boolean mergeTiles(Tile[] tiles) {
        boolean isCh = false;
        for (int j = 0; j < tiles.length - 1; j++) {
            if (tiles[j].value == tiles[j + 1].value && tiles[j].value != 0) {
                tiles[j].value *=2; // удваиваем
                tiles[j + 1] = new Tile(); // меняем на пустой
                score += tiles[j].value;
                if (tiles[j].value > maxTile) {
                    maxTile = tiles[j].value;
                }
                compressTiles(tiles);
                isCh = true;
            }
        }
        return isCh;
    }

    boolean canMove(){
        List<Tile> list = getEmptyTiles();
        if (!list.isEmpty()) return true;
        for (int i = 0; i < FIELD_WIDTH-1 ; i++) {
            for (int j = 0; j < FIELD_WIDTH-1; j++) {
                if (gameTiles[i][j].value==gameTiles[i+1][j].value || gameTiles[i][j].value==gameTiles[i][j+1].value){
                    return true;
                }
            }
        }
        return false;
    }
    void left() {

        if (isSaveNeeded){
            saveState(gameTiles);
        }

        boolean isMove = false;
        for (int i = 0; i < gameTiles.length; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isMove = true;
            }
        }
        if (isMove) {
            addTile();
        }
        isSaveNeeded=true;
    }
    void right() {
        saveState(gameTiles);
        rotate90();
        rotate90();
        left();
        rotate90();
        rotate90();
    //    isSaveNeeded=false;
    }
    void up() {
        saveState(gameTiles);
        rotate90();
        left();
        rotate90();
        rotate90();
        rotate90();
     //isSaveNeeded=false;
    }
    void down() {
        saveState(gameTiles);
        rotate90();
        rotate90();
        rotate90();
        left();
        rotate90();
    //    isSaveNeeded=false;
    }

    // алгоритм переворота всего массива на 90%
    private void rotate90() {
         // Tile[][] gameTiles= this.gameTiles;
        int m = FIELD_WIDTH;
        for (int k = 0; k < m / 2; k++) // border -> center
        {
            for (int j = k; j < FIELD_WIDTH - 1 - k; j++) // left -> right
            {
                // меняем местами 4 угла
                Tile tmp = gameTiles[k][j];
                gameTiles[k][j] = gameTiles[j][m - 1 - k];
                gameTiles[j][m - 1 - k] = gameTiles[m - 1 - k][m - 1 - j];
                gameTiles[m - 1 - k][m - 1 - j] = gameTiles[m - 1 - j][k];
                gameTiles[m - 1 - j][k] = tmp;
            }
        }
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    //    void right() {
//        Tile[] tiles = new Tile[FIELD_WIDTH];
//        boolean isMove = false;
//        for (int i = FIELD_WIDTH - 1; i >= 0; i--) {
//            for (int k = FIELD_WIDTH - 1; k >= 0; k--) {
//                tiles[FIELD_WIDTH - 1 - k] = gameTiles[i][k];
//            }
//            if (compressTiles(tiles) || mergeTiles(tiles)) {
//                isMove = true;
//            }
//            for (int k = FIELD_WIDTH - 1; k >= 0; k--) {
//                gameTiles[i][k] = tiles[FIELD_WIDTH - 1 - k];
//            }
//        }
//        if (isMove) {
//            addTile();
//        }
//    }
//
//    void up() {
//        Tile[] tiles = new Tile[FIELD_WIDTH];
//        boolean isMove = false;
//        for (int i = FIELD_WIDTH - 1; i >= 0; i--) {
//            for (int k = 0; k < FIELD_WIDTH; k++) {
//                tiles[k] = gameTiles[k][i];
//            }
//            if (compressTiles(tiles) || mergeTiles(tiles)) {
//                isMove = true;
//            }
//            for (int k = 0; k < FIELD_WIDTH; k++) {
//                gameTiles[k][i] = tiles[k];
//            }
//        }
//        if (isMove) {
//            addTile();
//        }
//    }
//
//    void down() {
//        Tile[] tiles = new Tile[FIELD_WIDTH];
//        boolean isMove = false;
//        for (int i = 0; i < FIELD_WIDTH; i++) {
//            for (int k = 0; k < FIELD_WIDTH; k++) {
//                tiles[k] = gameTiles[FIELD_WIDTH - 1 - k][i];
//            }
//            if (compressTiles(tiles) || mergeTiles(tiles)) {
//                isMove = true;
//            }
//            for (int k = 0; k < FIELD_WIDTH; k++) {
//                gameTiles[FIELD_WIDTH-1-k][i] = tiles[k];
//            }
//        }
//        if (isMove) {
//            addTile();
//        }
//    }

    // для тестов
    public static void main(String[] args) {
        Model model = new Model();
        model.gameTiles = new Tile[][]{{new Tile(0), new Tile(8), new Tile(0), new Tile(16)},
                {new Tile(0), new Tile(4), new Tile(0), new Tile(2)},
                {new Tile(128), new Tile(0), new Tile(32), new Tile(0)},
                {new Tile(64), new Tile(512), new Tile(0), new Tile(0)}};


        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(model.gameTiles[i][j].value + " ");
            }
            System.out.println("");
        }

        System.out.println("*************");
        Tile[][] tmp = model.gameTiles.clone();
        for (int k = 0; k < FIELD_WIDTH; k++) {
            for (int i = 0; i < tmp.length; i++) {
                try {
                    tmp[k][i]= (Tile) model.gameTiles[k][i].clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }



        model.right();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(tmp[i][j].value + " ");
            }
            System.out.println("");
        }

//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < 4; j++) {
//                System.out.print(model.gameTiles[i][j].value + "  ");
//            }
//            System.out.println("");
//        }
//        System.out.println("");
//        model.saveState(model.gameTiles);
//        model.up();
//        Tile[][] tm =model.previousStates.pop();
//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < 4; j++) {
//                System.out.print(model.gameTiles[i][j].value + "  ");
//            }
//            System.out.println("");
//        }
    }

}
