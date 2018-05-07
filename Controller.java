package com.javarush.task.task35.task3513;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//будет следить за нажатием клавиш во время игры.
public class Controller  extends KeyAdapter {

    private static final int WINNING_TILE = 2048;  //вес плитки при достижении которого игра будет считаться выигранной.

    private Model model;
    private View view;

    public Controller(Model model) {

        this.model = model;
        this.view=new View(this);
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Tile[][] getGameTiles(){
        return model.getGameTiles();
    }

    public int getScore(){
        return model.score;
    }

    public void resetGame(){
        model.score=0;
        model.maxTile=2;
        view.isGameWon=false;
        view.isGameLost=false;
        model.resetGameTiles();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        if (keyEvent.getKeyCode()==KeyEvent.VK_ESCAPE){
            model=new Model();
            this.resetGame();
        }
        if (!model.canMove()) view.isGameLost=true;

        if (!view.isGameLost && !view.isGameWon){
            if (keyEvent.getKeyCode()==KeyEvent.VK_LEFT){
                model.left();
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_RIGHT){
                model.right();
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_UP){
                model.up();
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_DOWN){
                model.down();
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_Z){
                model.rollback();
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_R){
                model.randomMove();
            }
            if (keyEvent.getKeyCode()==KeyEvent.VK_A){
                model.autoMove();
            }

        }
        if (model.maxTile==WINNING_TILE) view.isGameWon=true;
        view.repaint();
    }

    public View getView() {
        return view;
    }
}
