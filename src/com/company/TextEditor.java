package com.company;

import java.io.*;
import java.net.Socket;

public class TextEditor {

    public static void main(String[] args) {
	// write your code here
        String sentence;
        String response;
        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(System.in));

        Socket clientSocket = null;
        boolean isAuthenticated = false;

        while(!isAuthenticated){
            try {
                clientSocket = new Socket("127.0.0.1", 60000);

                System.out.println("Enter Username:");
                String username = inFromUser.readLine();
                System.out.println("Enter Password:");
                String password = inFromUser.readLine();

                BufferedReader inFromServer =
                        new BufferedReader(new
                                InputStreamReader(clientSocket.getInputStream()));

                DataOutputStream outToServer =
                        new DataOutputStream(clientSocket.getOutputStream());

                outToServer.writeBytes("USER " + username + "\r\n" + "PASS " + password + "\r\n");

                response = inFromServer.readLine();

                System.out.println("FROM SERVER: " + response);
                if (response.equals("OK"))
                    isAuthenticated = true;
                else{
                    System.out.println("Try again.\n");
                    Thread.sleep(1000);
                }

                clientSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
