package com.example.andrewoshodin.fingerprintregister.comm;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

/**
 * Created by Andrew on 08/04/2018.
 */

public class IOCommunication {
    private InputStream inputStream;
    private OutputStream outputStream;

    private volatile String received = "";

    private boolean listening = false;

    enum ErrorType{TIMEOUT, READ_ERROR};

    public interface OperationInterface {
        void onReceive(String message);
        void onError(ErrorType errorType, String errorMessage);
        void onReceiveZeroLength();
    }

    @FunctionalInterface
    public interface SendListener {
        void sent(boolean sent, String errorMessage);
    }

    public IOCommunication(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void listenForMessage(final int timeout, final OperationInterface operationInterface) {
        listening = false;
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean readSuccess = false;
                try {
                    for (int i=0; i<timeout; i++){
                        Thread.sleep(1);
                        if (inputStream.available() > 0) {
                            Thread.sleep(50);
                            String inStr = "";
                            while (true) {
                                int a = inputStream.read();
                                inStr += (char)a;
                                if(inputStream.available() == 0){
                                    break;
                                }
                            }
                            received = inStr;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    operationInterface.onReceive(received);
                                    if(received.length() > 0) {
                                    } else {
                                        operationInterface.onReceiveZeroLength();
                                    }
                                }
                            });
                            readSuccess = true;
                            break;
                        }
                    }
                    if (!readSuccess) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                operationInterface.onError(ErrorType.TIMEOUT, "Timeout error occurred");
                            }
                        });
                    }
                } catch (final Exception ioe) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            operationInterface.onError(ErrorType.READ_ERROR, ioe.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    void write(int in, SendListener sendListener) {
        try {
            outputStream.write(in);
            sendListener.sent(true, null);
        } catch (IOException ioe) {
            sendListener.sent(false, ioe.getMessage());
        }
    }

    void write(byte[] in, SendListener sendListener) {
        try {
            outputStream.write(in);
            sendListener.sent(true, null);
        } catch (IOException ioe) {
            sendListener.sent(false, ioe.getMessage());
        }
    }

    void write(byte[] in, int off, int len, SendListener sendListener) {
        try {
            outputStream.write(in, off, len);
            sendListener.sent(true, null);
        } catch (IOException ioe) {
            sendListener.sent(false, ioe.getMessage());
        }
    }

    void writeString(String string, SendListener sendListener) {
        byte[] stringBytes = string.getBytes();
        write(stringBytes, sendListener);
    }

    public boolean canSend() {
        try {
            outputStream.write(65);
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}