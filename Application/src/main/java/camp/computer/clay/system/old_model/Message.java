package camp.computer.clay.system.old_model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

public class Message {

    public final static String VERIFY_PREFIX = "got ";
    public final static int MAXIMUM_RETRY_COUNT = 10;
    public static final int RETRY_SEND_PERIOD = 5000;

    private UUID uuid = null;

    private String source; // private InetAddress fromAddress;
    private String target; // private InetAddress toAddress;
    private String content;

    private boolean verify; // if true, then verify that a response was received
    private boolean isVerified; // if true, then this message has been verified by the unit
    private String checksum; // i.e., the expected response to compare against

    private Date timeLastSent;
    private int retryCount;

    // TODO: Use enum to define Clay's supported constants.
//    public static final String UNDEFINED_PROTOCOL = "UNDEFINED";
//    public static final String UDP_PROTOCOL = "UDP";
//    public static final String TCP_PROTOCOL = "TCP";
//    public static final String HTTP_PROTOCOL = "HTTP";
    private String type;

    public Message (String type, String source, String target, String content) {
    //Message (InetAddress from, InetAddress to, String content) {

        this.uuid = UUID.randomUUID();

        this.type = type;

        this.source = source;
        this.target = target;
        this.content = content;

        timeLastSent = new Date(0);
        retryCount = 0;

        // TODO: Only do this when "verify" is true.
        if (content.startsWith (Message.VERIFY_PREFIX)) {
            this.checksum = Message.generateChecksum(this.content);
        } else {
            this.checksum = Message.generateChecksum (Message.VERIFY_PREFIX + this.content);
        }

    }

    public String getType () {
        return this.type;
    }

    // TODO: getSource().getAddress()
    public String getSourceAddress() {
        return this.source;
    }

    // TODO: getImageByCoordinate().getAddress()
    public String getTargetAddress() {
        return this.target;
    }

    public String getContent () {
        return this.content;
    }

    public boolean isDeliveryGuaranteed () {
        return verify;
    }

    public void setDeliveryGuaranteed (boolean isGuaranteed) {
        verify = isGuaranteed;
    }

    public boolean isDelivered () {
        return isVerified;
    }

    public void setDelivered (boolean delivered) {
        isVerified = delivered;
    }

    public int getRetryCount () {
        return retryCount;
    }

    public void setRetryCount (int count) {
        retryCount = count;
    }

    public Date getTimeLastSent() {
        return timeLastSent;
    }

    public void setTimeLastSent(Date time) {
        this.timeLastSent = time;
    }

    public void increaseRetryCount() {
        this.retryCount++;
    }

    public static String generateChecksum (String message) {
        return generateSha1Hash (message);
    }

    public static String generateSha1Hash (String string) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = string.getBytes ("UTF-8");
            digest.update (bytes, 0, bytes.length);
            bytes = digest.digest ();

            // This is ~55x faster than looping and String.formating()
            hash = bytesToHex (bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace ();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace ();
        }
        return hash;
    }

    // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex( byte[] bytes )
    {
        char[] hexChars = new char[ bytes.length * 2 ];
        for( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexArray[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexArray[ v & 0x0F ];
        }
        return new String( hexChars );
    }

    public String getChecksum () {
        return this.checksum;
    }
}
