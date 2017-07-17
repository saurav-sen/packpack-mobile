package com.pack.pack.application.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.cache.InMemory;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.notification.FeedMsg;
import com.pack.pack.services.exception.PackPackException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationReaderService extends Service {

    private static final String LOG_TAG = "NotificationService";

    private Timer timer;

    private AmqpConnection amqpConnection;

    public NotificationReaderService() {
        amqpConnection = new AmqpConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //new MessageSubscriber().execute(amqpConnection);
        if(timer != null) {
            timer.cancel();
        } else {
            timer = new Timer();
        }
        timer.scheduleAtFixedRate(new NotificationTimerTask(), 0, 2 * 60 * 1000);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class NotificationTimerTask extends TimerTask {

        @Override
        public void run() {
            new MessageSubscriber().execute(amqpConnection);
        }
    }

    private class AmqpConnection {

        private Connection connection;

        private Channel channel;

        private ConnectionFactory connectionFactory;

        public ConnectionFactory getConnectionFactory() {
            return connectionFactory;
        }

        public void setConnectionFactory(ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
        }

        public Connection getConnection() {
            return connection;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }
    }

    private class MessageSubscriber extends AsyncTask<AmqpConnection, Integer, AmqpConnection> {

        private boolean isFirstTime = true;

        @Override
        protected AmqpConnection doInBackground(AmqpConnection... params) {
            AmqpConnection amqpConnection = null;
            if(params == null || params.length == 0) {
                amqpConnection = new AmqpConnection();
            } else {
                amqpConnection = params[0];
            }
            subscribe(amqpConnection);
            return amqpConnection;
        }

        private void subscribe(AmqpConnection amqpConnection) {
            try {
                if(!NetworkUtil.checkConnectivity(NotificationReaderService.this)) {
                    return;
                }
                String QUEUE_NAME = null;
                String userId = AppController.getInstance().getUserId();
                if(userId != null && !userId.trim().isEmpty()) {
                    QUEUE_NAME = userId  + "_notify";
                } else {
                    return;
                }

                ConnectionFactory connectionFactory = amqpConnection.getConnectionFactory();
                Connection connection = amqpConnection.getConnection();
                Channel channel = amqpConnection.getChannel();

                if(connectionFactory == null || connection == null || !connection.isOpen() || channel == null || !channel.isOpen()) {
                    connectionFactory = new ConnectionFactory();
                    connectionFactory.setUri(ApiConstants.AMQP_URI);
                    connection = connectionFactory.newConnection();
                    channel = connection.createChannel();

                    amqpConnection.setConnectionFactory(connectionFactory);
                    amqpConnection.setConnection(connection);
                    amqpConnection.setChannel(channel);
                }

                channel.queueDeclare(QUEUE_NAME, false, false, false, null);

                String exchange_name = "Feeds_global";
                channel.exchangeDeclare(exchange_name, "fanout");

                channel.queueBind(QUEUE_NAME, exchange_name, "");
                channel.basicConsume(QUEUE_NAME, true, new MessageHandler(channel));
                //channel.close();
                //connection.close();
                Thread.sleep(3000);
                channel.close();
                connection.close();
                amqpConnection = null;
            } catch (Throwable e) {
                Log.d(LOG_TAG, e.getMessage(), e);
            }
        }
    }

    private class MessageHandler extends DefaultConsumer {

        MessageHandler(Channel channel) {
            super(channel);
        }

        private void showNotification(FeedMsg feedMsg) {
            String message =  feedMsg.getTitle();
            if(message == null) {
                return;
            }
            if(message == null) {
                message = "";
            }
            final int NOTIFICATION_ID = new Random().nextInt();//Math.abs(feedMsg.getKey())%10000;
            final NotificationManager notificationManager =
                    (NotificationManager) getSystemService(
                            Context.NOTIFICATION_SERVICE);
            final NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(NotificationReaderService.this)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle(feedMsg.getTitle())
                            .setContentText(message);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setSound(alarmSound);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    notificationBuilder.setProgress(100, 10, true);
                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
                    while(!status.isComplete()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Log.d(LOG_TAG, e.getMessage(), e);
                        }
                    }
                    if(status.isSuccess()) {
                        notificationBuilder.setContentText("Upload Photo Completed Successfully");
                    } else {
                        notificationBuilder.setContentText("Upload Photo Failed");
                    }
                    notificationBuilder.setProgress(100, 100, false);
                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
                }
            }).start();*/
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            try {
                String json = new String(body);
                FeedMsg msg = JSONUtil.deserialize(json, FeedMsg.class, true);
                showNotification(msg);
                super.handleDelivery(consumerTag, envelope, properties, body);
            } catch (Exception e) {
                Log.d(LOG_TAG, e.getMessage(), e);
            }
        }
    }
}