docker container run -it -p 8080:8080 --env WEAVE_PROFILE=management-plane --env WEAVE_NAMESPACE=demo --env WEAVE_APPLICATION=mp0 aftersound/weave:bundle
docker container run -it -p 8090:8080 --env WEAVE_PROFILE=standard --env WEAVE_NAMESPACE=demo --env WEAVE_APPLICATION=basics0 --env WEAVE_SERVICE_MANAGER=http://192.168.1.80:8080 --env WEAVE_SERVICE_AGENT_ENABLED=true aftersound/weave:bundle
docker container run -it -p 8081:8080 --env WEAVE_PROFILE=standard --env WEAVE_NAMESPACE=demo --env WEAVE_APPLICATION=jm0 --env WEAVE_SERVICE_MANAGER=http://192.168.1.80:8080 --env WEAVE_SERVICE_AGENT_ENABLED=true aftersound/weave:bundle
docker container run -it -p 8100:8080 --env WEAVE_PROFILE=standard --env WEAVE_NAMESPACE=demo --env WEAVE_APPLICATION=jr0 --env WEAVE_SERVICE_MANAGER=http://192.168.1.80:8080 --env WEAVE_SERVICE_AGENT_ENABLED=true --env WEAVE_JOB_MANAGER=http://192.168.1.80:8081 aftersound/weave:bundle
docker container run -it -p 8101:8080 --env WEAVE_PROFILE=standard --env WEAVE_NAMESPACE=demo --env WEAVE_APPLICATION=jr0 --env WEAVE_SERVICE_MANAGER=http://192.168.1.80:8080 --env WEAVE_SERVICE_AGENT_ENABLED=true --env WEAVE_JOB_MANAGER=http://192.168.1.80:8081 aftersound/weave:bundle
docker container run -it -p 8200:8080 --env WEAVE_PROFILE=standard --env WEAVE_NAMESPACE=demo --env WEAVE_APPLICATION=kafka0 --env WEAVE_SERVICE_MANAGER=http://192.168.1.80:8080 --env WEAVE_SERVICE_AGENT_ENABLED=true aftersound/weave:bundle