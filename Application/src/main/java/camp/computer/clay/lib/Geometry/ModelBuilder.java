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

    // <LABEL_CACHE>
    private static long labelUidCounter = 0;
    private HashMap<String, Long> labels = new HashMap<>();

    public long addLabel(String label) {
        if (!labels.containsKey(label)) {
            labels.put(label, labelUidCounter++);
            Log.v("MODEL_FILE_LOADER", "Indexing label \"" + label + "\" as " + labels.get(label));
        }
        return labels.get(label);
    }

    public long getLabelUid(String label) {
        return labels.get(label);
    }

    // TODO: private HashMap<Long, Shape> shapes2;
    // </LABEL_CACHE>

    private static long meshUidCounter = 0;

    private HashMap<Long, ArrayList<Shape>> meshes = new HashMap<>();
//    private List<Shape> shapes = new ArrayList<>();

    public void addShape(long meshUid, Shape shape) {
//        shapes.add(shape);
        meshes.get(meshUid).add(shape);
    }

    public Shape removeShape(long meshUid, int shapeIndex) {
//        return shapes.remove(index);
        return meshes.get(meshUid).remove(shapeIndex);
    }

    public List<Shape> getShapes(long meshUid) {
//        return shapes;
        return meshes.get(meshUid);
    }

    public List<Long> getMeshUid() {
        return new ArrayList<>(meshes.keySet());
    }

    /*
    public Shape getPrimitive(String label) {
        World.getWorld().lookupCount++;
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            if (shape.getLabel().equals(label)) {
                return shape;
            }
        }
        return null;
    }

    // TODO: <REMOVE?>
    public List<Shape> getShapes(String... labels) {
        List<Shape> shapes = new ArrayList<>();
        for (int i = 0; i < this.shapes.size(); i++) {
            for (int j = 0; j < labels.length; j++) {
                Pattern pattern = Pattern.compile(labels[j]);
                Matcher matcher = pattern.matcher(shapes.get(i).getLabel());
                if (matcher.matches()) {
                    shapes.add(this.shapes.get(i));
                }
            }
        }
        return shapes;
    }
    // TODO: </REMOVE?>
    */

    // TODO: ModelComponent createModelComponent() --- after loading...

    // <FILE_IO>

    /**
     * File Format Specification Outline:
     * - Labels in file must be unique (cannot be re-used).
     * - Loader constructs index of labels, group names, and assigns a unique integer ID (asset ID?) to each for faster retrieval.
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

        // TODO: Crawl entire JSON hierarchy, extract labels, and generate UUIDs for each label in the modelBuilder.

        // Create JSON object from file contents for parsing content.
        try {
            JSONObject rootObject = new JSONObject(jsonString);
            String rootType = rootObject.getString("type");
            String rootLabel = rootObject.getString("label");

            JSONArray meshesArray = rootObject.getJSONArray("meshes"); // Handle to Host

            // <REFACTOR>
            builder.addLabel(rootLabel);
            // </REFACTOR>

            for (int m = 0; m < meshesArray.length(); m++) {

                // Create Mesh
                long meshUid = meshUidCounter++;
                builder.meshes.put(meshUid, new ArrayList<Shape>());

                JSONObject meshObject = meshesArray.getJSONObject(m);

                String meshLabel = meshObject.getString("label");

                JSONArray geometryArray = meshObject.getJSONArray("geometry"); // Handle to array of primitives

                // <REFACTOR>
                // TODO: Replace with default unit and ability to specify units. Convert with device's screen characteristics.
                double scaleFactor = 6.0;
                // </REFACTOR>

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
                    builder.addLabel(label);
                    // </REFACTOR>

                    // Primitive
                    double x = position.getDouble("x") * scaleFactor;
                    double y = position.getDouble("y") * scaleFactor;
                    double rotation = shape.getDouble("rotation");

                    // <REFACTOR>
                    // Boundary
                    boolean isBoundary = false;
                    if (shape.has("boundary")) {
                        isBoundary = shape.getBoolean("boundary");
                    }
                    // </REFACTOR>

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
                        point.setLabel(label);
                        point.setPosition(x, y);
                        point.setRotation(rotation);
                        point.setColor(color);
                        point.setOutlineColor(outlineColor);
                        point.setOutlineThickness(outlineThickness);
//                    point.isBoundary = isBoundary;

                        builder.addShape(meshUid, point);

//                    // <ENTITY>
//                    // <HACK>
//                    // Set Label
//                    Entity shapeEntity = Model.addShape(entity, point); // HACK
//                    shapeEntity.getComponent(TransformConstraint.class).relativeTransform.set(x, y);
//                    shapeEntity.getComponent(TransformConstraint.class).relativeTransform.rotation = rotation;
//                    Label.setLabel(shapeEntity, label);
//                    // </HACK>
//                    // </ENTITY>

                    } else if (type.equalsIgnoreCase("Rectangle")) {

                        double width = shape.getDouble("width") * scaleFactor;
                        double height = shape.getDouble("height") * scaleFactor;
                        double cornerRadius = shape.getDouble("cornerRadius") * scaleFactor;

                        Rectangle rectangle = new Rectangle(width, height);
                        rectangle.setLabel(label);
                        rectangle.setCornerRadius(cornerRadius);
                        rectangle.setPosition(x, y);
                        rectangle.setRotation(rotation);
                        rectangle.setColor(color);
                        rectangle.setOutlineColor(outlineColor);
                        rectangle.setOutlineThickness(outlineThickness);
//                    rectangle.isBoundary = isBoundary;

                        builder.addShape(meshUid, rectangle);

//                    // <ENTITY>
//                    // <HACK>
//                    // Set Label
//                    Entity shapeEntity = Model.addShape(entity, rectangle); // HACK
//                    shapeEntity.getComponent(TransformConstraint.class).relativeTransform.set(x, y);
//                    shapeEntity.getComponent(TransformConstraint.class).relativeTransform.rotation = rotation;
//                    Label.setLabel(shapeEntity, label);
//                    // </HACK>
//                    // </ENTITY>

                    } else if (type.equalsIgnoreCase("Circle")) {

                        double radius = shape.getDouble("radius") * scaleFactor;

                        Circle circle = new Circle(radius);
                        circle.setLabel(label);
                        circle.setPosition(x, y);
                        circle.setRotation(rotation);
                        circle.setColor(color);
                        circle.setOutlineColor(outlineColor);
                        circle.setOutlineThickness(outlineThickness);
//                    circle.isBoundary = isBoundary;

                        builder.addShape(meshUid, circle);

//                    // <ENTITY>
//                    // TODO: Move to createModelComponent(...)
//                    // <HACK>
//                    // Set Label
//                    Entity shapeEntity = Model.addShape(entity, circle); // HACK
//                    shapeEntity.getComponent(TransformConstraint.class).relativeTransform.set(x, y);
//                    shapeEntity.getComponent(TransformConstraint.class).relativeTransform.rotation = rotation;
//                    Label.setLabel(shapeEntity, label);
//                    // </HACK>
//                    // </ENTITY>
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // </PLATFORM_LAYER>

        // <DELETE>
        List<String> labelList = new ArrayList<>(builder.labels.keySet());
        for (int i = 0; i < labelList.size(); i++) {
            Log.v("MODEL_FILE_LOADER", "" + builder.labels.get(labelList.get(i)) + "\t" + labelList.get(i));
        }
        // <DELETE>

        return builder;
    }
    // </FILE_IO>

    /**
     * Returns {@code Model} component.
     *
     * @return
     */
    public void getModelComponent(long meshUid, Entity entity) {

        List<Shape> shapes = meshes.get(meshUid);

        for (int i = 0; i < shapes.size(); i++) {
            // <ENTITY>
            // <HACK>
            // Set Label
            Entity shapeEntity = Model.addShape(entity, shapes.get(i)); // HACK
            shapeEntity.getComponent(TransformConstraint.class).relativeTransform.set(shapes.get(i).getPosition().x, shapes.get(i).getPosition().y);
            shapeEntity.getComponent(TransformConstraint.class).relativeTransform.rotation = shapes.get(i).getRotation();
            Label.setLabel(shapeEntity, shapes.get(i).getLabel());
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
            Entity shapeEntity = Model.addShape(entity, shapes.get(i)); // HACK
            shapeEntity.getComponent(TransformConstraint.class).relativeTransform.set(shapes.get(i).getPosition().x, shapes.get(i).getPosition().y);
            shapeEntity.getComponent(TransformConstraint.class).relativeTransform.rotation = shapes.get(i).getRotation();
            Label.setLabel(shapeEntity, shapes.get(i).getLabel());
            // </HACK>
            // </ENTITY>
        }
    }
    */
}
