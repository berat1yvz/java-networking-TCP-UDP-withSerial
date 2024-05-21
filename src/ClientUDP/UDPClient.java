package ClientUDP;

import com.fazecast.jSerialComm.*;
import java.io.*;
import java.net.*;


public class UDPClient {
    static final String user_ip = "000.000.0.0";
    static final int user_port = 0000;

    public static void main(String[] args) throws IOException {
        // Seri portu seç ve başlat
        SerialPort selectedPort = selectAndOpenSerialPort();
        if (selectedPort == null) {
            System.err.println("Seri port seçimi başarısız.");
            return;
        }

        // Sunucu IP adresi ve port numarasını ayarla
        InetAddress serverAddress = InetAddress.getByName(user_ip);
        int serverPort = user_port;

        // DatagramSocket oluştur
        DatagramSocket socket = new DatagramSocket();

        // Seri porttan veri okumak için bir dinleyici oluştur
        SerialPortDataListener listener = new SerialPortDataListener() {
            StringBuilder buffer = new StringBuilder();

            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                byte[] newData = new byte[selectedPort.bytesAvailable()];
                int numRead = selectedPort.readBytes(newData, newData.length);
                String receivedData = new String(newData);

                // Veriyi tampona ekle
                buffer.append(receivedData);

                // Tamponu kontrol et ve sunucuya gönder
                if (buffer.indexOf("\n") != -1) {
                    String dataToSend = buffer.toString().trim();
                    sendDataToServer(socket, serverAddress, serverPort, dataToSend);
                    System.out.println(" - Sunucuya gönderildi: " + dataToSend);
                    buffer.setLength(0); // Tamponu temizle
                }
            }
        };

        selectedPort.addDataListener(listener);

        // Bağlantıyı izlemek için thread başlat
        Thread connectionWatcher = new Thread(() -> {
            try {
                while (true) {
                    if (socket.isClosed()) {
                        System.err.println("Sunucu bağlantısı kesildi.");
                        selectedPort.closePort();
                        selectedPort.removeDataListener();
                        System.exit(1);
                    }
                    Thread.sleep(100); // Bağlantıyı her 0.1 saniyede bir kontrol et
                }
            } catch (InterruptedException e) {
                System.err.println("Bağlantı izleyici thread kesintiye uğradı: " + e.getMessage());
            }
        });
        connectionWatcher.start();
    }

    private static void sendDataToServer(DatagramSocket socket, InetAddress serverAddress, int serverPort, String dataToSend) {
        try {
            byte[] sendData = dataToSend.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
        } catch (IOException e) {
            System.err.println("Veri gönderilemedi: " + e.getMessage());
        }
    }

    private static SerialPort selectAndOpenSerialPort() {
        // Seri portları listele
        SerialPort[] ports = SerialPort.getCommPorts();
        SerialPort selectedPort = null;
        System.out.println("Bulunan seri portlar:");

        for (SerialPort port : ports) {
            System.out.println(port.getDescriptivePortName());
            if (port.getDescriptivePortName().toLowerCase().contains("stmicroelectronics stlink virtual com port")) {//stm32 stlink
                selectedPort = port;
                break; // STMicroelectronics portunu bulduğumuzda döngüyü sonlandır
            }
        }

        if (selectedPort == null) {
            System.err.println("STMicroelectronics USB portu bulunamadı.");
            return null;
        }

        System.out.println("STMicroelectronics USB portu bulundu: " + selectedPort.getSystemPortName());

        if (selectedPort.openPort()) {
            System.out.println("Port başarıyla açıldı.");
            return selectedPort;
        } else {
            System.err.println("Port açılamadı.");
            return null;
        }
    }
}
