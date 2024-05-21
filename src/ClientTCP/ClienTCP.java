package ClientTCP;

import com.fazecast.jSerialComm.*;
import java.io.*;
import java.net.*;

public class ClienTCP {
    static final String user_ip = "000.000.0.0";
    static final int user_port = 0000;
    static final String usb_device = "USB DEVICE NAME";

    public static void main(String[] args) throws IOException {
        // Seri portu seç ve başlat
        SerialPort selectedPort = selectAndOpenSerialPort();
        if (selectedPort == null) {
            System.err.println("Seri port seçimi başarısız.");
            return;
        }

        // Sunucuya bağlan
        Socket socket = connectToServer(user_ip, user_port);
        if (socket == null) {
            System.err.println("Sunucuya bağlanılamadı.");
            selectedPort.closePort();
            return;
        }

        // Giriş ve çıkış akışlarını oluştur
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Sunucu bağlantısını izlemek için bir thread oluştur
        Thread connectionWatcher = new Thread(() -> {
            try {
                while (true) {
                    if (socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown()) {
                        System.err.println("Sunucu bağlantısı kesildi.");
                        selectedPort.closePort();
                        selectedPort.removeDataListener();
                        System.exit(1);
                    }
                    Thread.sleep(100); // Bağlantıyı her 1 saniyede bir kontrol et
                }
            } catch (InterruptedException e) {
                System.err.println("Bağlantı izleyici thread kesintiye uğradı: " + e.getMessage());
            }
        });
        connectionWatcher.start();


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
                    out.println(dataToSend);
                    System.out.println(" - Sunucuya gönderildi: " + dataToSend);
                    buffer.setLength(0); // Tamponu temizle
                }
            }
        };

        selectedPort.addDataListener(listener);
    }

    private static SerialPort selectAndOpenSerialPort() {
        // Seri portları listele
        SerialPort[] ports = SerialPort.getCommPorts();
        SerialPort selectedPort = null;
        System.out.println("Bulunan seri portlar:");

        for (SerialPort port : ports) {
            System.out.println(port.getDescriptivePortName());
            if (port.getDescriptivePortName().toLowerCase().contains(usb_device)) {
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

    private static Socket connectToServer(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            System.out.println("Sunucuya bağlanıldı.");
            return socket;
        } catch (Exception e) {
            System.err.println("Sunucuya bağlanılamadı: " + e.getMessage());
            return null;
        }
    }
}
