package camp.computer.clay.application.communication.RedisSub;

import android.util.Log;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisSubThread implements Runnable {

//    private Jedis jedis = null;

    public RedisSubThread(Jedis jedis) {
        Log.v("Redis", "RedisSubThread");
//        this.jedis = jedis;
    }

    public void subscribe() {
//        Jedis jedis = new Jedis("pub-redis-14268.us-east-1-3.3.ec2.garantiadata.com", 14268);
//        Scanner scanner = new Scanner(System.in);
//        System.out.printf("Enter the channel name:");
        Log.v("Redis", "RedisSubThread.subscribe()");

        Jedis jedis = new Jedis("pub-redis-14268.us-east-1-3.3.ec2.garantiadata.com", 14268);
        jedis.auth("testdb");

        String channel = "events";
//        System.out.println("Starting subscriber for channel " + channel);

//        while (true) {
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                super.onMessage(channel, message);
                Log.v("Redis", "Received on \"" + channel + "\": " + message);
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                Log.v("Redis", "onSubscribe " + channel + " " + subscribedChannels);
            }

            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                Log.v("Redis", "onUnsubscribe " + channel + " " + subscribedChannels);
            }

            @Override
            public void onPMessage(String pattern, String channel, String message) {
                Log.v("Redis", "onPMessage " + pattern + " " + channel + " " + message);
            }

            @Override
            public void onPUnsubscribe(String pattern, int subscribedChannels) {
                Log.v("Redis", "onPUnsubscribe");
            }

            @Override
            public void onPSubscribe(String pattern, int subscribedChannels) {
                Log.v("Redis", "onPSubscribe");
            }

        }, channel);
//        }
    }

    @Override
    public void run() {
        subscribe();
    }
}