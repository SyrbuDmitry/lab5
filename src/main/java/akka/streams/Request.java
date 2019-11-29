package akka.streams;

public class Request implements Comparable<Request> {
    private int count;
    private String url;

    Request(String url, String count){
        this.url = url;
        this.count = Integer.parseInt(count);
    }
    public int getCount(){
        return count;
    }
    public String getUrl(){
        return url;
    }
    @Override
    public int compareTo(Request r){
        return this.url.compareTo(r.getUrl())!=0 ? this.url.compareTo(r.getUrl()): Integer.compare(this.count, r.getCount());
    }
}
