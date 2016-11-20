Local mysql docker:
```
docker run --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=mavet -e MYSQL_DATABASE=apollo -e MYSQL_USER=apollo -e MYSQL_PASSWORD=tahat -v /Users/roiravhon/github/misc/apollo-mysql:/var/lib/mysql -d mysql:latest
```