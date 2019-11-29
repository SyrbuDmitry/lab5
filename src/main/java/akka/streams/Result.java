package akka.streams;

public class Result {
    private Request request;
    private Long result;

    Result(Request req, Long res){
        this.request=req;
        this.result=res;
    }

    public Request getRequest() {
        return request;
    }

    public Long getResult() {
        System.out.println(result);
        return result;
    }
}
