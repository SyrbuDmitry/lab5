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
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import javafx.util.Pair;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;
import org.asynchttpclient.AsyncHttpClient;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

public class RouteActor {
    private ActorMaterializer materializer;
    public Flow<HttpRequest,HttpResponse, NotUsed> createRoute(){
        return Flow.of(HttpRequest.class)
                .map(this::parseQuery)
                .mapAsync(5,this::sendRequest)
                .map(this::convertIntoResponse);
    }

    public Request parseQuery(HttpRequest req){
        Query qry = req.getUri().query();
        Optional<String> url = qry.get("testUrl");
        Optional<String> count = qry.get("count");
        return new Request(url.get(),count.get());
    }

    public CompletionStage<Long> sendRequest(Request r){
        Sink<Request,CompletionStage<Long>> testSink =
                Flow.<Request>create()
                .mapConcat(t-> Collections.nCopies(t.getCount(),t))
                .mapAsync(5,this::getTime)
                .toMat(Sink.fold(0L, (agg, next) -> agg + next),  Keep.right());
        return Source.from(Collections.singletonList(r))
                .toMat(testSink, Keep.right()).run(materializer);
    }


    private CompletionStage<Long> getTime(Request r){
        Instant startTime = Instant.now();
        AsyncHttpClient client = Dsl.asyncHttpClient();
        CompletionStage<Long> whenResponse = client.prepareGet(r.getUrl()).execute()
                .toCompletableFuture()
                .thenCompose(w -> CompletableFuture.completedFuture(
                        Duration.between(startTime,Instant.now()).getSeconds()
                ));
        return  whenResponse;
    }
    public HttpResponse convertIntoResponse(Long r){
        HttpResponse res = HttpResponse.create()
    }
}
