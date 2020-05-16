# How to run in local

docker run --name weave-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password -d mysql:5.7

--weave.batch.appConfig=./app-config.json
--weave.batch.jobSpec=fe-pilot