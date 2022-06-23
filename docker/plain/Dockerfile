FROM openjdk:8-jre

ENV WEAVE_HOME=/opt/weave

COPY weave $WEAVE_HOME/
COPY service-lib $WEAVE_HOME/service/lib/

RUN ls -lar $WEAVE_HOME/*
RUN chmod 755 $WEAVE_HOME/bin/start-service-app.sh

COPY weave/service-app-entrypoint.sh /service-app-entrypoint.sh
RUN chmod 755 /service-app-entrypoint.sh

ENTRYPOINT ["/service-app-entrypoint.sh"]