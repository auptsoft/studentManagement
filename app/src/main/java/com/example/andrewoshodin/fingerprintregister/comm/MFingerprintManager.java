package com.example.andrewoshodin.fingerprintregister.comm;

import java.util.ArrayList;

/**
 * Created by Andrew Oshodin on 8/21/2018.
 */

public class MFingerprintManager {
    public final static char TAKE_FINGERPRINT = 'T';

    private final static int getImgAndImg2Tz = 1;
    private final static int regModelAndStore = 3;
    private final static int uploadChar = 5;
    private final static int downChar = 6;
    private final static int search = 7;

    private final static int match = 8;

    private int cmd_delay = 5000;
    private int data_delay = 5000;

    public enum Ack{SENT, ACKNOWLEDGED, NOT_SENT, TIMEOUT, READ_ERROR}

    boolean continueSending = true;
    int frac = 0;

    public final static String GET_FINGERPRINT = "A";
    public final static String VERIFY_FINGERPRINT = "V";

    public final static String ACK_SHORT_DATA = "A";
    public final static String ACK_LONG_DATA = "D";
    public final static String ACK_ERROR_OCCURRED = "E";

    public interface OnReceiveTemplateListener {
        void onReceiveAck1(Ack ack, String ackString);
        void onReceiveAck2(Ack ack, String ackString);
        void onReceiveAck3(Ack ack, String ackString);
        void onReceiveTemplate(Ack ack, String fingerPrintTemplate);
    }

    public interface OnAcknowledgementListener {
        void onAcknowledgement(Ack ack, String ackString);
    }

    public interface OnProgressListener {
        void onStart();
        void progress(int frac, int len, String errorMsg);
        void onEnd();
        void onError(String errorMsg);
    }

    IOCommunication ioCommunication;

    public MFingerprintManager(IOCommunication ioCommunication) {
        this.ioCommunication = ioCommunication;
    }


    public void sendData(String dataString, int timeout, final OnAcknowledgementListener onAcknowledgementListener) {
        byte[] bytes = dataString.getBytes();
        sendData(bytes, timeout, onAcknowledgementListener);
    }

    public void sendData(byte[] data, final int timeout, final OnAcknowledgementListener onAcknowledgementListener) {
        ioCommunication.write(data, new IOCommunication.SendListener() {
            @Override
            public void sent(boolean st, String errorMessage) {
                if (st){
                    onAcknowledgementListener.onAcknowledgement(Ack.SENT, null);
                    ioCommunication.listenForMessage(timeout, new IOCommunication.OperationInterface() {
                        @Override
                        public void onReceive(String message) {
                            onAcknowledgementListener.onAcknowledgement(Ack.ACKNOWLEDGED, message);
                        }

                        @Override
                        public void onError(IOCommunication.ErrorType errorType, String errorMessage) {
                            switch (errorType) {
                                case TIMEOUT:
                                    onAcknowledgementListener.onAcknowledgement(Ack.TIMEOUT, errorMessage);
                                    break;
                                case READ_ERROR:
                                    onAcknowledgementListener.onAcknowledgement(Ack.READ_ERROR, errorMessage);
                            }
                        }

                        @Override
                        public void onReceiveZeroLength() {
                            onAcknowledgementListener.onAcknowledgement(Ack.ACKNOWLEDGED, null);
                        }
                    });
                }
                else {
                    onAcknowledgementListener.onAcknowledgement(Ack.NOT_SENT, null);
                }
            }
        });
    }

    public void getTemplate(final OnReceiveTemplateListener onReceiveTemplateListener) {
        sendData(""+getImgAndImg2Tz, cmd_delay, new OnAcknowledgementListener() {
            @Override
            public void onAcknowledgement(Ack ack, String ackString) {
                onReceiveTemplateListener.onReceiveAck1(ack, ackString);
                sendData(""+getImgAndImg2Tz, cmd_delay, new OnAcknowledgementListener() {
                    @Override
                    public void onAcknowledgement(Ack ack, String ackString) {
                        onReceiveTemplateListener.onReceiveAck2(ack, ackString);
                        sendData(""+regModelAndStore, cmd_delay, new OnAcknowledgementListener() {
                            @Override
                            public void onAcknowledgement(Ack ack, String ackString) {
                                onReceiveTemplateListener.onReceiveAck3(ack, ackString);
                                sendData(""+uploadChar, cmd_delay,  new OnAcknowledgementListener() {
                                    @Override
                                    public void onAcknowledgement(Ack ack, String ackString) {
                                        onReceiveTemplateListener.onReceiveTemplate(ack, ackString);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void sendTemplate(String dataString, OnAcknowledgementListener commandOnAcknowledgementListener,
                             OnAcknowledgementListener dataOnAcknowledgementListener) {
        sendData(""+downChar, cmd_delay,  commandOnAcknowledgementListener);
        sendData(dataString, 50000, dataOnAcknowledgementListener);
    }

    public void sendTemplateInBulk(final ArrayList<String> templates, final OnProgressListener onProgressListener) {
        final int len = templates.size();
        ioCommunication.write('A', new IOCommunication.SendListener() {
            @Override
            public void sent(boolean sent, String errorMessage) {
                if (sent) {
                    for (frac=0; frac<len && continueSending; frac++) {
                        sendData("D"+templates.get(frac), 5000, new OnAcknowledgementListener() {
                            @Override
                            public void onAcknowledgement(Ack ack, String ackString) {
                                switch (ack) {
                                    case ACKNOWLEDGED:
                                        onProgressListener.progress(frac, len, null);
                                        if (frac==0) {
                                            onProgressListener.onStart();
                                        }else if(frac == len-1) {
                                            onProgressListener.onEnd();
                                        }
                                        break;
                                    case READ_ERROR:
                                    case TIMEOUT:
                                    case NOT_SENT:
                                        onProgressListener.progress(frac, len, "Error occurred");
                                        continueSending = false;
                                        break;
                                }
                            }
                        });
                    }
                } else {
                    onProgressListener.onError("Could not start. Try connecting Again");
                }
            }
        });

    }

    public void verifyFingerprint(OnAcknowledgementListener onAcknowledgementListener) {
        sendData(""+search, cmd_delay, onAcknowledgementListener);
    }
}