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
import javafx.util.Pair;

import java.util.Optional;

public class RouteActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .build();
    }
    public Flow<HttpRequest,HttpResponse, NotUsed> createRoute(){
        return Flow.of(HttpRequest.class)
                .mapConcat(this::parseQuery)
                .mapAsync(this::sendRequest)

    }

    public Pair<String, Integer> parseQuery(HttpRequest req){
        Query qry = req.getUri().query();
        Optional<String> url = qry.get("testUrl");
        Optional<String> count = qry.get("count");
        return new Pair<>(url.get(),Integer.parseInt(count.get()));
    }

    public HttpResponse sendRequest(Pair<String, Integer> request){

    }
}
