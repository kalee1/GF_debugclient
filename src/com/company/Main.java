package com.company;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static com.company.Screen.convertToScreen;

public class Main extends Application {



    //this is the ImageView that will hold the field background
    private ImageView fieldBackgroundImageView;
//    private Canvas fieldCanvas;




    private Group rootGroup;//holds the grid
    //this will hold the field map and debugging information in a horizontal layout
    private HBox mainHBox;

    //this is the first element in the mainHBox and will hold the background image and canvas
    private Group fieldGroup;



    //////////////////////ALL LAYOUT PARAMETERS////////////////////////
    private final int MAIN_GRID_HORIZONTAL_GAP = 100;//horizontal spacing of the main grid
    private final int MAIN_GRID_VERTICAL_GAP = 100;//vertical spacing of the main grid
    ///////////////////////////////////////////////////////////////////




    public static Semaphore drawSemaphore = new Semaphore(1);


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
        ////////////////


        rootGroup = new Group();

        primaryStage.setMaxWidth(1000);
        primaryStage.setMaxHeight(1000);


        //Now we can setup the HBox
        mainHBox = new HBox();
        mainHBox.prefWidthProperty().bind(primaryStage.widthProperty());
//        mainHBox.widthProperty().addListener((observable, oldValue, newValue) -> {
//
//            fieldBackgroundImageView.setFitWidth(newValue.doubleValue() / 2);
//            fieldBackgroundImageView.setFitHeight(newValue.doubleValue() / 2);
//        });


        fieldGroup = new Group();//this will hold the fieldBackgroundImageView and the fieldCanvas
        ///////////////////////////////////Setup the background image/////////////////////////////////
        Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "/field dark.png"));
        fieldBackgroundImageView = new ImageView();


        fieldBackgroundImageView.setImage(image);//set the image

        fieldGroup.getChildren().add(fieldBackgroundImageView);//add the background image
        //////////////////////////////////////////////////////////////////////////////////////////////




        //Setup the canvas//
//        fieldCanvas = new Canvas(Screen.widthScreen,Screen.heightScreen);
        //the GraphicsContext is what we use to draw on the fieldCanvas
//        GraphicsContext gc = fieldCanvas.getGraphicsContext2D();
//        fieldGroup.getChildren().add(fieldCanvas);//add the canvas

        ////////////////////


        //add the fieldBackgroundImageView as the first object in the GridPane (0,0)
        mainHBox.getChildren().add(fieldGroup);

        Button testButton = new Button("memes");
        testButton.setPrefWidth(500);
        mainHBox.getChildren().add(testButton);












        //now we can add the mainHBox to the root group
        rootGroup.getChildren().add(mainHBox);
        Scene scene = new Scene(rootGroup);//create a new scene, pass the rootGroup
        scene.setFill(Color.BLACK);//we'll be black
        primaryStage.setScene(scene);//set the primary stage's scene
        primaryStage.setMaximized(true);//we can maximize by default


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
                    Screen.setDimensionsPixels(primaryStage.getWidth()*0.3,
                            primaryStage.getHeight()*0.9);
//                    fieldCanvas.setWidth(Screen.getFieldSizePixels());
//                    fieldCanvas.setHeight(Screen.getFieldSizePixels());

                    fieldBackgroundImageView.setFitWidth(mainHBox.getWidth()/2);
                    fieldBackgroundImageView.setFitHeight(mainHBox.getWidth()/2);


                    System.out.println(primaryStage.getWidth());
//                    gc.setLineWidth(10);
//                    drawScreen(gc);

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
//        gc.clearRect(0,0,Screen.widthScreen,Screen.heightScreen);
        gc.setFill(Color.RED);
        gc.fillRect(0,0,Screen.widthScreen,Screen.heightScreen);
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
     * This will move the background image and everything else to follow the robot
     */
    private void followRobot(double robotX, double robotY){
        //set the center point to the robot
//        Screen.setCenterPoint(robotX, robotY);
        Screen.setCenterPoint(Screen.ACTUAL_FIELD_SIZE/2.0,Screen.ACTUAL_FIELD_SIZE * 0.26);

        //get where the origin of the field is in pixels
        floatPoint originInPixels = convertToScreen(new floatPoint(0,Screen.ACTUAL_FIELD_SIZE));
        fieldBackgroundImageView.setX(originInPixels.x);
        fieldBackgroundImageView.setY(originInPixels.y);
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
            double width = 1.0/Screen.getCentimetersPerPixel() * 18*2.54;//calculate the width of the image in pixels

            gc.save();//save the gc
            gc.transform(new Affine(new Rotate(Math.toDegrees(-robotAngle) + 90, bottomLeft.x, bottomLeft.y)));
            Image image = new Image(new FileInputStream(System.getProperty("user.dir") + "/robot.png"));
            gc.drawImage(image,bottomLeft.x, bottomLeft.y,width,width);


            gc.restore();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }





    public void drawLineField(GraphicsContext gc,double x1, double y1, double x2, double y2,Color color){
        floatPoint first = convertToScreen(new floatPoint(x1,y1));
        floatPoint second = convertToScreen(new floatPoint(x2,y2));
        gc.setStroke(color);
        gc.strokeLine(first.x,first.y,second.x,second.y);
        gc.setStroke(Color.BLACK);
    }


}