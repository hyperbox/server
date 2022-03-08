package lu.kamax.apps.hbox;

public interface Machine {

    String getId();

    String getName();

    Integer powerOn();

    Integer powerOff();

    Integer shutdown();

}
