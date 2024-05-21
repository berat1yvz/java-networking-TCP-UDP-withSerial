
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;


public class DataParse {

    private String arr;
    private String batl;
    private String alt;

    public DataParse(){
        //setAll(str);  String str
    }

    public static float findValue(String str, String key) {
        int index = str.indexOf(key);
        if (index == -1) {
            return -1;
        }
        int endIndex = -1;
        for (int i = index + key.length(); i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isLowerCase(ch) || ch == '\n') {
                endIndex = i;
                break;
            }
        }
        if (endIndex == -1) {
            return -19;
        }
        String valueStr = str.substring(index + key.length(), endIndex);
        try {
            float value = Float.parseFloat(valueStr);
            return value;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void dataToCSV(String arr, String batl, String alt){
        String str = (arr+','+batl+','+alt+'\n');
        System.out.println("data to csv: " + str);
        csv_writing(str);
    }

    public static void csv_writing(String str) {
        String filePath = "data.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getArr() {
        return arr;
    }

    public String getAlt() {
        return alt;
    }

    public String getBatl() {
        return batl;
    }


    public void setArr(String str) {
        if (findValue(str, "acc:") != -1)
            this.arr = String.valueOf(findValue(str, "acc:"));
        else
            this.arr = "xxxxxx";
    }

    public void setAlt(String str) {
        if (findValue(str, "alt:") != -1)
            this.alt = String.valueOf(findValue(str, "alt:"));
        else
            this.alt = "xxxxxx";
    }

    public void setBatl(String str) {
        if (findValue(str, "bat:") != -1)
            this.batl = String.valueOf(findValue(str, "bat:"));
        else
            this.batl = "xxxxxx";
    }

    public void setAll(String str){
        setAlt(str);
        setBatl(str);
        setArr(str);
        dataToCSV(this.arr,this.batl,this.alt);
    }
}
