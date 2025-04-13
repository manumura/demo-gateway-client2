### Commands

From layers

``` docker build -t manumura/demo-gateway-client2:latest . ```

``` docker run -p 8080:8080 -d --name demo-gateway-client2 manumura/demo-gateway-client2 ```

``` docker container logs demo-gateway-client2 -f ```

``` docker rm -f demo-gateway-client2 ```

# Login to docker hub account

``` docker login ```

``` echo $DOCKER_HUB_PRINCIPAL_PASSWORD | docker login --username $DOCKER_HUB_PRINCIPAL --password-stdin docker.io ```

``` echo ${{ secrets.ACR_SERVICE_PRINCIPAL_PASSWORD }} | docker login --username ${{ secrets.ACR_SERVICE_PRINCIPAL }} --password-stdin registry.azurecr.io ```

# tag image

``` docker tag demo-gateway-client2 manumura/demo-gateway-client2:latest ```

# push image

``` docker push manumura/demo-gateway-client2:latest ```

``` docker run -p 8080:8080 -d --name demo-gateway-client2 manumura/demo-gateway-client2 ```
