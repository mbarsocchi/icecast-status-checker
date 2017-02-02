# icecast-status-checker
A status checker for Icecast stream that can automatically run **[BUTT](https://danielnoethen.de/)** (**Broadcasting Using This Tool**, a software for streaming).

Need a ***configure.properties*** file in .jar path with:

    BUTTPATH=/home/bin/butt
    SERVERURL=http://<my-server-host>:<my-server-port>
    STATUS=status-json.xsl
    STREAM=http://<stream-host>:<stream-port>/<mount-point>
Compile the code and run as:

    java -jar statuschecker.jar

## Feature ##

- Checks is stream is up by calling status-json.xsl
- If stream is down, check if server is up
- If server is up, start **BUTT**
- If server is down, do nothing


Every transition is logged.
## Tested on: ##


- Windows 7
- Windows 10
- Linux Fedora