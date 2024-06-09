package Server;

import java.awt.Component;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ThreadHandler implements Runnable {
    private Socket client;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private ArrayList<ThreadHandler> clients;
    private String clientName;

    public ThreadHandler(Socket clientSocket, ArrayList<ThreadHandler> clients) throws IOException {
        this.client = clientSocket;
        this.clients = clients;
        this.dataInputStream = new DataInputStream(client.getInputStream());
        this.dataOutputStream = new DataOutputStream(client.getOutputStream());
    }

    @Override
    public void run() {
        try {
            clientName = dataInputStream.readUTF();
            addClientLabel(clientName);

            while (true) {
                try {
                    int fileNameLength = dataInputStream.readInt();

                    if (fileNameLength > 0) {
                        byte[] fileNameBytes = new byte[fileNameLength];
                        dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                        String fileName = new String(fileNameBytes);

                        int fileContentLength = dataInputStream.readInt();

                        if (fileContentLength > 0) {
                            byte[] fileContentBytes = new byte[fileContentLength];
                            dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);

                            outToAllClients(fileNameBytes, fileContentBytes, client);

                            JPanel jpFileRow = new JPanel();
                            jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));

                            JLabel jlFileName = new JLabel(clientName + " : " + fileName);
                            jlFileName.setFont(new Font("Arial", Font.BOLD, 14));
                            jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));
                            jlFileName.setAlignmentX(Component.LEFT_ALIGNMENT); // Align left

                            jpFileRow.setName(String.valueOf(Server.fileId));
                            jpFileRow.addMouseListener(Server.getMouseListener());
                            jpFileRow.add(jlFileName);
                            Server.jPanel.add(jpFileRow);
                            Server.jFrame.validate();

                            Server.myFiles.add(new MyFile(Server.fileId, fileName, fileContentBytes, Server.getFileExtension(fileName)));
                            Server.fileId++;
                        }
                    }
                } catch (EOFException | SocketException e) {
                    // Client has closed the connection
                    Server.logMessage(clientName + " disconnected...");
                    break;
                }
            }
        } catch (IOException error) {
            error.printStackTrace();
        } finally {
            try {
                dataInputStream.close();
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void outToAllClients(byte[] fileNameBytes, byte[] fileContentBytes, Socket author) throws IOException {
        for (ThreadHandler client : clients) {
            if (!client.client.equals(author)) {
                client.dataOutputStream.writeInt(fileNameBytes.length);
                client.dataOutputStream.write(fileNameBytes);
                client.dataOutputStream.writeInt(fileContentBytes.length);
                client.dataOutputStream.write(fileContentBytes);
            }
        }
    }

    private void addClientLabel(String clientName) {
        Server.logMessage(Server.connectedDevice+ " : named as "+clientName+"...");
    }
}
