#!/usr/bin/env python
import rospy
from std_msgs.msg import Time


def publish_time():
    topic_name = rospy.get_param('/time_sync_topic')
    pub = rospy.Publisher(topic_name, Time, queue_size=1)
    rospy.init_node('synchronizer', anonymous=True)
    rate = rospy.Rate(10)  # 10hz
    start_time = rospy.Time.now()
    while not rospy.is_shutdown():
        pub.publish(start_time)
        rate.sleep()


if __name__ == '__main__':
    try:
        publish_time()
    except rospy.ROSInterruptException:
        pass
