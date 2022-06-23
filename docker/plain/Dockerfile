FROM openjdk:8-jre

ENV WEAVE_HOME=/opt/weave

COPY service-lib/core $WEAVE_HOME/service/lib/core
COPY service-lib/ext $WEAVE_HOME/service/lib/ext
COPY service-lib/extra $WEAVE_HOME/service/lib/extra
COPY weave/service/runtime-config $WEAVE_HOME/service/runtime-config
COPY weave/bin $WEAVE_HOME/bin/
RUN chmod 755 $WEAVE_HOME/bin/start-service-app.sh
RUN ls -lar $WEAVE_HOME/*

COPY weave/service-app-entrypoint.sh /service-app-entrypoint.sh
RUN chmod 755 /service-app-entrypoint.sh

ENTRYPOINT ["/service-app-entrypoint.sh"]