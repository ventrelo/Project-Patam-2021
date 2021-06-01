package model;

import java.io.*;
import java.net.Socket;

public class FlightSim {
    File file;

    public Thread getThread() {
        return thread;
    }

    Thread thread;
    SocketOpen socketOpen;

    public FlightSim(File file)
    {
        this.file = file;
        socketOpen = new SocketOpen();
        socketOpen.setPath(file.getAbsolutePath());
    }
    public void start()
    {
        thread = new Thread(socketOpen);
        thread.start();
    }
    public void stop() throws InterruptedException {
        thread.wait();
    }
    public void Continue()
    {
        thread.notify();
    }

}
class SocketOpen implements Runnable
{
    String path;
    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public void run() {
        Socket fg= null;
        try {
            fg = new Socket("localhost", 5400);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader in= null;
        try {
            in = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter out= null;
        try {
            out = new PrintWriter(fg.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = null;
        while(true) {
            try {
                if (!((line=in.readLine())!=null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.println(line);
            out.flush();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        out.close();
        try {
            in.close();
            fg.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
