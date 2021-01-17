//package test.influxdb;
//
//import com.influxdb.client.BucketsApi;
//import com.influxdb.client.InfluxDBClient;
//import com.influxdb.client.InfluxDBClientFactory;
//import com.influxdb.client.WriteApi;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class DropSeries {
//    private static Logger LOG = LoggerFactory.getLogger("LOG");
//
//    public static void main(String[] args) {
//        // You can generate a Token from the "Tokens Tab" in the UI
//        String token = "kRnp4_WBOcO2FtlI9MVPP8u1AcCwH7cvxdlLOlLrmXmaF4w8izBqYFMdlXcj7CR4vhWG_FtqJVbhAChruh-Osg==";
//        String bucket = "fsa";
//        String org = "FSA";
//
//        InfluxDBClient client = InfluxDBClientFactory.create("http://172.18.0.2:8086", token.toCharArray());
//        try (BucketsApi service = client.getBucketsApi()) {
//            service.deleteBucket("fsa");
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//        }
//    }
//}
