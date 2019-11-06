#!/bin/bash
#create by yuxuewen
#email 8586826@qq.com

homePath=`echo $HOME`
currentProjectName=`pwd | awk 'BEGIN{FS="/"} {print $(NF-1)}'`

lastDocumentName=`pwd | awk 'BEGIN{FS="/"} {print $(NF)}'`
currentProjectSourcePath=`pwd | awk 'BEGIN{FS="/docker"} {print $1}'`
cat > ./docker-compose-spring-cloud.yml <<EOF
#create by yuxuewen
#email 8586826@qq.com
version: '3'
services:
    trade-base:
        image: maven:3-jdk-8
        container_name: trade-base
        hostname: trade-base
        network_mode: bridge
        ports:
          - "7100:7100"
        external_links:
          - mysql-dev:mysql-dev
          - redis-dev:redis-dev
          - nacos-dev:nacos-dev
        volumes:
          - $currentProjectSourcePath:$currentProjectSourcePath
          - $homePath/.m2:/root/.m2
        working_dir: $currentProjectSourcePath/bs-ccr-provider-trade-basic-data
        command: ["/bin/sh", "-c", "mvn clean spring-boot:run"]
    product-combo:
        image: maven:3-jdk-8
        container_name: product-combo
        hostname: product-combo
        network_mode: bridge
        ports:
          - "7200:7200"
        external_links:
          - mysql-dev:mysql-dev
          - redis-dev:redis-dev
          - nacos-dev:nacos-dev
        volumes:
          - $currentProjectSourcePath:$currentProjectSourcePath
          - $homePath/.m2:/root/.m2
        working_dir: $currentProjectSourcePath/bs-ccr-provider-product-combo
        command: ["/bin/sh", "-c", "mvn clean spring-boot:run"]

EOF
