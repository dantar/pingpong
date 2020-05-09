#!/bin/bash
#set -v # do not expand variables
set -x # output
set -e # stop on error
set -u # stop if you use an uninitialized variable

TODAY=`date +%Y-%m-%d-%H-%M-%S`
echo $TODAY

HACKGIT=~/hack/git

export JAVA_HOME=/usr/lib/jvm/java-8-oracle

REMOTE="ssh ada-ubuntu-devel "

cd $HACKGIT/pingpong/pingpong-rest
mvn clean install

APPNAME=dantar-hoard

$REMOTE sudo /etc/init.d/$APPNAME stop
$REMOTE cp boot-services/$APPNAME.jar backup/boot-services/$APPNAME-$TODAY.jar
scp $HACKGIT/pingpong/pingpong-rest/target/ping-pong-rest-0.0.1-SNAPSHOT.jar ada-ubuntu-devel:boot-services/$APPNAME.jar
$REMOTE sudo /etc/init.d/$APPNAME start

cd $HACKGIT/pingpong/pingpong-ng
ng build --base-href=./ --prod

rsync --delete -varzh $HACKGIT/pingpong/pingpong-ng/dist/pingpong-ng/* ada-ubuntu-devel:/var/www/html/hoard/

