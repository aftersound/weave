#docker container run -it -p 8080:8080 --env APPLICATION=service.demo aftersound/weave:bundle --name service.demo
app=$1
port=$2
rcm_server=$3
echo docker container run -it -p $port:8080 --env WEAVE_APP=$app --env WEAVE_RUNTIME_CONFIG_SERVER=$rcm_server aftersound/weave:bundle --name $app
docker container run -it -p $port:8080 --env WEAVE_APP=$app --env WEAVE_RUNTIME_CONFIG_SERVER=$rcm_server aftersound/weave:bundle --name $app