import akka.actor.typed.ActorSystem;

public class MyApp {
    public static void main(String[] args) {
        ActorSystem<Guardianactor.Command> actorSystem = ActorSystem.create(Guardianactor.create(), "Race_Simaulation");
        actorSystem.tell(new Guardianactor.InstructionCommand("start"));
    }
}
