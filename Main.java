package com.javarush.task.task35.task3513;

import javax.swing.*;

//будет содержать только метод main и служить точкой входа в наше приложение.
public class Main {

    public static void main(String[] args) {

        Model model = new Model();
        Controller controller = new Controller(model);


        JFrame game = new JFrame();         // создание фрейма
        game.setTitle("2048");             // название
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  // закрытие
        game.setSize(450, 500);     // размер
        game.setResizable(false);        // размер не меняется

        game.add(controller.getView());   // добавляем представление


        game.setLocationRelativeTo(null); // запуск по центру
        game.setVisible(true);           // виддимость
    }
}
