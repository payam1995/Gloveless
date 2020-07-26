package mainPackage;

import javax.sound.midi.*;
import java.util.List;


public class MidiHandler {

public MidiHandler() {
    MidiDevice device;
    MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

    for (MidiDevice.Info info : infos) {

        System.out.println("Info: '" + info.toString() + "'");

        try {
            device = MidiSystem.getMidiDevice(info);

            List<Transmitter> transmitters = device.getTransmitters();

            for(int j = 0; j<transmitters.size();j++) {
                transmitters.get(j).setReceiver(new MidiInputReceiver(device.getDeviceInfo().toString()));
            }  

            Transmitter trans = device.getTransmitter();
            trans.setReceiver(new MidiInputReceiver(device.getDeviceInfo().toString()));

            device.open();

        }catch (MidiUnavailableException e) {}
    }
}

public class MidiInputReceiver implements Receiver {

    public String name;

    public MidiInputReceiver(String name) {
        this.name = name;
    }

    public void send(MidiMessage msg, long timeStamp) {

        if (msg instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) msg;

            int channel = shortMessage.getChannel();
            int pitch = shortMessage.getData1();
            int vel = shortMessage.getData2();
            System.out.println("Channel: " + channel);
            System.out.println("Pitch: " + pitch);
            System.out.println("vel: " + vel);

        }

    }

    public void close() {}

}

}
