package camp.computer.clay.system;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.application.Application;
import camp.computer.clay.model.architecture.Environment;
import camp.computer.clay.model.interactivity.Perspective;
import camp.computer.clay.model.architecture.Body;
import camp.computer.clay.model.architecture.Frame;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.system.host.CacheHost;
import camp.computer.clay.system.host.DisplayHostInterface;
import camp.computer.clay.system.host.MessageHost;
import camp.computer.clay.system.host.MessageHostInterface;
import camp.computer.clay.system.host.NetworkHost;
import camp.computer.clay.system.host.NetworkResourceInterface;
import camp.computer.clay.system.host.SQLiteStoreHost;
import camp.computer.clay.system.old_model.Descriptor;
import camp.computer.clay.system.old_model.Device;
import camp.computer.clay.system.old_model.Event;
import camp.computer.clay.visualization.architecture.Layer;
import camp.computer.clay.visualization.architecture.Visualization;
import camp.computer.clay.visualization.image.FrameImage;
import camp.computer.clay.visualization.image.PortImage;

public class Clay {

    private Descriptor descriptor = null;

    private MessageHost messageHost = null;

    private NetworkHost networkHost = null;

    private CacheHost cache = null;

    private SQLiteStoreHost storeHost = null;

    private Environment environment;

    private Visualization visualization;

    // Group of discovered touchscreen devices
    private List<DisplayHostInterface> displays = new ArrayList<>();

    // Group of discovered devices
    private List<Device> devices = new ArrayList<>();

    public Clay() {

        this.cache = new CacheHost(this); // Set up cache

        this.messageHost = new MessageHost(this); // Start the messaging systems

        this.networkHost = new NetworkHost(this); // Start the networking systems

        // Descriptor
        // TODO: Stream this in from the Internet and devices.
        descriptor = new Descriptor("clay", "");
        descriptor.list("devices");

        // Environment
        this.environment = new Environment();

        // Visualization
        this.visualization = new Visualization (environment);

//        setupSimulation();

        // Create body and set perspective
        Body body = new Body();
        body.getPerspective().setVisualization(visualization);

        // Add body to environment
        environment.addBody(body);

        Application.getDisplay().getSurface().setVisualization(visualization);

        simulateFrame();
        simulateFrame();
        simulateFrame();

    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public Visualization getVisualization () {
        return this.visualization;
    }

//    private void setupSimulation() {
//
//        final int SIMULATED_FORM_COUNT = Probability.generateRandomInteger(5, 10);
//
//        // <FORM_CONFIGURATION>
//        // TODO: Read this from the device (or look up from form UUID). It will be encoded on
//        // TODO: (cont'd) the device.
//        final int PORT_COUNT = 12;
//        // </FORM_CONFIGURATION>
//
//        // TODO: Move Environment/Frame this into Environment or _Ecology (in Environment) --- maybe combine Environment+_Ecology
//        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        int letterIndex = 0;
//        for (int i = 0; i < SIMULATED_FORM_COUNT; i++) {
//            Frame form = new Frame();
//            for (int j = 0; j < PORT_COUNT; j++) {
//                Port port = new Port();
//                form.addPort(port);
//
//                form.addTag(alphabet.substring(letterIndex, letterIndex + 1));
//                letterIndex = letterIndex % alphabet.length();
//            }
//            environment.addFrame(form);
//        }
//    }

    private void simulateFrame() {

        // <FORM_CONFIGURATION>
        // TODO: Read this from the device (or look up from frame UUID). It will be encoded on
        // TODO: (cont'd) the device.
        final int PORT_COUNT = 12;
        // </FORM_CONFIGURATION>

        Frame frame = new Frame();

        for (int j = 0; j < PORT_COUNT; j++) {
            Port port = new Port();
            frame.addPort(port);
        }

        environment.addFrame(frame);

        // Create frame images
        FrameImage frameImage = new FrameImage(frame);

        // Setup frame's port images
        // Add a port sprite for each of the associated base's ports
        for (Port port : frame.getPorts()) {
            PortImage portImage = new PortImage(port);
            visualization.addImage(portImage, "ports");
        }

        visualization.addImage(frameImage, "frames");
    }

    public Descriptor getDescriptor() {
        return this.descriptor;
    }

    /*
     * Clay's essential operating system functions.
     */

    public void addHost(MessageHostInterface messageManager) {
        this.messageHost.addHost(messageManager);
    }

    public void addResource (NetworkResourceInterface networkResource) {
        this.networkHost.addHost(networkResource);
    }

    /**
     * Adds a descriptor manager for use by Clay. Retrieves the basic actions provided by the
     * descriptor manager and makes them available in Clay.
     */
    public void setStore(SQLiteStoreHost contentManager) {
        this.storeHost = contentManager;
    }

    /*
     * Clay's infrastructure management functions.
     */

    /**
     * Adds a view to Clay. This makes the view available for use in systems built with Clay.
     * @param view The view to make available to Clay.
     */
    public void addDisplay(DisplayHostInterface view) {
        this.displays.add (view);
    }

    /**
     * Returns the view manager the specified index.
     * @param i The index of the view to return.
     * @return The view at the specified index.
     */
    public DisplayHostInterface getView (int i) {
        return this.displays.get (i);
    }

    public CacheHost getCache() {
        return this.cache;
    }

    public SQLiteStoreHost getStore() {
        return this.storeHost;
    }

    public List<Device> getDevices() {
        return this.devices;
    }

    public boolean hasNetworkHost() {
        return this.networkHost != null;
    }

    // TODO: Create device profile. Add this to device profile. Change to getClay().getProfile().getInternetAddress()
    public String getInternetAddress () {
        Context context = Application.getContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.v ("Clay", "Internet address: " + ip);
        return ip;
    }

    public String getInternetBroadcastAddress () {
        String broadcastAddressString = getInternetAddress();
        Log.v ("Clay", "Broadcast: " + broadcastAddressString);
        broadcastAddressString = broadcastAddressString.substring(0, broadcastAddressString.lastIndexOf("."));
        broadcastAddressString += ".255";
        return broadcastAddressString;
    }

    public Device getDeviceByAddress(String address) {
        for (Device device : getDevices()) {
            if (device.getInternetAddress ().compareTo (address) == 0) {
                return device;
            }
        }
        return null;
    }

    private Clay getClay () {
        return this;
    }

    /**
     * Adds the specified unit to Clay's operating environment.
     */
    public Device addDevice(final UUID deviceUuid, final String internetAddress) {

//        Log.v("UDP", "found device");

        // Search for the device in the store
        if (hasDeviceByUuid(deviceUuid)) {
            return null;
        }

        // Try to restore the device profile from the storeHost.
        Device device = getStore().restoreDevice(deviceUuid);

        // If unable to restore the device's profile, then create a profile for the device.
        if (device == null) {
            device = new Device(getClay (), deviceUuid);
        }

        simulateFrame();

        // <HACK>
        /*
        String[] adverbs = new String[] { "abnormally", "absentmindedly", "accidentally", "acidly", "actually", "adventurously", "afterwards", "almost", "always", "angrily", "annually", "anxiously", "arrogantly", "awkwardly", "badly", "bashfully", "beautifully", "bitterly", "bleakly", "blindly", "blissfully", "boastfully", "boldly", "bravely", "briefly", "brightly", "briskly", "broadly", "busily", "calmly", "carefully", "carelessly", "cautiously", "certainly", "cheerfully", "clearly", "cleverly", "closely", "coaxingly", "colorfully", "commonly", "continually", "coolly", "correctly", "courageously", "crossly", "cruelly", "curiously", "daily", "daintily", "dearly", "deceivingly", "delightfully", "deeply", "defiantly", "deliberately", "delightfully", "diligently", "dimly", "doubtfully", "dreamily", "easily", "elegantly", "energetically", "enormously", "enthusiastically", "equally", "especially", "even", "evenly", "eventually", "exactly", "excitedly", "extremely", "fairly", "faithfully", "famously", "far", "fast", "fatally", "ferociously", "fervently", "fiercely", "fondly", "foolishly", "fortunately", "frankly", "frantically", "freely", "frenetically", "frightfully", "fully", "furiously", "generally", "generously", "gently", "gladly", "gleefully", "gracefully", "gratefully", "greatly", "greedily", "happily", "hastily", "healthily", "heavily", "helpfully", "helplessly", "highly", "honestly", "hopelessly", "hourly", "hungrily", "immediately", "innocently", "inquisitively", "instantly", "intensely", "intently", "interestingly", "inwardly", "irritably", "jaggedly", "jealously", "joshingly", "joyfully", "joyously", "jovially", "jubilantly", "judgementally", "justly", "keenly", "kiddingly", "kindheartedly", "kindly", "kissingly", "knavishly", "knottily", "knowingly", "knowledgeably", "kookily", "lazily", "less", "lightly", "likely", "limply", "lively", "loftily", "longingly", "loosely", "lovingly", "loudly", "loyally", "madly", "majestically", "meaningfully", "mechanically", "merrily", "miserably", "mockingly", "monthly", "more", "mortally", "mostly", "mysteriously", "naturally", "nearly", "neatly", "needily", "nervously", "never", "nicely", "noisily", "not", "obediently", "obnoxiously", "oddly", "offensively", "officially", "often", "only", "openly", "optimistically", "overconfidently", "owlishly", "painfully", "partially", "patiently", "perfectly", "physically", "playfully", "politely", "poorly", "positively", "potentially", "powerfully", "promptly", "properly", "punctually", "quaintly", "quarrelsomely", "queasily", "queerly", "questionably", "questioningly", "quicker", "quickly", "quietly", "quirkily", "quizzically", "rapidly", "rarely", "readily", "really", "reassuringly", "recklessly", "regularly", "reluctantly", "repeatedly", "reproachfully", "restfully", "righteously", "rightfully", "rigidly", "roughly", "rudely", "sadly", "safely", "scarcely", "scarily", "searchingly", "sedately", "seemingly", "seldom", "selfishly", "separately", "seriously", "shakily", "sharply", "sheepishly", "shrilly", "shyly", "silently", "sleepily", "slowly", "smoothly", "softly", "solemnly", "solidly", "sometimes", "soon", "speedily", "stealthily", "sternly", "strictly", "successfully", "suddenly", "surprisingly", "suspiciously", "sweetly", "swiftly", "sympathetically", "tenderly", "tensely", "terribly", "thankfully", "thoroughly", "thoughtfully", "tightly", "tomorrow", "too", "tremendously", "triumphantly", "truly", "truthfully", "ultimately", "unabashedly", "unaccountably", "unbearably", "unethically", "unexpectedly", "unfortunately", "unimpressively", "unnaturally", "unnecessarily", "utterly", "upbeat", "upliftingly", "upright", "upside-down", "upward", "upwardly", "urgently", "usefully", "uselessly", "usually", "utterly", "vacantly", "vaguely", "vainly", "valiantly", "vastly", "verbally", "very", "viciously", "victoriously", "violently", "vivaciously", "voluntarily", "warmly", "weakly", "wearily", "well", "wetly", "wholly", "wildly", "willfully", "wisely", "woefully", "wonderfully", "worriedly", "wrongly", "yawningly", "yearly", "yearningly", "yesterday", "yieldingly", "youthfully", "zealously", "zestfully", "zestily" };
        String[] nouns = new String[] { "account", "achiever", "acoustics", "act", "action", "activity", "actor", "addition", "adjustment", "advertisement", "advice", "aftermath", "afternoon", "afterthought", "agreement", "air", "airplane", "airport", "alarm", "amount", "amusement", "anger", "rotation", "animal", "answer", "ant", "ants", "apparatus", "apparel", "apple", "apples", "appliance", "approval", "arch", "argument", "arithmetic", "arm", "army", "art", "attack", "attempt", "attention", "attraction", "aunt", "authority", "babies", "baby", "back", "badge", "bag", "bait", "balance", "ball", "balloon", "balls", "banana", "band", "base", "baseball", "basin", "basket", "basketball", "bat", "bath", "battle", "bead", "beam", "bean", "bear", "bears", "beast", "bed", "bedroom", "beds", "bee", "beef", "beetle", "beggar", "beginner", "behavior", "belief", "believe", "bell", "bells", "berry", "bike", "bikes", "bird", "birds", "birth", "birthday", "bit", "bite", "blade", "blood", "blow", "board", "boat", "boats", "body", "bomb", "bone", "book", "books", "boot", "border", "bottle", "boundary", "box", "boy", "boys", "brain", "brake", "branch", "brass", "bread", "breakfast", "breath", "brick", "bridge", "brother", "brothers", "brush", "bubble", "bucket", "building", "bulb", "bun", "burn", "burst", "bushes", "business", "butter", "button", "cabbage", "cable", "cactus", "cake", "cakes", "calculator", "calendar", "camera", "camp", "can", "cannon", "canvas", "cap", "caption", "car", "card", "care", "carpenter", "carriage", "cars", "cart", "cast", "cat", "cats", "cattle", "cause", "cave", "celery", "cellar", "cemetery", "cent", "chain", "chair", "chairs", "chalk", "chance", "change", "channel", "cheese", "cherries", "cherry", "chess", "chicken", "chickens", "children", "chin", "church", "circle", "clam", "class", "clock", "clocks", "cloth", "cloud", "clouds", "clover", "club", "coach", "coal", "coast", "coat", "cobweb", "coil", "collar", "color", "comb", "comfort", "committee", "company", "comparison", "competition", "condition", "connection", "control", "cook", "copper", "copy", "cord", "cork", "corn", "cough", "country", "cover", "cow", "cows", "crack", "cracker", "crate", "crayon", "cream", "creator", "creature", "credit", "crib", "crime", "crook", "crow", "crowd", "crown", "crush", "cry", "cub", "cup", "current", "curtain", "curve", "cushion", "dad", "daughter", "day", "death", "debt", "decision", "deer", "degree", "design", "desire", "desk", "destruction", "detail", "development", "digestion", "dime", "dinner", "dinosaurs", "direction", "dirt", "discovery", "discussion", "disease", "disgust", "distance", "distribution", "division", "dock", "doctor", "dog", "dogs", "doll", "dolls", "donkey", "door", "downtown", "drain", "drawer", "dress", "drink", "driving", "drop", "drug", "drum", "duck", "ducks", "dust", "ear", "earth", "earthquake", "edge", "education", "effect", "egg", "eggnog", "eggs", "elbow", "end", "engine", "error", "event", "example", "exchange", "existence", "expansion", "experience", "expert", "eye", "eyes", "face", "fact", "fairies", "fall", "family", "fan", "fang", "farm", "farmer", "father", "father", "faucet", "fear", "feast", "feather", "feeling", "feet", "fiction", "field", "fifth", "fight", "finger", "finger", "fire", "fireman", "fish", "flag", "flame", "flavor", "flesh", "flight", "flock", "floor", "flower", "flowers", "fly", "fog", "fold", "food", "foot", "force", "fork", "form", "fowl", "frame", "friction", "friend", "friends", "frog", "frogs", "front", "fruit", "fuel", "furniture", "alley", "game", "garden", "gate", "geese", "ghost", "giants", "giraffe", "girl", "girls", "glass", "glove", "glue", "goat", "gold", "goldfish", "good-bye", "goose", "government", "governor", "grade", "grain", "grandfather", "grandmother", "grape", "grass", "grip", "ground", "group", "growth", "guide", "guitar", "gun ", "hair", "haircut", "hall", "hammer", "hand", "hands", "harbor", "harmony", "hat", "hate", "head", "health", "hearing", "heart", "heat", "help", "hen", "hill", "history", "hobbies", "hole", "holiday", "home", "honey", "hook", "hope", "horn", "horse", "horses", "hose", "hospital", "hot", "hour", "house", "houses", "humor", "hydrant", "ice", "icicle", "idea", "impulse", "income", "increase", "industry", "ink", "insect", "instrument", "insurance", "interest", "invention", "iron", "island", "jail", "jam", "jar", "jeans", "jelly", "jellyfish", "jewel", "join", "joke", "journey", "judge", "juice", "jump", "kettle", "key", "kick", "kiss", "kite", "kitten", "kittens", "kitty", "knee", "knife", "knot", "knowledge", "laborer", "lace", "ladybug", "lake", "lamp", "land", "language", "laugh", "lawyer", "lead", "leaf", "learning", "leather", "leg", "legs", "letter", "letters", "lettuce", "level", "library", "lift", "light", "limit", "line", "linen", "lip", "liquid", "list", "lizards", "loaf", "lock", "locket", "look", "loss", "love", "low", "lumber", "lunch", "lunchroom", "machine", "magic", "maid", "mailbox", "man", "manager", "map", "marble", "mark", "market", "mask", "mass", "match", "meal", "measure", "meat", "meeting", "memory", "men", "metal", "mice", "middle", "milk", "mind", "mine", "minister", "mint", "minute", "mist", "mitten", "mom", "money", "monkey", "month", "moon", "morning", "mother", "motion", "mountain", "mouth", "move", "muscle", "music", "nail", "name", "nation", "neck", "need", "needle", "nerve", "nest", "net", "news", "night", "noise", "north", "nose", "note", "notebook", "number", "nut", "oatmeal", "observation", "ocean", "offer", "office", "oil", "operation", "opinion", "orange", "oranges", "order", "organization", "ornament", "oven", "owl", "owner", "page", "pail", "pain", "paint", "pan", "pancake", "paper", "parcel", "parent", "park", "part", "partner", "party", "passenger", "paste", "patch", "payment", "peace", "pear", "pen", "pencil", "person", "pest", "pet", "pets", "pickle", "picture", "pie", "pies", "pig", "pigs", "pin", "pipe", "pizzas", "place", "plane", "planes", "plant", "plantation", "plants", "plastic", "plate", "play", "playground", "pleasure", "plot", "plough", "pocket", "point", "poison", "police", "polish", "pollution", "popcorn", "porter", "position", "pot", "potato", "powder", "power", "price", "print", "prison", "processAction", "produce", "profit", "property", "prose", "protest", "pull", "pump", "punishment", "purpose", "push", "quarter", "quartz", "queen", "question", "quicksand", "quiet", "quill", "quilt", "quince", "quiver ", "rabbit", "rabbits", "rail", "railway", "rain", "rainstorm", "rake", "range", "rat", "rate", "ray", "reaction", "reading", "reason", "receipt", "recess", "record", "regret", "relation", "religion", "representative", "request", "respect", "rest", "reward", "rhythm", "rice", "riddle", "rifle", "ring", "rings", "river", "road", "robin", "rock", "rod", "roll", "roof", "room", "root", "rose", "route", "rub", "rule", "run", "sack", "sail", "salt", "sand", "scale", "scarecrow", "scarf", "scene", "scent", "school", "science", "scissors", "screw", "sea", "seashore", "seat", "secretary", "seed", "selection", "self", "sense", "servant", "shade", "shake", "shame", "shape", "sheep", "sheet", "shelf", "ship", "shirt", "shock", "shoe", "shoes", "shop", "show", "side", "sidewalk", "sign", "silk", "silver", "sink", "sister", "sisters", "size", "skate", "skin", "skirt", "sky", "slave", "sleep", "sleet", "slip", "slope", "smash", "smell", "smile", "smoke", "snail", "snails", "snake", "snakes", "sneeze", "snow", "soap", "society", "sock", "soda", "sofa", "son", "song", "songs", "sort", "sound", "soup", "space", "spade", "spark", "spiders", "sponge", "spoon", "spot", "spring", "spy", "square", "squirrel", "stage", "stamp", "star", "start", "statement", "station", "steam", "steel", "stem", "step", "stew", "stick", "sticks", "stitch", "stocking", "stomach", "stone", "stop", "storeHost", "story", "stove", "stranger", "straw", "stream", "street", "stretch", "string", "structure", "substance", "sugar", "suggestion", "suit", "summer", "sun", "support", "surprise", "sweater", "swim", "swing", "system", "table", "tail", "talk", "tank", "taste", "tax", "teaching", "team", "teeth", "temper", "tendency", "tent", "territory", "test", "texture", "theory", "thing", "things", "thought", "thread", "thrill", "throat", "throne", "thumb", "thunder", "ticket", "tiger", "time", "tin", "title", "toad", "toe", "toes", "tomatoes", "tongue", "tooth", "toothbrush", "toothpaste", "top", "touchPoints", "town", "toy", "toys", "trade", "trail", "train", "trains", "tramp", "transport", "tray", "treatment", "tree", "trees", "trick", "trip", "trouble", "trousers", "truck", "trucks", "tub", "turkey", "turn", "twig", "twist", "umbrella", "uncle", "underwear", "unit", "use", "vacation", "value", "van", "vase", "vegetable", "veil", "vein", "verse", "vessel", "vest", "view", "visitor", "voice", "volcano", "volleyball", "voyage", "walk", "wall", "war", "wash", "waste", "watch", "water", "wave", "waves", "wax", "way", "wealth", "weather", "week", "weight", "wheel", "whip", "whistle", "wilderness", "wind", "window", "wine", "wing", "winter", "wire", "wish", "woman", "women", "wood", "wool", "word", "work", "worm", "wound", "wren", "wrench", "wrist", "writer", "writing", "yak", "yam", "yard", "yarn", "year", "yoke" };

        Random random = new Random();
        String deviceTag = adverbs[random.nextInt(adverbs.length)] + "-" + nouns[random.nextInt(nouns.length)];
        */

        /*
        String deviceTag = "";
        if (device.getUuid().toString().equals("001affff-ffff-ffff-4e45-3158200a0027")) {
            deviceTag = "bender";
            Application.getDisplay().getSpeechGenerator().speakPhrase("bender");
        } else if (device.getUuid().toString().equals("002effff-ffff-ffff-4e45-3158200a0015")) {
            deviceTag = "kitt";
            Application.getDisplay().getSpeechGenerator().speakPhrase("kitt");
        } else if (device.getUuid().toString().equals("002fffff-ffff-ffff-4e45-3158200a0015")) {
            deviceTag = "gerty";
            Application.getDisplay().getSpeechGenerator().speakPhrase("gerty");
        } else if (device.getUuid().toString().equals("0027ffff-ffff-ffff-4e45-36932003000a")) {
            deviceTag = "hal";
            Application.getDisplay().getSpeechGenerator().speakPhrase("hal");
        }

        device.setTag(deviceTag);
        */
        // </HACK>

        // Update the device's profile based on information received from device itself.
        if (device != null) {

            // Data.
            Descriptor deviceDescriptor = getClay().getDescriptor().get("devices").put(deviceUuid.toString());

            // <HACK>
            // TODO: Update this from a list of the observables received from the boards.
            Descriptor channelsDescriptor = deviceDescriptor.list("channels");
            for (int i = 0; i < 12; i++) {

                // device/<uuid>/channels/<number>
                Descriptor channelDescriptor = channelsDescriptor.put(String.valueOf(i + 1));

                // device/<uuid>/channels/<number>/number
                channelDescriptor.put("number", String.valueOf(i + 1));

                // device/<uuid>/channels/<number>/direction
                channelDescriptor.put("direction").from("input", "output").set("input");

                // device/<uuid>/channels/<number>/type
                channelDescriptor.put("type").from("toggle", "waveform", "pulse").set("toggle"); // TODO: switch

                // device/<uuid>/channels/<number>/descriptor
                Descriptor channelContentDescriptor = channelDescriptor.put("descriptor");

                // device/<uuid>/channels/<number>/descriptor/<observable>
                // TODO: Retreive the "from" values and the "default" value from the exposed observables on the actual hardware (or the hardware profile)
                channelContentDescriptor.put("toggle_value").from("on", "off").set("off");
                channelContentDescriptor.put("waveform_sample_value", "none");
                channelContentDescriptor.put("pulse_period_seconds", "0");
                channelContentDescriptor.put("pulse_duty_cycle", "0");
            }
            // </HACK>

            // Update restored device with information from device
            device.setInternetAddress(internetAddress);

            Log.v ("TCP", "device.internetAddress: " + internetAddress);

            // Store the updated device profile.
            getStore ().storeDevice (device);
            getStore().storeTimeline(device.getTimeline());

            Log.v("TCP", "device.internetAddress (2): " + internetAddress);

            // Add device to ClayaddMessage
            if (!this.devices.contains (device)) {

                // Add device to present (i.e., local cache).
                this.devices.add (device);
                Log.v("Content_Manager", "Successfully added timeline.");

//                ApplicationView.getDisplay().mapView.getEnvironment().simulateFrame(new Frame());

                // Add timelines to attached displays
                for (DisplayHostInterface view : this.displays) {
                    view.addDeviceView(device);
                }
            }

            Log.v("TCP", "device.internetAddress (3): " + internetAddress);

            // Establish TCP connection
            device.connectTcp();

            Log.v("TCP", "device.internetAddress (4): " + internetAddress);

            /*
            // Reset the device
            if (isNew) {

                // <HACK>
                device.enqueueMessage("request reset");
                // getClay().getDeviceByUuid(UUID.fromString(sourceDeviceUuid)).enqueueMessage(propagatorMessage);
                // </HACK>

                isNew = false;
            }
            */

//            // Show the action button
//            ApplicationView.getDisplay().getCursorView().show(true);

            // Populate the device's timeline
            // TODO: Populate from scratch only if no timeline has been programmed for the device
            for (Event event : device.getTimeline().getEvents()) {
                // <HACK>
                device.enqueueMessage("start event " + event.getUuid());
                device.enqueueMessage("set event " + event.getUuid() + " action " + event.getAction().getScript().getUuid()); // <HACK />
                device.enqueueMessage("set event " + event.getUuid() + " descriptor \"" + event.getState().get(0).getState().toString() + "\"");
                // </HACK>
            }
        }

        return device;
    }

    public boolean hasDeviceByUuid(UUID uuid) {
        for (Device device : getDevices()) {
            if (device.getUuid().compareTo(uuid) == 0) {
                return true;
            }
        }
        return false;
    }

    public Device getDeviceByUuid(UUID uuid) {
        for (Device device : getDevices()) {
            if (device.getUuid().compareTo(uuid) == 0) {
                return device;
            }
        }
        return null;
    }

    public boolean hasDeviceByAddress(String address) {
        /*
        for (Patch device : getPatches()) {
            if (device.getInternetAddress().equals(address)) {
                return true;
            }
        }
        */
        return false;
    }

//    public void simulateSession (boolean addBehaviorToTimeline, int behaviorCount, boolean addAbstractBehaviorToTimeline) {
//        Log.v("Content_Manager", "simulateSession");
//
//        // Discover first device
//        UUID unitUuidA = UUID.fromString("403d4bd4-71b0-4c6b-acab-bd30c6548c71");
//        getClay().addPatch(unitUuidA, "10.1.10.29");
//        Patch foundUnit = getDeviceByUuid(unitUuidA);
//
//        // Discover second device
//        UUID unitUuidB = UUID.fromString("903d4bd4-71b0-4c6b-acab-bd30c6548c78");
//        getClay().addPatch(unitUuidB, "192.168.1.123");
//
//        if (addBehaviorToTimeline) {
//            for (int i = 0; i < behaviorCount; i++) {
//                // Create action based on action script
//                Log.v("Content_Manager", "> Creating action");
//                Random r = new Random();
//                int selectedBehaviorIndex = r.nextInt(getClay().getCache().getActions().size());
////                Script selectedBehaviorScript = getClay().getCache().getScripts().get(selectedBehaviorIndex);
////                Action action = new Action(selectedBehaviorScript);
//                Action action = getClay().getCache().getActions().get(selectedBehaviorIndex);
//                getClay().getStore().storeAction(action);
//
//                // Create event for the action and add it to the unit's timeline
//                Log.v("Content_Manager", "> Patch (UUID: " + foundUnit.getUuid() + ")");
//                Event event = new Event(foundUnit.getTimeline(), action);
//                getClay().getDeviceByUuid(unitUuidA).getTimeline().addEvent(event);
//                getClay().getStore().storeEvent(event);
//                // TODO: Update unit
//            }
//        }
//
//        if (addAbstractBehaviorToTimeline) {
//            // Create action based on action script
//            Log.v("Content_Manager", "> Creating action");
////            Action action = new Action("so high");
////            action.setDescription("oh yeah!");
////            action.addAction(foundUnit.getTimeline().getEvents().get(0).getAction());
////            action.addAction(foundUnit.getTimeline().getEvents().get(1).getAction());
////            getClay().getStore().storeAction(action);
//            List<Action> children = new ArrayList<Action>();
//            List<State> states = new ArrayList<State>();
//            children.add(foundUnit.getTimeline().getEvents().get(0).getAction());
//            states.addAll(foundUnit.getTimeline().getEvents().get(0).getState());
//            children.add(foundUnit.getTimeline().getEvents().get(1).getAction());
//            states.addAll(foundUnit.getTimeline().getEvents().get(1).getState());
//            Action action = getClay().getStore().getActionComposition(children);
//
//            // remove events for abstracted actions
//            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(0));
//            foundUnit.getTimeline().getEvents().remove(0); // if storeHost action successful
//            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(1));
//            foundUnit.getTimeline().getEvents().remove(1); // if storeHost action successful
//
//            // Create event for the action and add it to the unit's timeline
//            Log.v("Content_Manager", "> Patch (UUID: " + foundUnit.getUuid() + ")");
//            Event event = new Event(foundUnit.getTimeline(), action);
//            // insert new event for abstract action
//            //            foundUnit.getTimeline().addEvent(event);
//            event.getState().erase();
//            event.getState().addAll(states);
//            Log.v("New_Behavior_Parent", "Added " + states.size() + " states to new event.");
//            for (State descriptor : event.getState()) {
//                Log.v("New_Behavior_Parent", "\t" + descriptor.getState());
//            }
//            foundUnit.getTimeline().getEvents().add(0, event); // if storeHost event was successful
//            getClay().getStore().storeEvent(event);
//            // TODO: Update unit
//        }
//
////        if (addAbstractBehaviorToTimeline) {
////            // Create behavior based on behavior script
////            Log.v("Content_Manager", "> Creating behavior");
////            Action behavior = new Action("so so high");
////            behavior.setDescription("oh yeah!");
////            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(0), null);
////            behavior.cacheAction(foundUnit.getTimeline().getEvents().get(0).getAction());
////            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(1), null);
////            behavior.cacheAction(foundUnit.getTimeline().getEvents().get(1).getAction());
////            getClay().getStore().storeAction(behavior);
////            // remove events for abstracted actions
////            foundUnit.getTimeline().getEvents().remove(0); // if storeHost behavior successful
////            foundUnit.getTimeline().getEvents().remove(1); // if storeHost behavior successful
////
////            // Create event for the behavior and add it to the unit's timeline
////            Log.v("Content_Manager", "> Patch (UUID: " + foundUnit.getUuid() + ")");
////            Event event = new Event(foundUnit.getTimeline(), behavior);
////            // insert new event for abstract behavior
////            //            foundUnit.getTimeline().addEvent(event);
////            foundUnit.getTimeline().getEvents().add(0, event); // if storeHost event was successful
////            getClay().getStore().storeEvent(event);
////            // TODO: Update unit
////        }
//
////        getClay().notifyChange(event);
//
//        getClay().getStore().writeDatabase();
//
//        for (Patch unit : getClay().getPatches()) {
//            Log.v ("Content_Manager", "Patch (UUID: " + unit.getUuid() + ")");
//            Log.v ("Content_Manager", "\tTimeline (UUID: " + unit.getTimeline().getUuid() + ")");
//
//            int tabCount = 3;
//            for (Event e : unit.getTimeline().getEvents()) {
//                Log.v ("Content_Manager", "\t\tEvent (UUID: " + e.getUuid() + ")");
//                // TODO: Recursively print out the behavior tree
//                printBehavior (e.getAction(), tabCount);
//            }
//        }
//    }

    /**
     * Returns true if Clay has a descriptor manager.
     * @return True if Clay has a descriptor manager. False otherwise.
     */
    public boolean hasStore() {
        return this.storeHost != null;
    }

    private boolean hasCache() {
        return this.cache != null;
    }

    /**
     * Cycle through routine operations.
     */
    public void step () {
        messageHost.processMessage();
    }
}
