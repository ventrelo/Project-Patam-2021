package model;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FlightSim{
    Socket socket;
    List<String> values;
    PrintWriter out;
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
        if (socket != null) {


            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            values = new ArrayList<>();
            String line = null;
            while (true) {
                try {
                    if (!((line = in.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                values.add(line);

            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (values != null) {
                ready = true;
                try {
                    out = new PrintWriter(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else ready = false;
        }
    }
    public void play(int frame)
    {
        if(ready) {
            out.println(values.get(frame));
            out.flush();
        }
    }


    protected void finalize()
    {
        try {
            socket.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


