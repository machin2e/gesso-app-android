package camp.computer.clay.util.ImageBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import camp.computer.clay.engine.World;

/**
 * Custom composable, structured, vector image format and API.
 *
 * Notes:
 * - This is a <em>descriptive</em> image format. That is, it just stores the structure of an image.
 * - The API provides functionality for accessing features of the image, such as the shapes
 *   defining the image, functionality for manipulating the image, and functionality for saving a
 *   revision of the image.
 * - API also provides functions for serialization and deserialization, and for reading and writing
 *   files representing the image.
 *
 * Wishlist:
 * - Flexible enough to represent simple vector drawings to complex circuit diagrams and component
 *   layouts (a la Fritzing).
 *
 * Inspiration:
 * - SVG
 * - JSON
 *
 * @author Computer Camp
 * @version 1.0.0-alpha
 */
public class ImageBuilder {

    // TODO: Rename to BuildableImage? Collage? ImageBuilder?

    protected List<Shape> shapes = new ArrayList<>();

    public void addShape(Shape shape) {
        shape.setImagePosition(shape.getPosition());
        shapes.add(shape);
    }

    public Shape removeShape(int index) {
        return shapes.remove(index);
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public Shape getShape(String label) {
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

    // <FILE_IO>
    // Opens image data from JSON file stored in custom format.
    // TODO: 11/2/2016 Consider adding support for constructing ImageBuilder from SVG file.
    public static ImageBuilder open(InputStream inputStream) {

        // Create Empty image
        ImageBuilder imageBuilder = new ImageBuilder();

        // Open specified JSON file.
        String jsonString = null;
        try {
            /*
            // <HACK>
            // NOTE: Hack is for locating file in Android application resources.
            InputStream inputStream = Application.getContext().getAssets().open(filename);
            // <HACK>
            */
            int fileSize = inputStream.available();
            byte[] fileBuffer = new byte[fileSize];
            inputStream.read(fileBuffer);
            inputStream.close();
            jsonString = new String(fileBuffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create JSON object from file contents for parsing content.
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);

            JSONObject hostObject = jsonObject.getJSONObject("host"); // Handle to Host
            String hostTitle = hostObject.getString("title"); // Handle to Host's title

            JSONArray geometryArray = hostObject.getJSONArray("geometry"); // Handle to array of shapes

            // TODO: Replace with default unit and ability to specify units. Convert with device's screen characteristics.
            double scaleFactor = 6.0;

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

                // Geometry
                double x = position.getDouble("x") * scaleFactor;
                double y = position.getDouble("y") * scaleFactor;
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

                if (type.equals("Point")) {

                    // NOTE: Geometry N/A

                    Point point = new Point();
                    point.setLabel(label);
                    point.setPosition(x, y);
                    point.setRotation(rotation);
                    point.setColor(color);
                    point.setOutlineColor(outlineColor);
                    point.setOutlineThickness(outlineThickness);
                    point.isBoundary = isBoundary;

                    imageBuilder.addShape(point);

                } else if (type.equals("Rectangle")) {

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
                    rectangle.isBoundary = isBoundary;

                    imageBuilder.addShape(rectangle);

                } else if (type.equals("Circle")) {

                    double radius = shape.getDouble("radius") * scaleFactor;

                    Circle circle = new Circle(radius);
                    circle.setLabel(label);
                    circle.setPosition(x, y);
                    circle.setRotation(rotation);
                    circle.setColor(color);
                    circle.setOutlineColor(outlineColor);
                    circle.setOutlineThickness(outlineThickness);
                    circle.isBoundary = isBoundary;

                    imageBuilder.addShape(circle);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return imageBuilder;
    }
    // </FILE_IO>
}
