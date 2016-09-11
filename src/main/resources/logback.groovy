def USER_HOME = System.getProperty("user.home")
def bySecond = timestamp("yyyy-MM-dd-HH-mm-ss")
def DIR = USER_HOME + "/logs/" + bySecond

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%-4relative [%thread] %-5level %logger{35} - %msg %n"
    }
}

appender("FILE", FileAppender) {
    file = "${DIR}/beswarm.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%-4relative [%thread] %-5level %logger{35} - %msg %n"
    }
}

appender("FILE_DRONE_BODY_VELOCITY", FileAppender) {
    file = "${DIR}/drone_body_velocity.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%msg %n"
    }
}

appender("FILE_RECEIVED_MESSAGES", FileAppender) {
    file = "${DIR}/received_messages.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%msg %n"
    }
}

appender("FILE_SIFT", GSiftingAppender) {
    discriminator(MDCBasedDiscriminator) {
        key = "loggerName"
        defaultValue = "unknown"
    }
    sift {
        appender("FILE-${loggerName}", FileAppender) {
            file = "${DIR}/${loggerName}.log"
            encoder(PatternLayoutEncoder) {
                pattern = "%msg %n"
            }
        }
    }
}

root(DEBUG, ["STDOUT", "FILE"])
logger("services.parrot.ParrotVelocity4dService.vel", TRACE, ["FILE_DRONE_BODY_VELOCITY"], false)
logger("services.ros_subscribers.MessagesSubscriberService", TRACE, ["FILE_RECEIVED_MESSAGES"], false)
logger("control.VelocityController4dLogger", TRACE, ["FILE_SIFT"], false)

// ----------------- for operational tests -----------------
appender("FILE_ARMARKER_VELOCITY", FileAppender) {
    file = "${DIR}/armarkervelocity.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%msg %n"
    }
}
logger("operationaltesting.StateEstimatorOT.velocity.armarker", TRACE,
        ["FILE_ARMARKER_VELOCITY"], false)

appender("FILE_ODOM_VELOCITY", FileAppender) {
    file = "${DIR}/odomvelocity.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%msg %n"
    }
}
logger("operationaltesting.StateEstimatorOT.velocity.odom", TRACE,
        ["FILE_ODOM_VELOCITY"], false)

// ----------------- for simulation -----------------
appender("FILE_GROUND_TRUTH_POSE", FileAppender) {
    file = "${DIR}/groundtruthpose.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%msg %n"
    }
}
logger("control.localization.FakeStateEstimatorDecorator", TRACE, ["FILE_GROUND_TRUTH_POSE"], false)