package camp.computer.clay.engine.system;

import android.util.Log;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.lib.Geometry.Rectangle;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.util.DeviceDimensionsHelper;
import camp.computer.clay.util.Geometry;

public class CameraSystem extends System {

    Group<Entity> cameraEntities;
    //    Group<Entity> pathAndPortEntities, portEntities;
    Group<Entity> hostAndExtensionEntities;

    public CameraSystem(World world) {
        super(world);
        setup();
    }

    private void setup() {
        cameraEntities = world.entityManager.subscribe(Group.Filters.filterWithComponents, Camera.class);
//        portEntities = world.entityManager.subscribe(Group.Filters.filterWithComponent, Port.class);
//        pathAndPortEntities = world.entityManager.subscribe(Group.Filters.filterWithComponent, Path.class, Port.class);
        hostAndExtensionEntities = world.entityManager.subscribe(Group.Filters.filterWithComponent, Host.class, Extension.class);
    }

    @Override
    public void update(long dt) {
        for (int i = 0; i < cameraEntities.size(); i++) {
            updateCamera(cameraEntities.get(i), dt);
        }
    }

    private void updateCamera(Entity camera, long dt) {

        // Update focus
        computeCameraFocus(camera);

        // <REFACTOR>
        // TODO: Move into PhysicsSystem (if possible!)
        Transform transformComponent = camera.getComponent(Transform.class);
        Physics physicsComponent = camera.getComponent(Physics.class);

//        double dt = world.engine.platform.getRenderSurface().renderer.dt; // REFACTORs

        // TODO: Replace use of targetTransform with animation based on facingDirection (i.e., vector; or normal vector?)
        transformComponent.x += (physicsComponent.targetTransform.x - transformComponent.x) * physicsComponent.velocity.x * dt;
        transformComponent.y += (physicsComponent.targetTransform.y - transformComponent.y) * physicsComponent.velocity.y * dt;

        transformComponent.scale += (physicsComponent.targetTransform.scale - transformComponent.scale) * physicsComponent.velocity.z * dt;
        // </REFACTOR>
    }

    private void computeCameraFocus(Entity camera) {

        Camera cameraComponent = camera.getComponent(Camera.class);

        if (cameraComponent.mode == Camera.Mode.FREE) {

            // TODO: Restrict panning and zooming to region surrounding elements in the workspace.

        } else if (cameraComponent.mode == Camera.Mode.FOCUS) {

            Entity focusEntity = cameraComponent.focus;

            if (focusEntity == null) {

//                // <MOVE_TO_WORLD_EVENT_HANDLER>
//                // Hide Portables' Ports.
////                pathAndPortEntities.setVisibility(Visible.INVISIBLE);
//                portEntities.setVisibility(Visible.INVISIBLE);
//                Group<ModelBuilder> pathAndPortModels = pathAndPortEntities.getModels();
//                for (int i = 0; i < pathAndPortModels.size(); i++) {
//                    pathAndPortModels.get(i).meshIndex = 0;
//                }
//
//
//                // Update distance between Hosts and Extensions
//                world.getSystem(PortableLayoutSystem.class).setPortableSeparation(World.HOST_TO_EXTENSION_SHORT_DISTANCE);
//                // </MOVE_TO_WORLD_EVENT_HANDLER>

                // <REFACTOR>
                cameraComponent.boundary = null;
                // </REFACTOR>

                // Update scale and position
                Rectangle boundingBox = Geometry.getBoundingBox(hostAndExtensionEntities.getModels().getPrimitives().getBoundaryVertices());
                setScale(camera, boundingBox);
                setPosition(camera, boundingBox.getPosition());


            } else if (focusEntity.hasComponent(Host.class)) {

                // <REFACTOR>
                // Get all Ports in all Path connected to any of the Host's Ports
                Group<Entity> hostPathPorts = new Group<>();
                Group<Entity> hostPorts = Portable.getPorts(focusEntity);
                for (int i = 0; i < hostPorts.size(); i++) {
                    Entity port = hostPorts.get(i);

                    if (!hostPathPorts.contains(port)) {
                        hostPathPorts.add(port);
                    }

                    Group<Entity> portPaths = Port.getPaths(port);
                    for (int j = 0; j < portPaths.size(); j++) {
                        Entity path = portPaths.get(j);
                        if (!hostPathPorts.contains(Path.getSourcePort(path))) {
                            hostPathPorts.add(Path.getSourcePort(path));
                        }
                        if (Path.getTargetPort(path) != null // HACK: for case when singleton Path has no Target Port
                                && !hostPathPorts.contains(Path.getTargetPort(path))) {
                            hostPathPorts.add(Path.getTargetPort(path));
                        }
                    }


                    // <REFACTOR>
                    // Update Path ModelBuilder
                    Group<Model> portPathModels = portPaths.getModels();
                    for (int j = 0; j < portPathModels.size(); j++) {
                        portPathModels.get(j).meshIndex = 1;
                    }
                    // </REFACTOR>
                }

                // </REFACTOR>

                Group<Entity> hostPathPortPrimitives = hostPathPorts.getModels().getPrimitives();
                Group<Entity> hostExtensionShapes = Portable.getExtensions(focusEntity).getModels().getPrimitives();
                hostPathPortPrimitives.addAll(hostExtensionShapes);

                Rectangle boundingBox = Geometry.getBoundingBox(hostPathPortPrimitives.getBoundaryVertices());

                // <REFACTOR>
                cameraComponent.boundary = hostPathPortPrimitives.getBoundaryVertices();
                // </REFACTOR>

                // Update scale and position
                setScale(camera, boundingBox);
                setPosition(camera, boundingBox.getPosition());

            } else if (focusEntity.hasComponent(Extension.class)) {

                Log.v("SetFocus", "setFocus(Extension)");

                // <REFACTOR>
                // Get Ports along every Path connected to the Ports on the selected Host
                Group<Entity> extensionPathPorts = new Group<>();
                Group<Entity> extensionPorts = Portable.getPorts(focusEntity);
                for (int i = 0; i < extensionPorts.size(); i++) {
                    Entity port = extensionPorts.get(i);

                    if (!extensionPathPorts.contains(port)) {
                        extensionPathPorts.add(port);
                    }

                    Group<Entity> portPaths = Port.getPaths(port);
                    for (int j = 0; j < portPaths.size(); j++) {
                        Entity path = portPaths.get(j);
                        if (!extensionPathPorts.contains(Path.getSourcePort(path))) {
                            extensionPathPorts.add(Path.getSourcePort(path));
                        }
                        if (!extensionPathPorts.contains(Path.getTargetPort(path))) {
                            extensionPathPorts.add(Path.getTargetPort(path));
                        }
                    }
                }
                // </REFACTOR>

                // Increase distance between Host and Extension
                Entity host = Portable.getHosts(focusEntity).get(0);
//                world.getSystem(PortableLayoutSystem.class).setExtensionDistance(host, World.HOST_TO_EXTENSION_LONG_DISTANCE);

                Group<Entity> extensionPathPortShapes = extensionPathPorts.getModels().getPrimitives();
                extensionPathPortShapes.addAll(Model.getPrimitives(focusEntity)); // HACK: Add Extension primitives
                Rectangle boundingBox = Geometry.getBoundingBox(extensionPathPortShapes.getBoundaryVertices());

                // <REFACTOR>
                cameraComponent.boundary = extensionPathPortShapes.getBoundaryVertices();
                // </REFACTOR>

                // Update scale and position
                setScale(camera, boundingBox);
                setPosition(camera, boundingBox.getPosition());
            }
        }
    }

    public void setFocus(Entity camera, Entity entity) {

        Camera cameraComponent = camera.getComponent(Camera.class);

        cameraComponent.focus = entity;
        cameraComponent.mode = Camera.Mode.FOCUS;

    }

    private void setPosition(Entity camera, Transform position) {

        Physics physicsComponent = camera.getComponent(Physics.class);

        physicsComponent.targetTransform.set(
                -position.x * physicsComponent.targetTransform.scale,
                -position.y * physicsComponent.targetTransform.scale
        );
    }

    /**
     * Adjusts the {@code CameraEntity} to fit the bounding box {@code boundingBox}.
     *
     * @param boundingBox The bounding box to fit into the display area.
     */
    private void setScale(Entity camera, Rectangle boundingBox) {

        // <REFACTOR>
        // TODO: Move into Platform helper.
        int displayWidth = DeviceDimensionsHelper.getDisplayWidth(Application.getContext());
        int displayHeight = DeviceDimensionsHelper.getDisplayHeight(Application.getContext());
        double horizontalScale = displayWidth / boundingBox.getWidth();
        double verticalScale = displayHeight / boundingBox.getHeight();
        // </REFACTOR>

        // <REFACTOR>
        camera.getComponent(Camera.class).boundingBox = boundingBox;
        // </REFACTOR>

//        if (horizontalScale <= Camera.MAXIMUM_SCALE || horizontalScale <= Camera.MAXIMUM_SCALE) {
        if (horizontalScale < verticalScale) {
            camera.getComponent(Physics.class).targetTransform.scale = horizontalScale;
        } else if (horizontalScale > horizontalScale) {
            camera.getComponent(Physics.class).targetTransform.scale = verticalScale;
        }
//        } else {
//            setScale(camera, Camera.MAXIMUM_SCALE, Camera.DEFAULT_SCALE_PERIOD);
//        }
    }
}
