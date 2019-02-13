package com.company;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Main extends Application {
    /**
     * Dimensions of the canvas/field:
     */
    public static double WIDTH = 1000;
    public static double HEIGHT = 1000;
    public static final double ACTUAL_FIELD_SIZE = 358.8;
    public static Semaphore drawSemaphore = new Semaphore(1);




    //this is the ImageView that will hold the field background
    private ImageView fieldBackgroundImageView;

    /**
     * Launches
     */
    public static void main(String[] args){
        launch(args);
    }

    /**
     * Runs at the initialization of the window (after main)
     */
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
        primaryStage.setMaximized(true);//we can maximize by default


        /**
         * Setup the background image
         */
        Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "/field dark.png"));
        fieldBackgroundImageView = new ImageView();
        fieldBackgroundImageView.setImage(image);//set the image
        root.getChildren().add(fieldBackgroundImageView);


        //show the primaryStage
        primaryStage.show();



        UdpUnicastClient udpUnicastClient = new UdpUnicastClient(11115);
        Thread runner = new Thread(udpUnicastClient);
        runner.start();






        //CREATE A NEW ANIMATION TIMER THAT WILL CALL THE DRAWING OF THE SCREEN
        new AnimationTimer() {
            @Override public void handle(long currentNanoTime) {
                try {
                    //acquire the drawing semaphore
                    drawSemaphore.acquire();
                    //set the width and height
                    WIDTH = primaryStage.getWidth();
                    HEIGHT = primaryStage.getHeight() - 22;//36


                    fieldBackgroundImageView.setFitWidth(getFieldSizePixels());
                    fieldBackgroundImageView.setFitHeight(getFieldSizePixels());


                    gc.setLineWidth(10);
                    drawScreen(gc);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                drawSemaphore.release();

            }
        }.start();
    }


    /**
     * This will draw the screen using the graphics context
     * @param gc the graphics context
     */
    private void drawScreen(GraphicsContext gc) {
        //clear everything first
        gc.clearRect(0,0,WIDTH,HEIGHT);
        //then draw the robot
        drawRobot(gc);
        //draw all the lines and points retrieved from the phone
        drawDebugLines(gc);
        drawDebugPoints(gc);

    }



    public static ArrayList<floatPoint> displayPoints = new ArrayList<>();//all the points to display
    public static ArrayList<Line> displayLines = new ArrayList<>();//all the lines to display

    private void drawDebugPoints(GraphicsContext gc) {
        for(int i =0; i < displayPoints.size(); i ++){
            floatPoint displayLocation = convertToScreen(
                    new floatPoint(displayPoints.get(i).x, displayPoints.get(i).y));
            double radius = 5;
            gc.setStroke(new Color(0.0,1.0,1.0,0.6));

            gc.strokeOval(displayLocation.x-radius,displayLocation.y-radius,2*radius,2*radius);
        }
    }
    private void drawDebugLines(GraphicsContext gc) {
        for(int i =0; i < displayLines.size(); i ++){
            floatPoint displayLocation1 = convertToScreen(
                    new floatPoint(displayLines.get(i).x1, displayLines.get(i).y1));
            floatPoint displayLocation2 = convertToScreen(
                    new floatPoint(displayLines.get(i).x2, displayLines.get(i).y2));


            gc.setLineWidth(3);
            gc.setStroke(new Color(0.0,1.0,1.0,0.6));


            gc.strokeLine(displayLocation1.x,displayLocation1.y,displayLocation2.x,displayLocation2.y);
        }
    }


    /**
     * These can be changed to pan and zoom
     * THESE ARE REAL COORDINATES
     */
    private double globalOffsetX = 0;
    private double globalOffsetY = 0;
    private double zoomImage = 1.0;


    /**
     * This will move the background image and everything else to follow the robot
     */
    private void followRobot(double robotX, double robotY){


        globalOffsetX = robotX;// - ((WIDTH/fieldSizePixels) * ACTUAL_FIELD_SIZE/2.0);
        globalOffsetY = robotY;// + ((HEIGHT/fieldSizePixels) * ACTUAL_FIELD_SIZE/2.0);
        floatPoint screenCoordinates =
                convertToScreenScale(new floatPoint(globalOffsetX, globalOffsetY));
        fieldBackgroundImageView.setX(screenCoordinates.x);
        fieldBackgroundImageView.setY(screenCoordinates.y);
    }

    private void drawRobot(GraphicsContext gc) {
        //robot radius is half the diagonal length
        double robotRadius = Math.sqrt(2) * 18.0 * 2.54 / 2.0;
        double robotX = MessageProcessing.getRobotX();
        double robotY = MessageProcessing.getRobotY();
        double robotAngle = MessageProcessing.getRobotAngle();

        followRobot(robotX,robotY);



        double topLeftX = robotX + (robotRadius * (Math.cos(robotAngle+Math.toRadians(45))));
        double topLeftY = robotY + (robotRadius * (Math.sin(robotAngle+Math.toRadians(45))));
        double topRightX = robotX + (robotRadius * (Math.cos(robotAngle-Math.toRadians(45))));
        double topRightY = robotY + (robotRadius * (Math.sin(robotAngle-Math.toRadians(45))));
        double bottomLeftX = robotX + (robotRadius * (Math.cos(robotAngle+Math.toRadians(135))));
        double bottomLeftY = robotY + (robotRadius * (Math.sin(robotAngle+Math.toRadians(135))));
        double bottomRightX = robotX + (robotRadius * (Math.cos(robotAngle-Math.toRadians(135))));
        double bottomRightY = robotY + (robotRadius * (Math.sin(robotAngle-Math.toRadians(135))));

        Color c = Color.color(1.0,1.0,0.0);
        //draw the points
//        drawLineField(gc,topLeftX, topLeftY, topRightX, topRightY,c);
//        drawLineField(gc,topRightX, topRightY, bottomRightX, bottomRightY,c);
//        drawLineField(gc,bottomRightX, bottomRightY, bottomLeftX, bottomLeftY,c);
//        drawLineField(gc,bottomLeftX, bottomLeftY, topLeftX, topLeftY,c);
//

        try {
            floatPoint bottomLeft = convertToScreen(new floatPoint(topLeftX,topLeftY));
            double width = fieldSizePixels * (18*2.54/360.0);//calculate the width of the image in pixels

            gc.save();//save the gc
            gc.transform(new Affine(new Rotate(Math.toDegrees(-robotAngle) + 90, bottomLeft.x, bottomLeft.y)));
            Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "/robot.png"));
            gc.drawImage(image,bottomLeft.x, bottomLeft.y,width,width);


            gc.restore();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    private double fieldSizePixels = 0;//size of the field in pixels but use getter
    /**
     * Converts a field point to screen scale. This doesn't take into account the translation
     * @param fieldPoint
     * @return
     */
    public floatPoint convertToScreenScale(floatPoint fieldPoint){
        fieldSizePixels = getFieldSizePixels();
        return new floatPoint(((fieldPoint.x/ACTUAL_FIELD_SIZE)*fieldSizePixels),
                ((1.0-(fieldPoint.y/ACTUAL_FIELD_SIZE))*fieldSizePixels));
    }






    /**
     * This converts a fieldPoint to actual field coordinates
     * @param fieldPoint
     * @return
     */
    public floatPoint convertToScreen(floatPoint fieldPoint){
        //translate the dude
        floatPoint withTranslation = new floatPoint(fieldPoint.x + globalOffsetX,
                fieldPoint.y + globalOffsetY);

        return convertToScreenScale(withTranslation);
    }


    /**
     * This will give the size of the field in pixels
     * @return
     */
    public double getFieldSizePixels(){
        fieldSizePixels = HEIGHT > WIDTH ? HEIGHT : WIDTH;
        return fieldSizePixels;
    }

    public void drawLineField(GraphicsContext gc,double x1, double y1, double x2, double y2,Color color){
        floatPoint first = convertToScreen(new floatPoint(x1,y1));
        floatPoint second = convertToScreen(new floatPoint(x2,y2));
        gc.setStroke(color);
        gc.strokeLine(first.x,first.y,second.x,second.y);
        gc.setStroke(Color.BLACK);
    }


}