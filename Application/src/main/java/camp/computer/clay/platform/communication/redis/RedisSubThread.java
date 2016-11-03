package camp.computer.clay.platform.communication.redis;

import android.os.AsyncTask;
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


    // <REDIS>
    private Jedis jedis;

    public class JedisConnectToDatabaseTask extends AsyncTask<String, Void, Void> {

        private Exception exception;

        protected Void doInBackground(String... args) {
            try {
                String uri = args[0].split(":")[0];
                int port = Integer.parseInt(args[0].split(":")[1]);
                Log.v("Redis", "Jedis Task");
                //jedis = new Jedis("pub-redis-14268.us-east-1-3.3.ec2.garantiadata.com", 14268);
                jedis = new Jedis(uri, port);
                jedis.auth("testdb");
                jedis.set("foo", "bar");
                String value = jedis.get("foo");
                Log.v("Redis", "foo: " + value);

//                jedis.publish("events", args[0]);
//                Log.v("Redis", "called publish");

                new Thread(new RedisSubThread(jedis)).start();

                //return theRSSHandler.getFeed();
                return null;
            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    public void publish(String message) {
        new JedisPublishTask().execute(message);
    }

    class JedisPublishTask extends AsyncTask<String, Void, Void> {

        private Exception exception;

        protected Void doInBackground(String... urls) {
            try {
//                Log.v("Jedis", "Jedis Task");
//                Jedis jedis = new Jedis("pub-redis-14268.us-east-1-3.3.ec2.garantiadata.com", 14268);
//                jedis.auth("testdb");
//                jedis.setValue("foo", "bar");
//                String value = jedis.get("foo");
//                Log.v("Jedis", "foo: " + value);

                jedis.publish("events", urls[0]);
                Log.v("Redis", "called publish");

                //return theRSSHandler.getFeed();
                return null;
            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }

    }
    // </REDIS>
}