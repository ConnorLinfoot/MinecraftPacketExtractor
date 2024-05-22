package dev.linfoot.mcpacketextractor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum PacketType {
    COMMON("CommonPacketTypes"),
    COOKIE("CookiePacketTypes"),
    PING("PingPacketTypes"),
    HANDSHAKE("HandshakePacketTypes"),
    STATUS("StatusPacketTypes"),
    LOGIN("LoginPacketTypes"),
    CONFIGURATION("ConfigurationPacketTypes"),
    GAME("GamePacketTypes"),
    ;

    private static final Pattern PACKETS = Pattern.compile("public\\s+static\\s+final\\s+PacketType<(?<packetType>.*?)>\\s+(?<name>.*?)\\s*=\\s*create(Server|Client)bound\\s*\\(\\s*\"(?<parameter>.*?)\"\\s*\\);", Pattern.MULTILINE | Pattern.DOTALL);
    public static final PacketType[] VALUES = values();

    private final String fileName;
    private Map<String, Packet> byField;

    PacketType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    private Map<String, Packet> getByField() {
        if (byField == null) {
            byField = new HashMap<>();
            String file = getFileContents();
            Matcher matcher = PACKETS.matcher(file);
            while (matcher.find()) {
                String packetName = matcher.group(2);
                String packetType = matcher.group(1);
                byField.put(packetName, new Packet(packetType, packetName, matcher.group(4)));
            }
        }
        return byField;
    }

    private String getFileContents() {
        String contents = Main.findFile(fileName + ".java");
        if(contents==null){
            throw new IllegalStateException("File not found: " + fileName);
        }
        return contents;
    }

    public String getPacketName(String field) {
        Packet packet = getByField().get(field);
        if (packet == null) {
            return null;
        }
        return packet.getIdentifier();
    }
}
