package akka.streams;

public class Request {
    private int count;
    private String url;

    Request(String url, String count){
        System.out.println("BUILDING REQUEST");
        this.url = url;
        this.count = Integer.parseInt(count);
    }
    public int getCount(){
        return count;
    }
    public String getUrl(){
        return url;
    }
}
