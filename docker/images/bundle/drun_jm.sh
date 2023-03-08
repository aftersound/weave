#docker container run -it -p 8080:8080 --env APPLICATION=service.demo aftersound/weave:bundle --name service.demo
ns=$1
app=$2
port=$3
mgr=$4
ena=$5
echo docker container run -it -p $port:8080 --env WEAVE_PROFILE=standard --env WEAVE_NAMESPACE=$ns --env WEAVE_APPLICATION=$app --env WEAVE_SERVICE_MANAGER=$mgr --env WEAVE_SERVICE_AGENT_ENABLED=$ena aftersound/weave:bundle
docker container run -it -p $port:8080 --env WEAVE_PROFILE=standard --env WEAVE_NAMESPACE=$ns --env WEAVE_APPLICATION=$app --env WEAVE_SERVICE_MANAGER=$mgr --env WEAVE_SERVICE_AGENT_ENABLED=$ena aftersound/weave:bundle