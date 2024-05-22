package dev.linfoot.mcpacketextractor;

public class Packet {
    private final String className;
    private final String fieldName;
    private final String identifier;

    public Packet(String className, String fieldName, String identifier) {
        this.className = className;
        this.fieldName = fieldName;
        this.identifier = identifier;
    }

    public String getClassName() {
        return className;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "className='" + className + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}
