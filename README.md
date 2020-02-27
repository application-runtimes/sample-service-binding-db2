# Binding an simple Appsody Spring boot app to DB2

This sample demonstrates binding a simple Appsody Spring boot application to DB2 in Kubernetes.

This sample makes use of [Appsody](https://appsody.dev) for cloud-native development and deployment, it is bootstrapped into existence with Appsody, using the Appsody Spring boot stack.

## Prerequisites

* Install Appsody

```
brew install appsody
```

* Install Appsody operator

```
appsody install operator
```

## Deploying the application to Kubernetes

Appsody generates a `AppsodyApplication` CR called `app-deploy.yaml` when you run `appsody build`, which can be used to deploy the application to Kubernetes. In this sample we have made `app-deploy.yaml` available for simplicity purposes.

There are few defaults in this configuration file, that we can change or add.
1. Application name can be changed by changing `metdata.name`
2. Application image name can be changed by changing `spec.applicationImage`
3. You can also add volume mounts for Kubernetes secrets acquisition by specifying the `volumes` and `volumeMounts`

More info on the parameters that can be configured in `AppsodyApplication` CR can be found [here](https://github.com/appsody/appsody-operator/blob/master/doc/user-guide.md#configuration)

### Create a secret in kubernetes with DB2 credentials.

Add the DB2 credentials to db2-secret.yaml 

```
apiVersion: v1
kind: Secret
metadata:
  name: db2-secrets
stringData:
  db2.url: <<url>>
  db2.username: <<username>>
  db2.password: <<password>>
```

and run

```
kubectl apply -f db2-secret.yaml
```

### Deploy the application

To deploy the application to Kubernetes, run the following command

```
appsody deploy -t <<mynamespace>>/sample-service-binding-db2:[tag] --push
```

The above command performs the following actions:
1. Calls `appsody build` and creates a deployment image.
2. The `-t mynamespace/myrepository[:tag]` flag tags the image.
3. The --push flag tells the Appsody CLI to push the image to Docker Hub. You can push the image to private registry by specifying `--push-url` and the registry url.
4. The `app-deploy.yaml` file is used to issue a `kubectl apply -f` command against the target Kubernetes cluster.

More info on how to deploy appsody application can be found [here](https://appsody.dev/docs/using-appsody/building-and-deploying)

### Testing the application

* To determine the IP address

a. Identify the node on which your pod has been scheduled

```
kubectl get pods -o wide
```
	
b. Use the node name to get the IP address

```
kubectl get node <<nodename>> -o wide
```

* To determine the mapped service port, run

```
kubectl get service -o wide sample-service-binding-db2
```

* To access the sample application service endpoint

```
curl http://<<ipaddress>>:<<port>>/rest/v1/books
```

## Service binding

This sample application acquires the Secrets via K8s volume mounts and this is done use [Spring Cloud Kubernetes](https://spring.io/projects/spring-cloud-kubernetes)(SCK). Check `app-deploy.yaml` for info on `volumes` and `volumeMounts`.

The application enables SCK K8s Secrets mapping, and declares a set of Secrets paths, as shown here (from `src/main/resources/bootstrap.yml`)

```
spring:
  cloud:
    kubernetes:
      secrets:
        enabled: true
        paths: /etc/secrets/db2
```

SCK then maps the contents of each file under the directory `/etc/secrets/db2` to resolve placeholders found within `application.properties`.

```
spring.datasource.url= ${db2.url}
spring.datasource.username: ${db2.username}
spring.datasource.password: ${db2.password}
```








