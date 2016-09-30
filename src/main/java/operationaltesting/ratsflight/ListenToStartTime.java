package operationaltesting.ratsflight;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.rossubscribers.MessagesSubscriberService;
import std_msgs.Time;
import time.RosTime;

import java.util.concurrent.TimeUnit;

/** @author Hoang Tung Dinh */
public final class ListenToStartTime extends AbstractNodeMain {

  private static final Logger logger = LoggerFactory.getLogger(ListenToStartTime.class);

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("ListenToStartTime");
  }

  @Override
  public void onStart(ConnectedNode connectedNode) {
    final MessagesSubscriberService<Time> timeListener =
        MessagesSubscriberService.create(
            connectedNode.<Time>newSubscriber("/start_time", Time._TYPE),
            RosTime.create(connectedNode));

    while (true) {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      logger.info(timeListener.getMostRecentMessage().toString());
    }
  }
}
