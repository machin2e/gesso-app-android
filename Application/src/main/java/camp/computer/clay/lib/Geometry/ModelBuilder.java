package camp.computer.clay.lib.Geometry;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.TransformConstraint;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.platform.Application;

/**
 * Custom composable, structured, vector image format and API.
 * <p>
 * Implemented with the <em>builder pattern</em> (see https://en.wikipedia.org/wiki/Builder_pattern#Java).
 * <p>
 * <p>
 * Notes:
 * - This is a <em>descriptive</em> image format. That is, it just stores the structure of an image.
 * - The API provides functionality for accessing features of the image, such as the primitives
 * defining the image, functionality for manipulating the image, and functionality for saving a
 * revision of the image.
 * - API also provides functions for serialization and deserialization, and for reading and writing
 * files representing the image.
 * <p>
 * Wishlist:
 * - Flexible enough to represent simple vector drawings to complex circuit diagrams and component
 * layouts (a la Fritzing).
 * <p>
 * Inspiration:
 * - SVG
 * - JSON
 *
 * @author Computer Camp
 * @version 1.0.0-alpha
 */
public class ModelBuilder {

    // <TAG_MANAGER>
    // TODO: Make universal "TagManager"
    private static long shapeCounter = 0;

    private HashMap<String, Long> shapeTags = new HashMap<>();

    private long addTag(String tag) {
        if (!shapeTags.containsKey(tag)) {
            shapeTags.put(tag, shapeCounter++);
            Log.v("MODEL_FILE_LOADER", "Indexing tag \"" + tag + "\" as " + shapeTags.get(tag));
        }
        return shapeTags.get(tag);
    }

    public long getTagUid(String label) {
        return shapeTags.get(label);
    }
    // </TAG_MANAGER>

    // <HACK>
    private List<Shape> shapes2 = new ArrayList<>();
    // </HACK>

    private HashMap<Long, Shape> shapes = new HashMap<>();

    public long addShape(Shape shape) {
        if (!shapes.containsValue(shape)) {
            long shapeUid = shapeCounter++;
            shapes.put(shapeUid, shape);

            // <HACK>
            shapes2.add(shape);
            // </HACK>

            String tag = shape.getTag();
            if (!shapeTags.containsKey(tag)) {
                shapeTags.put(tag, shapeUid);
                Log.v("MODEL_FILE_LOADER", "Indexing tag \"" + tag + "\" as " + shapeTags.get(tag));
            }

            return shapeUid;
        }
        return -1;
    }

    /*
    // TODO: Replace with removeShape(long shapeUid) and also remove its boundary (if cached).
    public Shape removeShape(int index) {
        return shapes.remove(index);
    }
    */

    public List<Shape> getShapes() {
        // <HACK>
        return shapes2;
        // </HACK>
//        return new ArrayList<>(shapes.values());
    }

    public Shape getShape(long uid) {
        return shapes.get(uid);
    }

//    public List<Shape> getBoundaryShapes() {
//        List<Shape> boundaryShapes = new ArrayList<>();
//        for (int i = 0; i < shapes.size(); i++) {
//            if (shapes.get(i).isBoundary) {
//                boundaryShapes.add(shapes.get(i));
//            }
//        }
//        return boundaryShapes;
//    }

    /*
    public List<Shape> getShapes(String... shapeTags) {
        List<Shape> shapes = new ArrayList<>();
        for (int i = 0; i < this.shapes.size(); i++) {
            for (int j = 0; j < shapeTags.length; j++) {
                Pattern pattern = Pattern.compile(shapeTags[j]);
                Matcher matcher = pattern.matcher(shapes.get(i).getTag());
                if (matcher.matches()) {
                    shapes.add(this.shapes.get(i));
                }
            }
        }
        return shapes;
    }
    */

    // TODO: ModelComponent createModelComponent() --- after loading...

    // <FILE_IO>

    /**
     * File Format Specification Outline:
     * - Labels in file must be unique (cannot be re-used).
     * - Loader constructs index of shapeTags, group names, and assigns a unique integer ID (asset ID?) to each for faster retrieval.
     */
    public static ModelBuilder openFile(String filename) {

        // Create Empty image
        ModelBuilder builder = new ModelBuilder();

        // <PLATFORM_LAYER>
        String jsonString = null;
        try {
            InputStream inputStream = Application.getContext().getAssets().open(filename);
            int fileSize = inputStream.available();
            byte[] fileBuffer = new byte[fileSize];
            inputStream.read(fileBuffer);
            inputStream.close();
            jsonString = new String(fileBuffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO: Crawl entire JSON hierarchy, extract shapeTags, and generate UUIDs for each tag in the modelBuilder.

        // Create JSON object from file contents for parsing content.
        try {
            JSONObject rootObject = new JSONObject(jsonString);

            String rootType = rootObject.getString("type");
            String rootLabel = rootObject.getString("label");

            JSONArray geometryArray = rootObject.getJSONArray("geometry"); // Handle to array of primitives

            // <DELETE>
            // TODO: Replace with default unit and ability to specify units. Convert with device's screen characteristics.
            double scaleFactor = 6.0;
            // </DELETE>

            for (int i = 0; i < geometryArray.length(); i++) {
                JSONObject shape = geometryArray.getJSONObject(i);
                JSONObject position = shape.getJSONObject("position");

                JSONObject style = null;
                if (shape.has("style")) {
                    style = shape.getJSONObject("style");
                }

                // Description
                String label = shape.getString("label");
                String type = shape.getString("type");

                // <REFACTOR>
//                builder.addTag(tag);
                // </REFACTOR>

                // Position
                double x = 0, y = 0, z = 0;
                if (position.has("x")) {
                    x = position.getDouble("x") * scaleFactor; // TODO: Remove scaleFactor. Use conversion from mm to DP.
                }
                if (position.has("y")) {
                    y = position.getDouble("y") * scaleFactor; // TODO: Remove scaleFactor. Use conversion from mm to DP.
                }
                if (position.has("z")) {
                    z = position.getDouble("z") * scaleFactor; // TODO: Remove scaleFactor. Use conversion from mm to DP.
                }
                double rotation = shape.getDouble("rotation");

                // Boundary
                boolean isBoundary = false;
                if (shape.has("boundary")) {
                    isBoundary = shape.getBoolean("boundary");
                }

                // Style
                String color = "#ffffff";
                String outlineColor = "#000000";
                double outlineThickness = 0.0;

                if (style != null) {
                    if (style.has("color")) {
                        color = style.getString("color");
                    }
                    if (style.has("outlineColor")) {
                        outlineColor = style.getString("outlineColor");
                    }
                    if (style.has("outlineThickness")) {
                        outlineThickness = style.getDouble("outlineThickness") * scaleFactor;
                    }
                }

                if (type.equalsIgnoreCase("Point")) {

                    Point point = new Point();
                    point.setTag(label);
                    point.setPosition(x, y, z);
                    point.setRotation(rotation);
                    point.setColor(color);
                    point.setOutlineColor(outlineColor);
                    point.setOutlineThickness(outlineThickness);
                    point.isBoundary = isBoundary;

                    builder.addShape(point);

                } else if (type.equalsIgnoreCase("Rectangle")) {

                    double width = shape.getDouble("width") * scaleFactor;
                    double height = shape.getDouble("height") * scaleFactor;
                    double cornerRadius = shape.getDouble("cornerRadius") * scaleFactor;

                    Rectangle rectangle = new Rectangle(width, height);
                    rectangle.setTag(label);
                    rectangle.setCornerRadius(cornerRadius);
                    rectangle.setPosition(x, y, z);
                    rectangle.setRotation(rotation);
                    rectangle.setColor(color);
                    rectangle.setOutlineColor(outlineColor);
                    rectangle.setOutlineThickness(outlineThickness);
                    rectangle.isBoundary = isBoundary;

                    builder.addShape(rectangle);

                } else if (type.equalsIgnoreCase("Circle")) {

                    double radius = shape.getDouble("radius") * scaleFactor;

                    Circle circle = new Circle(radius);
                    circle.setTag(label);
                    circle.setPosition(x, y, z);
                    circle.setRotation(rotation);
                    circle.setColor(color);
                    circle.setOutlineColor(outlineColor);
                    circle.setOutlineThickness(outlineThickness);
                    circle.isBoundary = isBoundary;

                    builder.addShape(circle);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // </PLATFORM_LAYER>

        return builder;
    }
    // </FILE_IO>

    /**
     * Returns {@code Model} component.
     *
     * @return
     */
    public void getModelComponent(Entity entity) {

        List<Shape> shapes = getShapes();

        for (int i = 0; i < shapes.size(); i++) {
            // <ENTITY>
            // <HACK>
            // Set Label
            Entity primitive = Model.createPrimitiveFromShape(shapes.get(i));
            primitive.getComponent(Transform.class).z = shapes.get(i).getPosition().z;
            Model.addPrimitive(entity, primitive); // HACK
            primitive.getComponent(TransformConstraint.class).relativeTransform.set(shapes.get(i).getPosition().x, shapes.get(i).getPosition().y);
            primitive.getComponent(TransformConstraint.class).relativeTransform.rotation = shapes.get(i).getRotation();
            Label.setLabel(primitive, shapes.get(i).getTag()); // TODO: Remove?
            // </HACK>
            // </ENTITY>
        }
    }

    /*
    // TODO: Model getModel();
    public Model getModelComponent() {

        Model model = new Model();

        for (int i = 0; i < shapes.size(); i++) {
            // <ENTITY>
            // <HACK>
            // Set Label
            model.
            Entity shapeEntity = Model.addPrimitive(entity, shapes.get(i)); // HACK
            shapeEntity.getComponent(TransformConstraint.class).relativeTransform.set(shapes.get(i).getPosition().x, shapes.get(i).getPosition().y);
            shapeEntity.getComponent(TransformConstraint.class).relativeTransform.rotation = shapes.get(i).getRotation();
            Label.setTag(shapeEntity, shapes.get(i).getTag());
            // </HACK>
            // </ENTITY>
        }
    }
    */
}
