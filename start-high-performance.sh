#!/bin/bash
# High Performance JVM startup script for customer-contact API

java \
  -Xms2g \
  -Xmx4g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+UseStringDeduplication \
  -XX:+OptimizeStringConcat \
  -XX:+UseCompressedOops \
  -XX:NewRatio=2 \
  -XX:SurvivorRatio=8 \
  -XX:MaxTenuringThreshold=1 \
  -XX:+DisableExplicitGC \
  -XX:+UseNUMA \
  -Djava.awt.headless=true \
  -Dspring.profiles.active=production \
  -jar target/customer-contact-0.0.1-SNAPSHOT.jar \
  --server.port=8082