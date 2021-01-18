package test.influxdb;

import com.influxdb.client.BucketsApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DropSeries {
    private static Logger LOG = LoggerFactory.getLogger("LOG");

    public static void main(String[] args) {
        // You can generate a Token from the "Tokens Tab" in the UI
        String token = "kuxAWjG7z-e4e05m5LhnLUbDiWc6y2l3eRsFAA1H7g2rEiy3yi14-SJ9vgCIPSIwsP-corLvnh98O8gbLFfZng==";
        String bucket = "fsa";
        String org = "FSA";

        InfluxDBClient client = InfluxDBClientFactory.create("http://172.19.0.2:8086", token.toCharArray());
        BucketsApi service = client.getBucketsApi();
        Bucket fsa = service.findBucketByName("fsa");
        LOG.info("Bucket: {}", fsa );
//        service.deleteBucket("fsa");
//        service.createBucket("fsa");

    }
}
