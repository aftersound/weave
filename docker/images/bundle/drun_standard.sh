#docker container run -it -p 8080:8080 --env APPLICATION=service.demo aftersound/weave:bundle --name service.demo
ns=$1
app=$2
port=$3
rcm_server=$4
echo docker container run --name $app -it -p $port:8080 --env WEAVE_PRF=standard --env WEAVE_APP=$app --env WEAVE_RUNTIME_CONFIG_SERVER=$rcm_server aftersound/weave:bundle
docker container run -it -p $port:8080 --env WEAVE_PRF=standard --env WEAVE_NS=$ns --env WEAVE_APP=$app --env WEAVE_RUNTIME_CONFIG_SERVER=$rcm_server aftersound/weave:bundle