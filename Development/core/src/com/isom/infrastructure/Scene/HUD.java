package com.isom.infrastructure.Scene;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isom.infrastructure.Screens.PlayScreen;
import com.isom.infrastructure.WikiJump;
import sprite.Wiki;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;


public class HUD implements Disposable{

    public OrthographicCamera hudCam = new OrthographicCamera();
    private Viewport hudViewport = new FitViewport(WikiJump.V_WIDTH, WikiJump.V_HEIGHT, hudCam);
    public PlayScreen playScreen;

    private static double time;
    public static final int standardTime = 100;
    private long startTime;
    private long currentTime;
    private DecimalFormat formatter = new DecimalFormat("####.#");

    private static int score;
    private static int[] highScore = new int[3];

    public Stage stage;
    private Table table;
    private Label scoreTitleLabel, scoreLabel;
    private Label timeTitleLabel, timeLabel; // "cd" for countdown
//    private Label posLabel;
    private static Label godModeLabel;

    private BitmapFont captainFont = new BitmapFont(Gdx.files.internal("fonts/captain/captain.fnt"));

    private static FileHandle file;




    // getters & setters

    public static double getTime() {return time;}
    public static void addScore(int increment) {
        score += increment;
    }
    public static int getScore() {return score;}
    public static void addHighScore() {
        if (score > highScore[0]) {
            highScore[2] = highScore[1];
            highScore[1] = highScore[0];
            highScore[0] = score;
        } else if (score > highScore[1]) {
            highScore[2] = highScore[1];
            highScore[1] = score;
        } else if (score > highScore[2]) {
            highScore[2] = score;
        }

        String scoreStr = highScore[0] + " " + highScore[1] + " " + highScore[2] + " ";
        file.writeString(scoreStr, false);
    }
    public static int getHighScore(int index) {
        try {
            return highScore[index];
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }
    public static Label getGodModeLabel() {
        return godModeLabel;
    }







    public HUD(SpriteBatch batch, PlayScreen playScreen) {

        // read high score from load file
        file = Gdx.files.local("load/load.txt");
        String fullStr;
        try {
            fullStr = file.readString();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "The load file is lost!");
            fullStr = "0 0 0 ";
        }
        String[] scoreStrArray = fullStr.split(" ");
        int thisScore;
        for (int i=0; i<3; i++) {
            try {
                thisScore = Integer.parseInt(scoreStrArray[i]);
            } catch (Exception e) {
                thisScore = 0;
                JOptionPane.showMessageDialog(null, "The load file has been messed up.\nOne of the high scores will be re-initialized.");
            }
            if (thisScore > 8000) {
                thisScore = 0;
                JOptionPane.showMessageDialog(null, "The load file has been messed up.\nOne of the high scores will be re-initialized.");
            }
            highScore[i] = thisScore;
        }


        startTime = System.currentTimeMillis();
        time = 0;
        score = 0;

        this.playScreen = playScreen;
        stage = new Stage(hudViewport, batch);
        table = new Table();
        table.top();
        table.setFillParent(true);

        captainFont.getData().setScale(0.13f, 0.13f);


        scoreTitleLabel = new Label("SCORE",
                new Label.LabelStyle(captainFont, Color.WHITE));
        scoreLabel = new Label(String.format("%-6d", score),
                new Label.LabelStyle(captainFont, Color.WHITE));
        timeTitleLabel = new Label("TIME",
                new Label.LabelStyle(captainFont, Color.WHITE));
        timeLabel = new Label(formatter.format(time) + " s",
                new Label.LabelStyle(captainFont, Color.WHITE));
        godModeLabel = new Label(WikiJump.godMode ? "GOD MODE!" : "",
                new Label.LabelStyle(captainFont, Color.WHITE));

        //table.setDebug(true);
        table.add(scoreTitleLabel).expandX().left().padLeft(20);
        table.add(timeTitleLabel).expandX().right().padRight(20);
        table.row();
        table.add(scoreLabel).left().padLeft(20);
        table.add(timeLabel).right().padRight(20);
        table.row();
        table.add(godModeLabel).left().padLeft(20);


        // TEST HARNESS
//        table.row();
//        posLabel = new Label("null",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
//        table.add(posLabel).expandX().left().padLeft(20);

        stage.addActor(table);
    }

    public void update(float delta) {


        // update time
        currentTime = System.currentTimeMillis();
        time = (double)(currentTime - startTime)/1000;

        // update the labels
        timeLabel.setText(formatter.format(time) + "s");
        scoreLabel.setText(String.format("%d", score));


//        // update posLabel
//        posLabel.setText((int)(playScreen.wiki.getX() * WikiJump.PPM) +", "+ (int)(playScreen.wiki.getY() * WikiJump.PPM));

    }



    @Override
    public void dispose() {

        stage.dispose();
    }
}
