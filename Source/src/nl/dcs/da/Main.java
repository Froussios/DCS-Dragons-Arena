package nl.dcs.da;

import nl.dcs.da.tss.*;
import nl.dcs.da.tss.events.*;
import nl.dcs.network.client.ClientInterface;
import nl.dcs.network.server.ServerInterface;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main implements Battlefield.Listener, Serializable {

    private final EventQueue events = new EventQueue();
    private TSS state;
    private static final Scanner scanner = new Scanner(System.in);
    private static final int port = 1099; //for test
    /**
     * @param args
     */
    public static void main(String[] args) {

        try {
            Main m = new Main();
            m.run();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public Main () throws RemoteException {
        super();
        try {
        } catch (Exception ex){

        }

    }

    public void run() {
        State start = new State();
        start.addListener(this);
        start.set(new Point(10, 10), new Player(20, 5));
        start.set(new Point(10, 11), new Player(20, 5));
        start.set(new Point(11, 11), new Dragon(50, 10));
        state = new TSS(start, 30);

        System.out.println(state);
        
        try {

            while (true) {
                System.out.print(" > ");
                String command = scanner.next().toLowerCase();
                int x, y;
                long id, time;
                Actor e = null;

                switch (command) {
                    case "exit":
                    case "stop":

                        System.out.println("Bye");
                        return;
                    case "test":
                        test();
                        break;
                    case "map":
                        System.out.println(state);
                        break;
                    case "print":
                        x = scanner.nextInt();
                        y = scanner.nextInt();
                        e = state.get(new Point(x, y));
                        if (e != null) {
                            System.out.println(state.get(new Point(x, y)).details());
                        } else {
                            System.out.println("null");
                        }
                        break;
                    case "player":
                        x = scanner.nextInt();
                        y = scanner.nextInt();
                        state.set(new Point(x, y), new Player(20, 5));
                        break;
                    case "dragon":
                        x = scanner.nextInt();
                        y = scanner.nextInt();
                        state.set(new Point(x, y), new Dragon(50, 10));
                        break;
                    case "move":
                        time = scanner.nextLong();
                        id = scanner.nextInt();
                        x = scanner.nextInt();
                        y = scanner.nextInt();
                        PlayerMove move = new PlayerMove(time, id, new Point(x, y));
                        feedEvent(move);
                        break;
                    case "attack":
                        time = scanner.nextLong();
                        id = scanner.nextInt();
                        x = scanner.nextInt();
                        y = scanner.nextInt();
                        ActorAttack attack = new ActorAttack(time, id, new Point(x, y));
                        feedEvent(attack);
                        break;
                    case "heal":
                        time = scanner.nextLong();
                        id = scanner.nextInt();
                        long idt = scanner.nextInt();
                        Heal heal = new Heal(time, id, idt);
                        feedEvent(heal);
                        break;
                    case "history":
                        for (String message : state.snapshot().getHistory()) {
                            System.out.println(" - " + message);
                        }
                        break;
                    case "ff":
                        long timespan = scanner.nextLong();
                        state.incrementTime(timespan);
                        break;
                    case "clocks":
                        for (SynchronizedState st : state.getStates()) {
                            System.out.println(st.getClock());
                        }
                        break;
                    case "events":
                        for (Event event : state.getEventQueue()) {
                            System.out.println(event);
                        }
                        break;
                    default:
                        System.out.println("Learn to type");
                        break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            scanner.close();
        }
    }

    private static void test() {
        SortedSet<Event> ss = new TreeSet<>();
        ss.add(new Heal(10, 0, 0));
        ss.add(new PlayerMove(15, 0, new Point(0, 0)));
        ss.add(new Heal(20, 0, 0));

        Collection<Event> killset = new ArrayList<>(ss.headSet(new MarkEvent(16)));
        ss.removeAll(killset);

        for (Event e : ss) {
            System.out.println(e);
        }
    }

    private void feedEvent(Event event) {
        this.feedEvent(event, false);
    }

    private void feedEvent(Event event, boolean sentByServer) {
        try {
            state.receiveEvent(event);

        } catch (OutOfSyncException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onStateChanged() {
        System.out.println(state);
    }






}
