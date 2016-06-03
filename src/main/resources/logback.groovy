appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%-4relative [%thread] %-5level %logger{35} - %msg %n"
    }
}

def USER_HOME = System.getProperty("user.home")
def bySecond = timestamp("yyyy-MM-dd-HH-mm-ss")
def DIR = USER_HOME + "/logs/" + bySecond

appender("FILE", FileAppender) {
    file = "${DIR}/beboprosjava.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%-4relative [%thread] %-5level %logger{35} - %msg %n"
    }
}

root(INFO, ["STDOUT", "FILE"])
logger("control", INFO, ["FILE"])
