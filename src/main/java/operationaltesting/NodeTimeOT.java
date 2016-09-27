package operationaltesting;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/** @author Hoang Tung Dinh */
public final class NodeTimeOT extends AbstractNodeMain {

  private static final Logger logger = LoggerFactory.getLogger(NodeTimeOT.class);

  public NodeTimeOT() {}

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("NodeTimeOT");
  }

  @Override
  public void onStart(ConnectedNode connectedNode) {
    while (true) {
      logger.info(String.valueOf(connectedNode.getCurrentTime().toSeconds()));
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        logger.info("Sleep in NoteTimeOT is interrupted.", e);
      }

      if (Thread.interrupted()) {
        return;
      }
    }
  }
}
