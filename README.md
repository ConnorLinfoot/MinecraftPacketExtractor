# Minecraft Packet ID Extractor

This is a simple tool to extract packet IDs from Minecraft's protocol files. To use it you must have a decompiled mapped version of the Minecraft server code.

There is no promise that this tool will work with future versions, it was created and tested with 1.20.5 and some 1.21 snapshots.

For some versions, I may post the packet IDs to [this public repository](https://github.com/ConnorLinfoot/MinecraftPacketIds) as a branch. If it's not there then you may attempt to run this tool manually.

## Usage

You can find the compiled jar in the [releases](https://github.com/ConnorLinfoot/MinecraftPacketExtractor/releases) page. 

Usage example:

```shell

java -jar minecraft-packet-id-extractor.jar /path/to/minecraft-server/src/main/java/net/minecraft/server/ /path/to/output/ [DECIMAL/HEX]

```

This script will then output JSON files with the packet IDs in the output folder, with the format being decimal by default. If you want to output the IDs in hexadecimal, you can pass the third argument as `HEX`.