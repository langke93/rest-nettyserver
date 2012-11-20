#!/bin/sh
#if [ "`whoami`" != "root" ] ; then
#        echo Must start use root
#        exit 1
#fi

DIRNAME=/usr/bin/dirname
BASE_PATH=`$DIRNAME $0`
cd $BASE_PATH
echo `pwd`

#export JAVA_HOME=/usr/local/jdk1.6.0_26
#export PATH=/usr/local/jdk1.6.0_26/bin:$PATH

for i in ./*.jar; do
 CLASSPATH=$i:$CLASSPATH;
done

for j in ../lib/*.jar; do
 CLASSPATH=$j:$CLASSPATH;
done

SEARCH_VER=0
#DEFAULT_OPTS="-server -Xms300M -Xmx100M -Xss128k"
DEFAULT_OPTS="$DEFAULT_OPTS -Dcom.sun.management.jmxremote.port=89${SEARCH_VER}6" 
DEFAULT_OPTS="$DEFAULT_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
DEFAULT_OPTS="$DEFAULT_OPTS -Dcom.sun.management.jmxremote.ssl=false"
DEBUG_INFO=" -Xdebug -Xrunjdwp:transport=dt_socket,address=1527${SEARCH_VER},server=y,suspend=n "
DEBUG=""
case $1 in
        "debug") DEBUG=${DEBUG_INFO};;
        esac;
shift;
PNAME=org.langke.core.server.RestNettyServer
if test $(pgrep -f ${PNAME}|wc -l) -ne 0;then
  echo "closing...... $PNAME"
  pkill -f $PNAME
  sleep 1
fi


# process
CMD="java -cp $CLASSPATH $DEFAULT_OPTS $DEBUG ${PNAME} > /dev/null 2>&1 &"
eval $CMD
echo "start ~~ $CMD"
echo "as pid:`pgrep -f ${PNAME}`"
