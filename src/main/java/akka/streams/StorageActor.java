package akka.streams;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;
import java.util.Map;

public class StorageActor extends AbstractActor {
    Map<Request,Long> storage = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(SaveResultMessage.class, t->{
                    storage.put(t.getRequest(),t.getResult());
                })
                .match(GetResultMessage.class, msg->{
                    
                })
                .match(GetResultMessage.class, r-> sender())
                .build();
    }
}
