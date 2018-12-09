package com.company;

import java.util.ArrayList;

public class MessageProcessing {
    public MessageProcessing(){

    }

    public void processMessage(String receivedMessage) {
        String[] messageSplit = receivedMessage.split(";");
        if(messageSplit.length == 0){
            System.out.println("messageSplit length = 0");
            return;
        }
        receivedMessage = messageSplit[0];

        String[] crcSplit = receivedMessage.split("%");

        if(crcSplit.length != 2){
            System.out.println("Split length not 2!");
            return;
        }
        try{
            int length = Integer.parseInt(crcSplit[1]);
            if(length != crcSplit[0].length()){
                System.out.println("calc length diff from claimed!");
            }
        }catch (Exception e){
            System.out.println("try catch exception!");
            return;
        }

        receivedMessage = crcSplit[0];
        System.out.println(receivedMessage);


        //the messages are sent using commas
        String[] splitString = receivedMessage.split(",");
        String id = splitString[0];

        if(id.equals("ROBOT")){
            processRobotLocation(splitString);
        }else{
            if(id.equals("POINT")){//POINT codes for debug point, just display it on the screen as a dot
                processPoint(splitString);
            }else{
                if(id.equals("CLEAR")){
                    clear();
                }
            }
        }
    }




    private static double robotX = 0;
    private static double robotY = 0;
    private static double robotAngle = 0;

    public static double getRobotX() {
        return robotX;
    }
    public static double getRobotY() {
        return robotY;
    }
    public static double getRobotAngle() {
        return robotAngle;
    }

    /**
     * This processes the robot location and saves it's position
     * @param splitString
     */
    private void processRobotLocation(String[] splitString) {
        if(splitString.length != 4){return;}
        robotX = Double.parseDouble(splitString[1]);
        robotY = Double.parseDouble(splitString[2]);
        robotAngle = Double.parseDouble(splitString[3]);
    }


    //this handles the list of debugPoints to be drawn on the screen
    ArrayList<floatPoint> debugPoints = new ArrayList<>();

    /**
     * Takes a String[] and parses it into a point, adding it to the list of display points.
     * @param splitString
     */
    private void processPoint(String[] splitString) {
        if(splitString.length != 3){return;}
        debugPoints.add(new floatPoint(Double.parseDouble(splitString[1]),Double.parseDouble(splitString[2])));
    }


    /**
     * Clears the debug points arraylist, occurrs when the CLEAR command is send by the phone
     */
    private void clear() {
        Main.displayPoints = debugPoints;
        debugPoints.clear();

    }

}
