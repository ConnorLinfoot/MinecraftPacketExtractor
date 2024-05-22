package dev.linfoot.mcpacketextractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static String DECOMPILE_PATH;
    private static String OUTPUT_PATH;
    private static Format FORMAT = Format.DECIMAL;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final Pattern PACKETS_PATTERN = Pattern.compile(".(withBundle|add)Packet\\((.*?)PacketTypes\\.(.*?),", Pattern.MULTILINE);

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: <decompile directory> <output directory> [format]");
            return;
        }

        DECOMPILE_PATH = args[0];
        OUTPUT_PATH = args[1];
        if (args.length > 2) {
            FORMAT = Format.valueOf(args[2].toUpperCase());
        }

        System.out.println("Starting...");

        // Handshake
        dumpPackets(Direction.SERVERBOUND, "HandshakeProtocols", "handshake/serverbound.json");

        // Status
        dumpPackets(Direction.SERVERBOUND, "StatusProtocols", "status/serverbound.json");
        dumpPackets(Direction.CLIENTBOUND, "StatusProtocols", "status/clientbound.json");

        // Login
        dumpPackets(Direction.SERVERBOUND, "LoginProtocols", "login/serverbound.json");
        dumpPackets(Direction.CLIENTBOUND, "LoginProtocols", "login/clientbound.json");

        // Configuration
        dumpPackets(Direction.SERVERBOUND, "ConfigurationProtocols", "configuration/serverbound.json");
        dumpPackets(Direction.CLIENTBOUND, "ConfigurationProtocols", "configuration/clientbound.json");

        // Game
        dumpPackets(Direction.SERVERBOUND, "GameProtocols", "play/serverbound.json");
        dumpPackets(Direction.CLIENTBOUND, "GameProtocols", "play/clientbound.json");
    }

    public static String findFile(String fileName) {
        return findFile(DECOMPILE_PATH, fileName);
    }

    private static String findFile(String directory, String fileName) {
        File file = new File(directory);
        if (!file.isDirectory()) {
            System.err.println("Not a directory: " + directory);
            return null;
        }

        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                String content = findFile(f.getAbsolutePath(), fileName);
                if (content != null) {
                    return content;
                }
                continue;
            }

            if (!f.getName().equals(fileName)) {
                continue;
            }

            try {
                List<String> content = Files.readAllLines(f.toPath());
                return String.join("\n", content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    private static void dumpPackets(Direction direction, String inputFile, String outputFile) {
        outputFile = OUTPUT_PATH+ "/" + outputFile;
        System.out.println("Dumping packets for " + direction + " from " + inputFile + " to " + outputFile);

        String inputFileContents = findFile(inputFile + ".java");
        if (inputFileContents == null) {
            System.err.println("File not found: " + inputFile);
            return;
        }

        JsonObject output = new JsonObject();

        Matcher matcher = PACKETS_PATTERN.matcher(inputFileContents);
        int index = -1;
        while (matcher.find()) {
            String packetName = matcher.group(3);
            if (!inputFile.startsWith("Handshake") && !packetName.startsWith(direction.name())) {
                continue;
            }

            index++;
            String identifier = findPacketIdentifier(packetName);
            if (identifier == null) {
                System.err.println("Packet identifier not found for: " + packetName);
                continue;
            }

            if (FORMAT == Format.DECIMAL) {
                output.addProperty(identifier, index);
            } else {
                String hex = Integer.toHexString(index);
                if (hex.length() % 2 != 0) {
                    hex = "0" + hex;
                }
                output.addProperty(identifier, "0x" + hex);
            }
        }

        // Make sure directories exist for the file
        File file = new File(outputFile);
        file.getParentFile().mkdirs();

        try (Writer writer = new FileWriter(outputFile)) {
            writer.write(GSON.toJson(output));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        System.out.println("  Dumped " + (index + 1) + " packets");
    }

    private static String findPacketIdentifier(String fieldName) {
        String packetName = null;
        for (PacketType type : PacketType.VALUES) {
            packetName = type.getPacketName(fieldName);
            if (packetName != null) {
                break;
            }
        }
        return packetName;
    }
}