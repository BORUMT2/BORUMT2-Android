package com.lumorixgame.borumt2;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public JSONObject request(JSONObject request) {
        try {
            writer.println(request.toString());
            String response = reader.readLine();
            if (response == null) return null;
            return new JSONObject(response);
        } catch (Exception e) {
            return null;
        }
    }

    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (Exception ignored) {}
    }
}
