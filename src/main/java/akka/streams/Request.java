package akka.streams;

public class Request {
    private int count;
    private String url;

    Request(String url, String count){
        this.url = url;
        this.count = Integer.parseInt(count);
    }
}
