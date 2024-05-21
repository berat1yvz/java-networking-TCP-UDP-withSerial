import java.io.*;
import java.net.*;

public class UDPServer {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(3131);
            byte[] receiveBuffer = new byte[1024];

            System.out.println("Server hazır!");

            DataParse data = new DataParse();
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String clientData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Gelen veri: " + clientData);
                data.setAll(clientData + '\n');
            }
        } catch (IOException e) {
            System.out.println("Bağlantı koptu: " + e);
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
