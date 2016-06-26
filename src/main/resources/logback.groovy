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

appender("FILE_DRONE_POSE", FileAppender) {
    file = "${DIR}/dronepose.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%msg %n"
    }
}

appender("FILE_DRONE_VELOCITY", FileAppender) {
    file = "${DIR}/dronevelocity.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%msg %n"
    }
}

root(DEBUG, ["STDOUT", "FILE"])
logger("control.PidController4d.poselogger", TRACE, ["FILE_DRONE_POSE"], false)
logger("control.PidController4d.velocitylogger", TRACE, ["FILE_DRONE_VELOCITY"], false)
