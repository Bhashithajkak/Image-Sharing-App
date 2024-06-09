package Client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import Server.MyFile;

public class Client {
    static JPanel jReceivedPanel;
    static JPanel jSentPanel;
    static ArrayList<MyFile> myFiles = new ArrayList<>();
    static JFrame jFrame;
    static int fileId = 0;
    static String name;

    public static void main(String[] args) throws IOException {
        name = JOptionPane.showInputDialog("Enter your name:");
        if (name == null || name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Name cannot be empty. Exiting...");
            return;
        }

        final File[] fileToSend = new File[1];
        Socket socket = new Socket("192.168.56.1", 6600);
        System.out.println("Server is connected....\n\n");

        DataOutputStream nameOutputStream = new DataOutputStream(socket.getOutputStream());
        nameOutputStream.writeUTF(name);

        ServerConnection serverConnection = new ServerConnection(socket);
        new Thread(serverConnection).start();

        jFrame = new JFrame(name);
        jFrame.setSize(600, 800);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
        jFrame.getContentPane().setBackground(new Color(245, 245, 245));

        JLabel jlTitle = new JLabel("Share Your Images");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jHeaderPanel = new JPanel();
        jHeaderPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        jHeaderPanel.setBackground(new Color(60, 63, 65));

        JButton jbSendPanel = new JButton("Send Image");
        jbSendPanel.setPreferredSize(new Dimension(150, 40));
        jbSendPanel.setFont(new Font("Arial", Font.BOLD, 15));
        jbSendPanel.setBackground(new Color(60, 63, 65));
        jbSendPanel.setForeground(Color.WHITE);
        jbSendPanel.setFocusPainted(false);

        JButton jbSentPanel = new JButton("Sent Images");
        jbSentPanel.setPreferredSize(new Dimension(150, 40));
        jbSentPanel.setFont(new Font("Arial", Font.BOLD, 15));
        jbSentPanel.setBackground(new Color(60, 63, 65));
        jbSentPanel.setForeground(Color.WHITE);
        jbSentPanel.setFocusPainted(false);

        JButton jbReceivedPanel = new JButton("Received Images");
        jbReceivedPanel.setPreferredSize(new Dimension(150, 40));
        jbReceivedPanel.setFont(new Font("Arial", Font.BOLD, 15));
        jbReceivedPanel.setBackground(new Color(60, 63, 65));
        jbReceivedPanel.setForeground(Color.WHITE);
        jbReceivedPanel.setFocusPainted(false);

        jHeaderPanel.add(jbSendPanel);
        jHeaderPanel.add(jbSentPanel);
        jHeaderPanel.add(jbReceivedPanel);

        JPanel jContentPanel = new JPanel(new CardLayout());
        jContentPanel.setBackground(new Color(245, 245, 245));
        jContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Received images
        jReceivedPanel = new JPanel();
        jReceivedPanel.setLayout(new BoxLayout(jReceivedPanel, BoxLayout.Y_AXIS));
        jReceivedPanel.setBackground(Color.WHITE);

        JScrollPane jScrollPanel = new JScrollPane(jReceivedPanel);
        jScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Sent images
        jSentPanel = new JPanel();
        jSentPanel.setLayout(new BoxLayout(jSentPanel, BoxLayout.Y_AXIS));
        jSentPanel.setBackground(Color.WHITE);

        JScrollPane jSentScrollPanel = new JScrollPane(jSentPanel);
        jSentScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Send images
        JLabel jlFileName = new JLabel("Choose an image to send");
        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
        jlFileName.setBorder(new EmptyBorder(50, 0, 0, 0));
        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jSendImageContent = new JPanel();
        jSendImageContent.setBorder(new EmptyBorder(10, 10, 10, 10));
        jSendImageContent.setBackground(new Color(245, 245, 245));
        jSendImageContent.add(jlFileName);

        JPanel jButtonPanel = new JPanel();
        jButtonPanel.setBorder(new EmptyBorder(30, 0, 10, 0));
        jButtonPanel.setBackground(new Color(245, 245, 245));

        JButton jbSendFile = new JButton("Send Image");
        jbSendFile.setPreferredSize(new Dimension(150, 75));
        jbSendFile.setFont(new Font("Arial", Font.BOLD, 20));
        jbSendFile.setBackground(new Color(60, 63, 65));
        jbSendFile.setForeground(Color.WHITE);
        jbSendFile.setFocusPainted(false);

        JButton jbChooseFile = new JButton("Choose Image");
        jbChooseFile.setPreferredSize(new Dimension(150, 75));
        jbChooseFile.setFont(new Font("Arial", Font.BOLD, 20));
        jbChooseFile.setBackground(new Color(60, 63, 65));
        jbChooseFile.setForeground(Color.WHITE);
        jbChooseFile.setFocusPainted(false);

        jButtonPanel.add(jbSendFile);
        jButtonPanel.add(jbChooseFile);

        JPanel SendImagePanel = new JPanel();
        SendImagePanel.setLayout(new BoxLayout(SendImagePanel, BoxLayout.Y_AXIS));
        SendImagePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        SendImagePanel.setBackground(new Color(245, 245, 245));

        SendImagePanel.add(jSendImageContent);
        SendImagePanel.add(jButtonPanel);

        jContentPanel.add(SendImagePanel, "SendImagePanel");
        jContentPanel.add(jScrollPanel, "ScrollPanel");
        jContentPanel.add(jSentScrollPanel, "SentScrollPanel");

        jbSendPanel.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) jContentPanel.getLayout();
            cardLayout.show(jContentPanel, "SendImagePanel");
        });

        jbSentPanel.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) jContentPanel.getLayout();
            cardLayout.show(jContentPanel, "SentScrollPanel");
        });

        jbReceivedPanel.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) jContentPanel.getLayout();
            cardLayout.show(jContentPanel, "ScrollPanel");
        });

        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Choose an Image to send");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif", "bmp");
                jFileChooser.setFileFilter(filter);

                if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    ImageIcon imageIcon = new ImageIcon(fileToSend[0].getAbsolutePath());
                    Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
                    jlFileName.setIcon(new ImageIcon(image));
                    jlFileName.setText(fileToSend[0].getName());
                }
            }
        });

        jbSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileToSend[0] == null) {
                    jlFileName.setText("Please choose an image first.");
                } else {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                        String fileName = fileToSend[0].getName();
                        byte[] fileNameBytes = fileName.getBytes();
                        byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
                        fileInputStream.read(fileContentBytes);

                        dataOutputStream.writeInt(fileNameBytes.length);
                        dataOutputStream.write(fileNameBytes);

                        dataOutputStream.writeInt(fileContentBytes.length);
                        dataOutputStream.write(fileContentBytes);

                        // Clear the fileToSend array
                        fileToSend[0] = null;

                        // Reset the label
                        jlFileName.setText("Choose an image to send");
                        jlFileName.setIcon(null);

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

                        jpFileRow.add(jlImage);
                        jSentPanel.add(jpFileRow);
                        jFrame.validate();
                    } catch (IOException error) {
                        error.printStackTrace();
                    }
                }
            }
        });

        jFrame.add(jlTitle);
        jFrame.add(jHeaderPanel);
        jFrame.add(jContentPanel);
        jFrame.setVisible(true);
    }
}
