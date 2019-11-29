package akka.streams;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.model.*;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.AsyncHttpClient;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.Query;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class RouteActor {
    private ActorMaterializer materializer;
    private ActorRef cashActor;
    private int prlsm;
    RouteActor(ActorMaterializer materializer,ActorSystem system){
        this.cashActor = system.actorOf(Props.create(StorageActor.class));
        this.materializer = materializer;
        this.prlsm = 5;
    }
    public Flow<HttpRequest,HttpResponse, NotUsed> createRoute(){
        return Flow.of(HttpRequest.class)
                .map(this::parseQuery)
                .mapAsync(prlsm,this::checkRequest)
                .map(this::convertIntoResponse);
    }

    private Request parseQuery(HttpRequest req){
        Query qry = req.getUri().query();
        Optional<String> url = qry.get("testUrl");
        Optional<String> count = qry.get("count");
        return new Request(url.get(),count.get());
    }

    private CompletionStage<Result> checkRequest(Request r){
        return Patterns.ask(cashActor,r, Duration.ofSeconds(5))
                .thenApply(res->(Result)res)
                .thenCompose(m ->m.getResult()!=null ? CompletableFuture.completedFuture(m) : sendRequest(m.getRequest()));
    }

    private CompletionStage<Result> sendRequest(Request r){
        Sink<Request,CompletionStage<Long>> testSink =
                Flow.<Request>create()
                .mapConcat(t-> Collections.nCopies(t.getCount(),t))
                .mapAsync(prlsm,this::getTime)
                .toMat(Sink.fold(0L, (agg, next) -> agg + next),  Keep.right());
        return Source.from(Collections.singletonList(r))
                .toMat(testSink, Keep.right())
                .run(materializer)
                .thenApply(res->new Result(r,res/r.getCount()));
    }


    private CompletionStage<Long> getTime(Request r){
        long start = System.currentTimeMillis();
        AsyncHttpClient client = Dsl.asyncHttpClient();
        return client.prepareGet(r.getUrl()).execute()
                .toCompletableFuture()
                .thenCompose(w -> CompletableFuture.completedFuture(
                        System.currentTimeMillis()-start
                ));
    }
    private HttpResponse convertIntoResponse(Result r){
        cashActor.tell(r,ActorRef.noSender());
        return HttpResponse
                .create()
                .withEntity(ContentTypes.APPLICATION_JSON, ByteString.fromString(r.getRequest().getUrl()+" "+r.getRequest().getCount()+
                        " "+r.getResult()+"\n"));
    }
}
