package applications.timesync;

import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** @author Hoang Tung Dinh */
public final class StartTimeAnnouncer extends AbstractNodeMain {
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("StartTimeAnnouncer");
  }

  @Override
  public void onStart(final ConnectedNode connectedNode) {
    final String timeSyncTopic = connectedNode.getParameterTree().getString("/time_sync_topic");

    final Time startTime = connectedNode.getCurrentTime();
    final Publisher<std_msgs.Time> startTimePublisher =
        connectedNode.newPublisher(timeSyncTopic, std_msgs.Time._TYPE);

    final std_msgs.Time timeMsgs = startTimePublisher.newMessage();
    timeMsgs.setData(startTime);

    Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(
            new Runnable() {
              @Override
              public void run() {
                startTimePublisher.publish(timeMsgs);
              }
            },
            0,
            20,
            TimeUnit.MILLISECONDS);
  }
}
