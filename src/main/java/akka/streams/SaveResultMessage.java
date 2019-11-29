package akka.streams;

public class SaveResultMessage {
    private Request request;
    private Long result;

    public Request getRequest() {
        return request;
    }

    public Long getResult() {
        return result;
    }
}
