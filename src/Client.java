import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    public Client(String host) {
        super("ChatClient");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendMessage(e.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300, 150);
        setVisible(true);
    }

    public void startRunning() {
        try {
            connectToServer();
            setupStreams();
            whileChatting();
        } catch (EOFException e) {
            showMessage("\n Client termonated connection");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeProgram();
        }
    }

    private void connectToServer() throws IOException {
        showMessage("Attempting connection... \n");
        connection = new Socket(InetAddress.getByName(serverIP), 6754);
        showMessage("Connected to: " + connection.getInetAddress().getHostName());
    }

    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("You are connected. Your streams are fine!");
    }

    private void whileChatting() throws IOException {
        ableToType(true);
        do {
            try {
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException e) {
                showMessage("\n Unknown object type");
            }
        } while (!message.equals("SERVER - END"));
    }

    private void closeProgram() {
        showMessage("\n Closing the application...");
        ableToType(false);
        try {
            output.close();
            input.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        try {
            output.writeObject("CLIENT - " + message);
            output.flush();
            showMessage("\n CLIENT - " + message);
        } catch (IOException e) {
            chatWindow.append("\n Failure, no message wos sent");
        }
    }

    private void showMessage(final String m) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(m);
                    }
                }
        );
    }

    private void ableToType(final boolean canType) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(canType);
                    }
                }
        );
    }
}
