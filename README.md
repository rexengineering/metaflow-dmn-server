## Getting Started

This is a DMN server that evaluates DMN DecisionTables and FEEL expressions.

The default port is 8001, but can be configured with:

```
export DMN_HOST=<dns name or ip>
export DMN_PORT=<port to listen on>
```

## Entry points

There are three entry points currently defined:

/version
```
curl http://localhost:8001/version | jq
{
  "response": {
    "port": 8001,
    "host": "0.0.0.0",
    "title": "DMN Evaluator Engine",
    "version": "0.0"
  }
}
```

/dmn/decision-tree
/dmn/dt





## Building from source

In the /src folder there is a thin client DmnServer.java which instantiates the server. It can be run from the command line via

```
$ cd src
$ export CLASSPATH="../src:../lib/*"
$ java DmnServer.java
```

## As a Docker container

```
$ docker build -t dmnserver .
$ kubectl apply -f deploy.yaml
$ kubectl get po --all-namespace
NAMESPACE      NAME                                     READY   STATUS    RESTARTS   AGE
default        dmnserver-6d566d7695-wkdvk               1/1     Running   0          4s
istio-system   istio-ingressgateway-559f565fcd-ncn7b    1/1     Running   1          2d18h
istio-system   istiod-545bb98448-66tlj                  1/1     Running   1          2d18h
kube-system    coredns-f9fd979d6-667kk                  1/1     Running   2          23d
kube-system    coredns-f9fd979d6-w5dt6                  1/1     Running   2          23d
kube-system    etcd-docker-desktop                      1/1     Running   2          23d
kube-system    kube-apiserver-docker-desktop            1/1     Running   2          23d
kube-system    kube-controller-manager-docker-desktop   1/1     Running   2          23d
kube-system    kube-proxy-766xp                         1/1     Running   2          23d
kube-system    kube-scheduler-docker-desktop            1/1     Running   16         23d
kube-system    storage-provisioner                      1/1     Running   13         23d
kube-system    vpnkit-controller                        1/1     Running   2          23d
$
```
## Testing the docker container

Outside of writing ac

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

## Dependency Management

The `JAVA DEPENDENCIES` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-pack/blob/master/release-notes/v0.9.0.md#work-with-jar-files-directly).
