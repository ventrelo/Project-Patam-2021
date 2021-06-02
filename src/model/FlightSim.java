package model;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FlightSim{
    Socket socket;
    List<String> values;
    boolean ready;

    public boolean openSocket()
    {
        try {
            socket=new Socket("localhost", 5400);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void setValues(String path)
    {
        BufferedReader in= null;
        try {
            in = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter out= null;
        try {
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        values = new ArrayList<>();
        String line = null;
        while(true) {
            try {
                if (!((line=in.readLine())!=null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.println(line);
            out.flush();
            values.add(line);

        }
        out.close();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}


