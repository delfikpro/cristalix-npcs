package ru.cristalix.npcs.mod;

import dev.xdark.clientapi.entity.EntityLivingBase;
import lombok.Data;
import ru.cristalix.npcs.data.NpcData;

@Data
public class Npc {

    private final EntityLivingBase entity;
    private final NpcData data;


}
