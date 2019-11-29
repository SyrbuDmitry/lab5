package akka.streams;

import akka.actor.AbstractActor;

import java.util.HashMap;
import java.util.Map;

public class StorageActor extends AbstractActor {
    Map<Request,Long> storage = new HashMap<>();

}
