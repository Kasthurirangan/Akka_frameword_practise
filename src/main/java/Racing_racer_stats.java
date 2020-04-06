import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Racing_racer_stats extends AbstractBehavior<Racing_racer_stats.Command> {

    private final double defaultAverageSpeed = 48.2;
    public static int averageSpeedAdjustmentFactor;
    public static Random random;

    private double currentSpeed = 0;
    private double currentPosition = 0;

    public static class Command implements Serializable{
        String message;
        int counter;
        int raceLength;
        ActorRef<Racer.Command> sender;
        Map<Integer, Integer> currentPositions = new ConcurrentHashMap<Integer, Integer>();
        Map<Integer, Long> results = new ConcurrentHashMap<>();

        Command(String message, int counter, int raceLength, Map<Integer, Integer> currentPositions , Map<Integer, Long> results, ActorRef<Racer.Command> sender)
        {
            this.message = message;
            this.counter = counter;
            this.raceLength = raceLength;
            this.currentPositions = currentPositions;
            this.results = results;
            this.sender = sender;
            random = new Random();
            averageSpeedAdjustmentFactor = random.nextInt(30) - 10;
        }

        public String getMessage() {
            return message;
        }

        public int getCounter() {
            return counter;
        }

        public int getRaceLength() {
            return raceLength;
        }

        public ActorRef<Racer.Command> getSender() {
            return sender;
        }

        public Map<Integer, Integer> getCurrentPositions() {
            return currentPositions;
        }

        public Map<Integer, Long> getResults() {
            return results;
        }
    }

    public Racing_racer_stats(ActorContext<Racing_racer_stats.Command> context) {
        super(context);
    }

    public static Behavior<Command> create()
    {
        return Behaviors.setup(Racing_racer_stats::new);
    }

    Map<Integer, Integer> currentPositions_return = new ConcurrentHashMap<Integer, Integer>();
    Map<Integer, Long> results_return = new ConcurrentHashMap<>();

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder().
                onMessage(Command.class, race ->{
                    System.out.println("testing 3 ");
                    if (race.getMessage().equals("Racing_stats")) {
                        System.out.println("testing 5");
                        while (currentPosition < race.getRaceLength()) {
                            System.out.println("testing 6");
                            determineNextSpeed(race.getRaceLength());
                            currentPosition += getDistanceMovedPerSecond();
                            if (currentPosition > race.getRaceLength())
                                System.out.println("testing 8");
                                currentPosition = race.getRaceLength();
                            race.getCurrentPositions().put(race.getCounter(), (int) currentPosition);
                        }
                        race.getResults().put(race.getCounter(), System.currentTimeMillis());
                        race.getCurrentPositions().put(race.getCounter(), 0);
                        currentPositions_return = race.getCurrentPositions();
                        results_return = race.getResults();
                        System.out.println("Atlast maps vales are :");
                        System.out.println(currentPosition);
                        System.out.println(" ******************************** ");
                        System.out.println(results_return);
                        race.getSender().tell(new Racer.Command(currentPositions_return, results_return));
                    }
                    return Behaviors.same();
                }).build();
    }

    private double getDistanceMovedPerSecond() {
        System.out.println("testing 9");
        return currentSpeed * 1000 / 3600;
    }

    private void determineNextSpeed(int raceLength ) {
        System.out.println("testing 10");
            if (currentPosition < (raceLength / 4)) {
                System.out.println("testing 12");
                System.out.println("the values are " +currentSpeed +"   " + getMaxSpeed() + "  " +random.nextDouble());
                currentSpeed = currentSpeed  + (((getMaxSpeed() - currentSpeed) / 10) * random.nextDouble());
            }
            else {
                System.out.println("testing 13");
                currentSpeed = currentSpeed * (0.5 + random.nextDouble());
            }

            if (currentSpeed > getMaxSpeed())
                System.out.println("testing 14");
                currentSpeed = getMaxSpeed();

            if (currentSpeed < 5)
                currentSpeed = 5;

            if (currentPosition > (raceLength / 2) && currentSpeed < getMaxSpeed() / 2) {
                System.out.println("testing 15");
                currentSpeed = getMaxSpeed() / 2;
            }
        System.out.println("testing 7");
        }

    private double getMaxSpeed() {
        System.out.println("testing 11");
        System.out.println("checking the value : " + (1+((double)averageSpeedAdjustmentFactor / 100)));
        return defaultAverageSpeed * (1+((double)averageSpeedAdjustmentFactor / 100));
    }
}
