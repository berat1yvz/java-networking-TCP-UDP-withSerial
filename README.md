# java-networking-TCP-UDP-withSerial
This project was produced by the Yıldız Rocket Team software department support team for use in the rocket test system. The components and details used in the project are mentioned below.
## User inputs:
- user_ip
- user_port
## Equipment used:
- Raspberry Pi
- STM32 F446RE
- Connection cable
## Details:
Written in java programming language using IntelliJ IDEA. Socket structure provides communication using TCP/IP and UDP protocols. Client class is converted to .jar file and thrown to Rasberry. The client reads the data from the STM32 via serial port, saves the data to a buffer and sends it to the server. We used the “jSerialComm.jar” library for serial port reading. The server parses the data from the client with the help of the DataParse class and prints it to a .csv file one after the other. 
### Conversion to .jar file
File menu-> Project Structure or (Ctrl+Alt+Shift+S)-> Artifacts tab-> Click on the “+” button on the right-> JAR-> From modules with dependencies-> Select your base class and click OK.
![jar conversion](https://github.com/9ABDULLAH9/java-networking-TCP-UDP-withSerial/assets/63702174/feec72b4-39d3-4d4b-b34b-7858301a3a7a)


The next step is to build: Build menu-> Build Artifacts-> Build

![jar conversion_2](https://github.com/9ABDULLAH9/java-networking-TCP-UDP-withSerial/assets/63702174/053b2086-0bb5-40b7-97bf-6d92fb89a7a6)

### Finding user ip:
Open cmd-> write "ipconfig"
we use IPv4 Adress



