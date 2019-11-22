package akka.streams;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.http.scaladsl.model.Uri;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.RoundRobinPool;
import akka.stream.javadsl.Flow;

public class RouteActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .build();
    }
    public Flow<HttpRequest,HttpResponse, NotUsed> createRoute(){
        return Flow.of(HttpResponse.class)
                .mapConcat(this::parseQuery)
    }
    public HttpResponse parseQuery(HttpRequest req){
        Query qry = req.getUri().query();
        String url = qry.get("testUrl");
        String
    }
}
