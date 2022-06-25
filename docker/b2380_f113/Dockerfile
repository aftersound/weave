FROM openjdk:8-jre

ENV WEAVE_HOME=/opt/weave

COPY weave $WEAVE_HOME/

COPY weave-lib $WEAVE_HOME/lib/

RUN ls -lar $WEAVE_HOME/*
RUN chmod 755 $WEAVE_HOME/bin/start-service-app.sh

COPY weave/bin/service-app-entrypoint.sh /service-app-entrypoint.sh
RUN chmod 755 /service-app-entrypoint.sh

ENTRYPOINT ["/service-app-entrypoint.sh"]