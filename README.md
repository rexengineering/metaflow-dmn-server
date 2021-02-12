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

/dmn/decision-tree or /dmn/dt

This accepts a decision tree either DMN XML format, or an abbreviated Riehl JSON format. The format is specified in the application mime type headers. 
```
$ curl -H 'Content-type:application/xml' http://localhost:8001/dt?status=gold\&sum=1000.3d -d @check-order.dmn

$ curl -H 'Content-type:application/json' http://localhost:8001/dt -d @test.json
```

/feel

This executes simple FEEL expressions. Values are listed as KVP in the URI, and the expression can be passed as the 'expr' value or via data.
```
$ curl http://localhost:8001/feel?x=2\&y=3\&expr=x%2By

$ curl http://localhost:8001/feel?x=2\&y=3 -d "x + y"

$ curl http://localhost:8001/feel?name=jim -d @test.txt
```

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

Outside of writing a client to run as a pod to interact with the server, the easiest I've found is to deploy a ubuntu 
container into the default name space. Put the following into a file e.g. ubuntu.yaml:


```
apiVersion: v1
kind: Pod
metadata:
  name: ubuntu
  labels:
    app: ubuntu
spec:
  containers:
  - image: ubuntu
    command:
      - "sleep"
      - "604800"
    imagePullPolicy: IfNotPresent
    name: ubuntu
  restartPolicy: Always
```

In order to talk to the dmnserver you will need it's IP address inside the cluster:

```
$ kubectl get svc
NAME         TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)    AGE
dmnserver    ClusterIP   10.99.4.150    <none>        8001/TCP   20h
feelserver   ClusterIP   10.104.28.68   <none>        8001/TCP   13d
kubernetes   ClusterIP   10.96.0.1      <none>        443/TCP    23d
```

Now deploy the base ubuntu image then install a couple of things:

```
$ kubectl apply -f ubuntu.yaml
$ kubectl exec --stdin --tty ubuntu -- /bin/bash
root@ubuntu:/# apt-get update
root@ubuntu:/# apt-get install curl
root@ubuntu:/# curl http://10.99.4.150:8001/version
{"response":{"port":8001,"host":"0.0.0.0","title":"DMN Evaluator Engine","version":"0.0"}}
root@ubuntu:/# exit
```
