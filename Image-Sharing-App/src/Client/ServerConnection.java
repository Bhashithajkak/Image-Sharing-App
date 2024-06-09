package Client;

import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Server.MyFile;
import Server.Server;

public class ServerConnection implements Runnable {
    private Socket serverSocket;
    private DataInputStream dataInputStream;

    public ServerConnection(Socket socket) throws IOException {
        this.serverSocket = socket;
        this.dataInputStream = new DataInputStream(serverSocket.getInputStream());

    }

    @Override
    public void run() {
        System.out.println("Thread Started....\n\n");
        try {
            while (true) {
            	int fileNameLength;
                try {
                    fileNameLength = dataInputStream.readInt();
                } catch (EOFException eof) {
                    // Client has closed the connection
                    System.out.println("Client closed the connection.");
                    break;
                }

                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength];
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);

                    int fileContentLength = dataInputStream.readInt();

                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes, 0, fileContentLength);

                        System.out.println("File received from client." + fileName);

                        JPanel jpFileRow = new JPanel();
                        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));

                        // Create JLabel to display the image
                        JLabel jlImage = new JLabel();
                        jlImage.setFont(new Font("Arial", Font.BOLD, 25));
                        jlImage.setBorder(new EmptyBorder(10, 0, 10, 5));
                        jlImage.setAlignmentX(Component.CENTER_ALIGNMENT);

                        // Display received image
                        ImageIcon imageIcon = new ImageIcon(fileContentBytes);
                        Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
                        jlImage.setIcon(new ImageIcon(image));
                        
                        /*JLabel jlFileName = new JLabel(fileName);
					jlFileName.setFont(new Font("Arial", Font.BOLD, 25));
					jlFileName.setBorder(new EmptyBorder(10,0,10,0));
					jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);
					
					if(Server.getFileExtension(fileName).equalsIgnoreCase("txt")) {
						jpFileRow.setName(String.valueOf(Client.fileId));
						jpFileRow.addMouseListener(Server.getMouseListener());
						
						jpFileRow.add(jlFileName);
						Client.jReceivedPanel.add(jpFileRow);
						Client.jFrame.validate();
					} else {
						jpFileRow.setName(String.valueOf(Client.fileId));
						jpFileRow.addMouseListener(Server.getMouseListener());
						
						jpFileRow.add(jlFileName);
						Client.jReceivedPanel.add(jpFileRow);
						
						Client.jFrame.validate();
						
					}*/

                        jpFileRow.add(jlImage);
                        Client.jReceivedPanel.add(jpFileRow);
                        Client.jFrame.validate();

                        Client.myFiles.add(new MyFile(Client.fileId, fileName, fileContentBytes, Server.getFileExtension(fileName)));

                        Client.fileId++;
                    }
                }

            }

        } catch (IOException error) {
            error.printStackTrace();
        } finally {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}