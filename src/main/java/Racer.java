import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Racer extends AbstractBehavior<Racer.Command> {

    int displayLength = 160;
    long start;

    public static class Command implements Serializable {
        String message;
        ActorRef<Guardianactor.Command> sender;
        int raceLength = 100;

        Map<Integer, Integer> currentPositions = new ConcurrentHashMap<Integer, Integer>();
        Map<Integer, Long> results = new ConcurrentHashMap<>();

        Command(String message, ActorRef<Guardianactor.Command> sender)
        {
            this.message = message;
            this.sender = sender;
        }

        Command(Map<Integer, Integer> currentPositions1, Map<Integer, Long> results1)
        {
            this.currentPositions = currentPositions1;
            this.results = results1;
            System.out.println("testing 16 " + "  ");
        }

        public String getMessage() {
            return message;
        }

        public ActorRef<Guardianactor.Command> getSender() {
            return sender;
        }

        public Map<Integer, Integer> getCurrentPositions() {
            return currentPositions;
        }

        public Map<Integer, Long> getResults() {
            return results;
        }
    }

        SortedSet<String> final_result_of_race_result;

    public Racer(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create()
    {
        return Behaviors.setup(Racer::new);
    }

    private  String final_result;
    public Double calculate_time_dif;

    @Override
    public Receive<Command> createReceive(){
        return newReceiveBuilder()
                .onMessage(Command.class, var -> {
                    if (var.getMessage().equals("Racer")) {
                        start = System.currentTimeMillis();
                        for (int i = 0; i < 10; i++) {
                         //   System.out.println("testing 1");
                            ActorRef<Racing_racer_stats.Command> actorRef = getContext().spawn(Racing_racer_stats.create(), "Racing_stats");

                            actorRef.tell(new Racing_racer_stats.Command("Racing_stats", i, var.raceLength, var.currentPositions, var.results, getContext().getSelf()));

                            System.out.println("testing 20");
                            var.getCurrentPositions().put(i, 0);
                           // System.out.println("testing 2");
                        }
                    }
                    boolean finished = false;
                    while (!finished) {
                        for (int i = 0; i < 50; ++i)  {System.out.println(); }
                        System.out.println(start +"  " +System.currentTimeMillis() +"  " + (System.currentTimeMillis() - start));
                        System.out.println("Race has been running for " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
                        System.out.println("    " + new String(new char[displayLength]).replace('\0', '='));
                        for (int i = 0; i < 10; i++) {
                            System.out.println(i + " : " + new String(new char[var.getCurrentPositions().get(i) * displayLength / 100]).replace('\0', '*'));
                        }
                        finished = var.getResults().size() == 10;
                    }
                    var.getResults().values().stream().sorted().forEach(it ->
                    {
                        for (Integer key : var.getResults().keySet()) {
                            if (var.getResults().get(key) == it) {
                                calculate_time_dif = ((double) it - start) / 1000;
                                final_result = "Racer " + key + " finished in " + calculate_time_dif + " seconds.";
                                final_result_of_race_result.add(final_result);

                            }
                        }
                    });
                    var.getSender().tell(new Guardianactor.ResultCommand(final_result_of_race_result));
                    return this;
                }).build();
    }
}
