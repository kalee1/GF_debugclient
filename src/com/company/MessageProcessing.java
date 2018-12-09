package com.company;

public class MessageProcessing {
    public MessageProcessing(){

    }

    public void processMessage(String receivedMessage) {
        //the messages are sent using commas
        String[] splitString = receivedMessage.split(",");
        if(splitString[0].equals("ROBOT")){
            processRobotLocation(splitString);
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
     * This process the robot location and saves it's position
     * @param splitString
     */
    private void processRobotLocation(String[] splitString) {
        if(splitString.length != 4){return;}
        robotX = Double.parseDouble(splitString[1]);
        robotY = Double.parseDouble(splitString[2]);
        robotAngle = Double.parseDouble(splitString[3]);
    }

}
