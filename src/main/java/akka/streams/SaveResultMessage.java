package akka.streams;

public class SaveResultMessage {
    private Request request;
    private Long result;

    SaveResultMessage(Request req, Long res){
        this.request=req;
        this.result=res;
    }

    public Request getRequest() {
        return request;
    }

    public Long getResult() {
        return result;
    }
}
