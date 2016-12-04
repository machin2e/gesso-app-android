package camp.computer.clay.engine.component.util;

import java.util.List;

import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.Geometry;
import camp.computer.clay.util.Random;

public class HostLayoutStrategy implements LayoutStrategy<Entity> {
    @Override
    public void execute(List<Entity> hosts) {

        int minDistanceBetweenPoints = 800;

        for (int i = 0; i < hosts.size(); i++) {

            if (i == 0) {
                // Set initial position to (0, 0)
                hosts.get(i).getComponent(Physics.class).targetTransform.set(0, 0);
            } else {

                // Iterate through previously-placed points to find a new one
                Transform minDistanceTransform = null;
                double minTotalDistance = Double.MAX_VALUE;
                for (int j = 0; j < hosts.size(); j++) {

                    // Generate point at each angle
                    int startAngle = Random.getRandomInteger(0, 360);
                    for (int angle = startAngle; angle < startAngle + 360; angle++) {

                        // Generate candidate point i
                        Transform newPoint = Geometry.getRotateTranslatePoint(
                                hosts.get(j).getComponent(Physics.class).targetTransform,
                                angle % 360,
                                minDistanceBetweenPoints
                        );

                        // Check if point is valid. Check if minimum distance from all previous points.
                        boolean isValid = true;
                        double totalDistanceToPreviousPoints = 0;
                        for (int jj = 0; jj < hosts.size(); jj++) {

                            // Get distance between previously generated points and point i
                            double distanceBetweenPoints = Geometry.distance(
                                    newPoint,
                                    hosts.get(jj).getComponent(Physics.class).targetTransform
                            );

                            // Check if point is valid
                            if (distanceBetweenPoints < minDistanceBetweenPoints) {
                                isValid = false;
                                break;
                            }

                            // Add distance to point
                            totalDistanceToPreviousPoints += distanceBetweenPoints;
                        }

                        // Check if point is best candidate (nearest to all other points)
                        if (isValid) {
                            if (totalDistanceToPreviousPoints < minTotalDistance) {
                                minTotalDistance = totalDistanceToPreviousPoints;
                                minDistanceTransform = newPoint;
                            }
                        }
                    }
                }

                // Set the new point
                hosts.get(i).getComponent(Physics.class).targetTransform.set(
                        minDistanceTransform
                );
            }

            hosts.get(i).getComponent(Physics.class).targetTransform.setRotation(Random.generateRandomInteger(0, 360));

        }
    }
}
