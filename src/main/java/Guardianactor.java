import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

public class Guardianactor extends AbstractBehavior<Guardianactor.Command> {

    public interface Command extends Serializable {}

    public Guardianactor(ActorContext<Command> context) {
        super(context);
    }

    public static class InstructionCommand implements Command
    {
        private String message;

        InstructionCommand(String message)
        {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public  static  class ResultCommand implements  Command
    {
        private SortedSet<String> result;

        ResultCommand(SortedSet<String> final_result)
        {
            result = final_result;
        }

        public SortedSet<String> getResult() {
            return result;
        }
    }

    public static Behavior<Command> create()
    {
        return Behaviors.setup(Guardianactor::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(InstructionCommand.class, Command -> {
                    if (Command.getMessage().equals("start"))
                    {
                          ActorRef<Racer.Command> actorRef = getContext().spawn(Racer.create(), "Racer");
                            actorRef.tell(new Racer.Command("Racer", getContext().getSelf()));
                    }
                    return  this;
                })
                .onMessage(ResultCommand.class, Command -> {
                    System.out.println("Recieved number of racers are " + Command.result.size());
                    if (Command.result.size() == 10)
                    {
                        System.out.println("Results");
                        Command.result.forEach(System.out :: println);
                    }
                    return  this;
                }).build();
    }
}
