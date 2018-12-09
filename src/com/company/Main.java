package com.company;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main extends Application {
    public static double WIDTH = 1000;
    public static double HEIGHT = 1000;
    public static final double ACTUAL_FIELD_SIZE = 358.8;
    //launches
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //WINDOW STUFF//
        primaryStage.setTitle("Gluten Free Debug Receiver v0.1");
        Group root = new Group();
        Canvas canvas = new Canvas(WIDTH,HEIGHT);
        ////////////////

        //the GraphicsContext is what we use to draw on the canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //add the canvas
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        //show the primaryStage
        primaryStage.show();




        UdpUnicastClient udpUnicastClient = new UdpUnicastClient(11115);
        Thread runner = new Thread(udpUnicastClient);
        runner.start();



        new AnimationTimer() {
            @Override public void handle(long currentNanoTime) {
                WIDTH = primaryStage.getWidth() * 1;
                HEIGHT = primaryStage.getHeight() * 1.0 - 30;

                gc.setLineWidth(10);
                drawScreen(gc);
            }
        }.start();

    }

    private void drawScreen(GraphicsContext gc) {
        gc.clearRect(0,0,2000,2000);//clear
        drawField(gc);
        drawRobot(gc);

    }

    private void drawField(GraphicsContext gc) {

        drawLineField(gc,0,0,ACTUAL_FIELD_SIZE,0,Color.BLACK);
        drawLineField(gc,ACTUAL_FIELD_SIZE,0,ACTUAL_FIELD_SIZE,ACTUAL_FIELD_SIZE,Color.BLACK);
        drawLineField(gc,ACTUAL_FIELD_SIZE,ACTUAL_FIELD_SIZE,0,ACTUAL_FIELD_SIZE,Color.BLACK);
        drawLineField(gc,0,ACTUAL_FIELD_SIZE,0,0,Color.BLACK);


        try {
            Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "/field.png"));
            gc.drawImage(image,0,0,fieldSizePixels,fieldSizePixels);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void drawRobot(GraphicsContext gc) {
        //robot radius is half the diagonal length
        double robotRadius = Math.sqrt(2) * 18.0 * 2.54 / 2.0;
        double robotX = MessageProcessing.getRobotX();
        double robotY = MessageProcessing.getRobotY();
        double robotAngle = MessageProcessing.getRobotAngle();




        double topLeftX = robotX + (robotRadius * (Math.cos(robotAngle+Math.toRadians(45))));
        double topLeftY = robotY + (robotRadius * (Math.sin(robotAngle+Math.toRadians(45))));
        double topRightX = robotX + (robotRadius * (Math.cos(robotAngle-Math.toRadians(45))));
        double topRightY = robotY + (robotRadius * (Math.sin(robotAngle-Math.toRadians(45))));
        double bottomLeftX = robotX + (robotRadius * (Math.cos(robotAngle+Math.toRadians(135))));
        double bottomLeftY = robotY + (robotRadius * (Math.sin(robotAngle+Math.toRadians(135))));
        double bottomRightX = robotX + (robotRadius * (Math.cos(robotAngle-Math.toRadians(135))));
        double bottomRightY = robotY + (robotRadius * (Math.sin(robotAngle-Math.toRadians(135))));

        Color c = Color.color(1.0,0.9,0);
        //draw the points
        drawLineField(gc,topLeftX, topLeftY, topRightX, topRightY,c);
        drawLineField(gc,topRightX, topRightY, bottomRightX, bottomRightY,c);
        drawLineField(gc,bottomRightX, bottomRightY, bottomLeftX, bottomLeftY,c);
        drawLineField(gc,bottomLeftX, bottomLeftY, topLeftX, topLeftY,c);
    }



    double fieldSizePixels = 0;
    /**
     * Converts a field point to screen coordinates
     * @param fieldPoint
     * @return
     */
    public floatPoint convertToScreen(floatPoint fieldPoint){
        fieldSizePixels = HEIGHT < WIDTH ? HEIGHT : WIDTH;
        return new floatPoint((fieldPoint.x/ACTUAL_FIELD_SIZE)*fieldSizePixels,
                (1.0-(fieldPoint.y/ACTUAL_FIELD_SIZE))*fieldSizePixels);
    }



    public void drawLineField(GraphicsContext gc,double x1, double y1, double x2, double y2,Color color){
        floatPoint first = convertToScreen(new floatPoint(x1,y1));
        floatPoint second = convertToScreen(new floatPoint(x2,y2));
        gc.setStroke(color);
        gc.strokeLine(first.x,first.y,second.x,second.y);
        gc.setStroke(Color.BLACK);
    }


}