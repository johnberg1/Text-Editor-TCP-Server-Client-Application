import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

                public class TextEditor {

                    final String USERNAME = "USER";
                    final String PASSWORD = "PASS";
                    final String WRITE = "WRTE";
                    final String APPEND = "APND";
                    final String UPDATE = "UPDT";
                    final String EXIT = "EXIT";
                    Scanner scanner;
                    String txtEditor;
                    String command ="";
                    int versionNumber = 0;
                    String text = "";
                    boolean keepConnected = true;
                    boolean update = false;

                    TextEditor () {
                        scanner = new Scanner( System.in);
                        txtEditor = "";
                    }
                    public void displayMenu (){
                        System.out.println( "1-) User name \n2-) Password\n3-) Write to\n4-) Append\n5-) Update\n6-) Show txt\n7-) Exit"  );

                        boolean valid = false;
                        while ( !valid) {
                            try{
                int opt = Integer.parseInt(scanner.nextLine());
                if (opt >= 1 && opt <= 7) {
                    valid = true;

                }
                switch (opt) {
                    case 1:
                        System.out.println("Enter user name: ");

                        String usernameArgument = scanner.nextLine();

                        addCommand( USERNAME, usernameArgument);
                        break;
                    case 2:
                        System.out.println( "Enter password: ");
                        String passwordArgument = scanner.nextLine();
                        System.out.println();
                        addCommand( PASSWORD, passwordArgument);
                        break;
                    case 3:
                        System.out.println( "Enter line number: ");
                        int lineNumber = Integer.parseInt(scanner.nextLine());
                        System.out.println("Text to write : ");
                        String text = scanner.nextLine();

                        String argument = versionNumber + " " + lineNumber + " " + text;
                        addCommand( WRITE, argument);

                        break;
                    case 4:
                        System.out.println("Text to write : ");
                        String txt = scanner.nextLine();
                        String arg = versionNumber + " " + txt;
                        addCommand( APPEND, arg);
                        break;
                    case 5:
                        addCommand( UPDATE, versionNumber + "");
                        update = true;
                        break;
                    case 6:
                        System.out.println( txtEditor);
                        break;
                    case 7:
                        keepConnected = false;
                        addCommand( EXIT, "");
                        break;
                    default:
                        System.out.println( "Please enter a valid number between 1-7 ");
                        break;
                }
            }catch (Exception e) {
                System.out.println( "Please enter a valid number between 1-7 ");
            }
        }

    }

    public void addCommand ( String type, String argument) {
        if ( argument.length() == 0) {
            command = type + "\r\n";
            return;
        }
        command = type + " " + argument + "\r\n";
    }


    public static void main(String[] args) throws IOException {
        if ( args.length != 2) {
            System.out.println("Connection error");
            return;
        }

        String serverIP = args [0];
        int serverPort = Integer.parseInt(args [1]);
        Socket clientSocket = new Socket(serverIP, serverPort);
        DataOutputStream out = new DataOutputStream( clientSocket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        TextEditor editor = new TextEditor();

        while ( editor.keepConnected ) {
            editor.displayMenu();
                System.out.println( "[CLIENT] " + editor.command);

                // Send data to server
                out.writeBytes(editor.command);
                out.flush();
                // Get response from server
            StringBuilder sb = new StringBuilder();
            int character;
            while ((character = in.read()) != -1 ) {
                // To capture \r char. After capturing \r wee add new line
                if ( character == 13){
                    character = in.read();
                    sb.append('\n');
                    break;
                }
                //To capture new line
                if (character == 10)
                    sb.append('\n');
                else
                    sb.append( (char)character ) ;
            }

                String response = sb.toString();
                System.out.println("[SERVER] " + response);
                String[] parts = response.split( " ");

                if ( editor.update && parts[0].equals("OK")) {
                    editor.versionNumber = Integer.parseInt(parts [1]);
                    System.out.println( "Version updated: " + editor.versionNumber);
                }
            editor.update = false;

        }
        editor.scanner.close();
        clientSocket.close();
    }
}