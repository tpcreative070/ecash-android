package vn.ecpay.fragmentcommon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationCenter {

    private static NotificationCenter instance;
    private HashMap<Integer, List<NotificationCenterListener>> registeredListener;

    public static NotificationCenter getInstance() {
        if (instance == null) {
            synchronized (NotificationCenter.class) {
                if (instance == null) {
                    instance = new NotificationCenter();
                }
            }
        }
        return instance;
    }

    public void register(NotificationCenterListener listener, int id) {
        if (registeredListener == null) {
            registeredListener = new HashMap<>();
        }
        List<NotificationCenterListener> listeners;
        if (registeredListener.containsKey(id)) {
            listeners = registeredListener.get(id);
        } else {
            listeners = new ArrayList<>();
            registeredListener.put(id, listeners);
        }
        listeners.add(listener);
    }

    public void unregister(NotificationCenterListener listener, int id) {
        if (registeredListener == null) {
            return;
        }
        if (registeredListener.containsKey(id)) {
            registeredListener.get(id).remove(listener);
        }
    }

    public void unregister(int id) {
        if (registeredListener == null) {
            return;
        }
        if (registeredListener.containsKey(id)) {
            registeredListener.remove(id);
        }
    }

    public void didNotificationReceive(int id, Object data) {
        if (registeredListener == null) {
            return;
        }
        if (registeredListener.containsKey(id)) {
            List<NotificationCenterListener> listeners = registeredListener.get(id);
            if (listeners != null && !listeners.isEmpty()) {
                for (NotificationCenterListener listener : listeners) {
                    if (listener != null) {
                        listener.didNotificationReceive(id, data);
                    }
                }
            }
        }
    }


    public interface NotificationCenterListener {
        void didNotificationReceive(int id, Object data);
    }
}
