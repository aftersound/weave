#docker container run -it -p 8080:8080 --env APPLICATION=service.demo aftersound/weave:bundle --name service.demo
namespace=$1
port=$2
docker container run -it -p $port:8080 --env APPLICATION=service.$namespace aftersound/weave:bundle --name service.$namespace