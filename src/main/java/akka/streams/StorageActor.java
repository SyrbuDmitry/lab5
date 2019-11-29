package akka.streams;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;
import java.util.Map;

public class StorageActor extends AbstractActor {
    Map<Request,Long> storage = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Result.class, t->{
                    storage.put(t.getRequest(),t.getResult());
                })
                .match(Request.class, msg->{
                    sender().tell(new Result(msg,storage.get(msg)), ActorRef.noSender());
                })
                .match(GetResultMessage.class, r-> sender())
                .build();
    }
}
