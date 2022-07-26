#docker container run -it -p 8080:8080 --env APPLICATION=service.demo aftersound/weave:bundle --name service.demo
port=$1
docker container run -it -p $port:8080 --env APPLICATION=service.demo aftersound/weave:bundle --name service.demo