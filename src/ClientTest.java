import javax.swing.*;

public class ClientTest {

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1");
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.startRunning();
    }
}
