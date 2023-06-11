#!/usr/bin/env bash
set -euvx

kubectl apply -k "github.com/aws/eks-charts/stable/aws-load-balancer-controller//crds?ref=master"

vpcID="$( \
  aws eks describe-cluster \
    --name spring-graceful-shutdown \
    --query 'cluster.resourcesVpcConfig.vpcId' \
    --output text \
)"

helm upgrade \
  -i \
  albc \
  eks/aws-load-balancer-controller \
  --version '1.4.0' \
  --namespace kube-system \
  --set clusterName=spring-graceful-shutdown \
  --set vpcId="${vpcID}" \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller \
  ;
