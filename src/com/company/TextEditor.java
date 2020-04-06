package com.company;

import java.io.*;
import java.net.Socket;

public class TextEditor {
    static BufferedReader inFromUser =
            new BufferedReader(new InputStreamReader(System.in));
    static String currentVersion = "0";
    static String commandToSend = "";
    static boolean update = false;
    static boolean exit = false;
    public static void main(String[] args) {
	// write your code here
        String sentence;
        String response;
        Socket clientSocket = null;
        boolean isAuthenticated = false;
        BufferedReader inFromServer = null;
        DataOutputStream outToServer = null;
        String updatedText = "";
        String responseRest = "";
        String textFile = "";
        try {
            clientSocket = new Socket("127.0.0.1", 60000);


        } catch (IOException e) {
            e.printStackTrace();
        }

        while(!isAuthenticated){
            try {
                inFromServer =
                        new BufferedReader(new
                                InputStreamReader(clientSocket.getInputStream()));

                outToServer =
                        new DataOutputStream(clientSocket.getOutputStream());
                System.out.println("Enter Username:");
                String username = inFromUser.readLine();
                System.out.println("Enter Password:");
                String password = inFromUser.readLine();

                outToServer.writeBytes("USER " + username + "\r\n" + "PASS " + password + "\r\n");
                outToServer.flush();
                response = inFromServer.readLine();
                response = inFromServer.readLine();
                System.out.println("FROM SERVER: " + response);
                if (response.substring(0,2).equals("OK")){
                    isAuthenticated = true;
                }
                else{
                    System.out.println("Try again.\n");
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while(isAuthenticated){
            try {
                inFromServer =
                        new BufferedReader(new
                                InputStreamReader(clientSocket.getInputStream()));

                outToServer =
                        new DataOutputStream(clientSocket.getOutputStream());
                printMenu();
                outToServer.writeBytes(commandToSend);
                outToServer.flush();
                response = inFromServer.readLine();
                String[] respDivided = response.split(" ");

                if (update && respDivided[0].equals("OK")){
                    updatedText = "";
                    updatedText += respDivided[2];
                    currentVersion = respDivided[1];
                    update = false;
                    while(inFromServer.ready()){
                        responseRest = inFromServer.readLine();
                        //while((responseRest = inFromServer.readLine()) != null){
                        updatedText = updatedText + System.lineSeparator() + responseRest;
                    }
                    textFile = updatedText;
                    System.out.println("FROM SERVER: " + textFile);
                }
                else{
                    System.out.println("FROM SERVER: " + response);
                }
                if (exit)
                    isAuthenticated = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printMenu(){
        try {
            System.out.println("Enter 1 to write, 2 to append, 3 to update, 4 to exit");
            String cmd = inFromUser.readLine();
            String msg = "";
            String lineNum = "";
            switch (cmd){
                case "1":
                    System.out.println("Enter the line number");
                    lineNum = inFromUser.readLine();
                    System.out.println("Enter your message");
                    msg = inFromUser.readLine();
                    commandToSend = "WRTE " + currentVersion + " " + lineNum + " " + msg + "\r\n";
                    break;
                case "2":
                    System.out.println("Enter your message");
                    msg = inFromUser.readLine();
                    commandToSend = "APND " + currentVersion + " " + msg + "\r\n";
                    break;
                case "3":
                    System.out.println("Updating to the latest version!");
                    update = true;
                    commandToSend = "UPDT " + currentVersion + "\r\n";
                    break;
                case "4":
                    commandToSend = "EXIT" + "\r\n";
                    exit = true;
                    break;
                default:{
                    System.out.println("The number you have entered is not valid: ");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
