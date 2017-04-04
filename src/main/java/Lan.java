import java.util.List;

public class Lan {

    public final String name;
    public final List<String> clients;
    public final String speed;
    public final String delay;

    public Lan(String name, List<String> clients, String speed, String delay) {
        this.name = name;
        this.clients = clients;
        this.speed = speed;
        this.delay = delay;
    }

}
