package ru.kudo.pallets.model;

import lombok.*;

import java.util.UUID;
@Getter
@Setter
@RequiredArgsConstructor
public class Good {
    private UUID id;
    private int index;
    private int height;
    private int width;
    private int length;
    private boolean rotated;
    private int xCoord;
    private int yCoord;
    private int zCoord;
    private Integer rCoord;
    private Integer tCoord;
    private Integer fCoord;
    private String desc;
    private UUID group;
    private UUID stackedOnGood;
    private int sequenceNr;
    private boolean stackingAllowed;
    private boolean turningAllowed;
    private boolean turned;
    private UUID orderGuid;

    public Good(String id, int index, int height, int width, int length, boolean rotated, int xCoord, int yCoord, int zCoord, Integer rCoord, Integer tCoord, Integer fCoord, String desc, String group, String stackedOnGood, int sequenceNr, boolean stackingAllowed, boolean turningAllowed, boolean turned, String orderGuid) {
        this.id = UUID.fromString(id);
        this.index = index;
        this.height = height;
        this.width = width;
        this.length = length;
        this.rotated = rotated;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.rCoord = rCoord;
        this.tCoord = tCoord;
        this.fCoord = fCoord;
        this.desc = desc;
        this.group = UUID.fromString(group);
        this.stackedOnGood = UUID.fromString(stackedOnGood);
        this.sequenceNr = sequenceNr;
        this.stackingAllowed = stackingAllowed;
        this.turningAllowed = turningAllowed;
        this.turned = turned;
        this.orderGuid = UUID.fromString(orderGuid);
    }

    public Good(UUID id, int index, int height, int width, int length, boolean rotated, int xCoord, int yCoord, int zCoord, Integer rCoord, Integer tCoord, Integer fCoord, String desc, UUID group, UUID stackedOnGood, int sequenceNr, boolean stackingAllowed, boolean turningAllowed, boolean turned, UUID orderGuid) {
        this.id = id;
        this.index = index;
        this.height = height;
        this.width = width;
        this.length = length;
        this.rotated = rotated;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.rCoord = rCoord;
        this.tCoord = tCoord;
        this.fCoord = fCoord;
        this.desc = desc;
        this.group = group;
        this.stackedOnGood = stackedOnGood;
        this.sequenceNr = sequenceNr;
        this.stackingAllowed = stackingAllowed;
        this.turningAllowed = turningAllowed;
        this.turned = turned;
        this.orderGuid = orderGuid;
    }
}