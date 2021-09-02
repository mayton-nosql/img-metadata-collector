package mayton.jfr;

import jdk.jfr.Event;
import jdk.jfr.Name;

@Name("mayton.CollectEvent")
public class CollectEvent extends Event {

    public int id;

}
