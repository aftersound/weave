#docker container run -it -p 8080:8080 --env APPLICATION=service.demo aftersound/weave:bundle --name service.demo
ns=$1
app=$2
port=$3
echo docker container run -it -p $port:8080 --env WEAVE_PROFILE=management-plane --env WEAVE_NAMESPACE=$ns --env WEAVE_APPLICATION=$app aftersound/weave:bundle
docker container run -it -p $port:8080 --env WEAVE_PROFILE=management-plane --env WEAVE_NAMESPACE=$ns --env WEAVE_APPLICATION=$app aftersound/weave:bundle