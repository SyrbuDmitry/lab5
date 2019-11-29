package akka.streams;

public class GetResultMessage {
    Request request;
    Long result;
    GetResultMessage(Request r){

    }

    public Request getRequest() {
        return request;
    }
}
