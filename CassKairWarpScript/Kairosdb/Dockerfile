FROM amd64/openjdk:8u275-jre
# Tag : test/kairosdb

# Install KAIROSDB
RUN cd /opt; \
  curl -L https://github.com/kairosdb/kairosdb/releases/download/v1.2.2/kairosdb-1.2.2-1.tar.gz | \
  tar zxfp -

ADD kairosdb.properties /opt/kairosdb/conf/kairosdb.properties

# Copy scripts into container to set kairos config params
ADD config-kairos.sh /usr/bin/config-kairos.sh
RUN chmod +x /usr/bin/config-kairos.sh

# Set Kairos config vars
#ENV KAIROS_JETTY_PORT 8083
ENV CASSANDRA_HOST_LIST 10.0.0.101:9160

# Kairos API telnet and jetty ports
EXPOSE 4242 8083


# Run kairosdb in foreground on boot
CMD ["/usr/bin/config-kairos.sh"]
