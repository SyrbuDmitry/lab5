package akka.streams;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class StorageActor extends AbstractActor {
    Map<Request,Long> storage = new TreeMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Result.class, t->{
                    storage.put(t.getRequest(),t.getResult());

                })
                .match(Request.class, msg->{
                    sender().tell(new Result(msg,storage.get(msg)), ActorRef.noSender());
                })
                .build();
    }
}
