package com.vidhur2k.SpaceInvaders.component;

import com.almasb.ents.component.ObjectComponent;
import com.vidhur2k.SpaceInvaders.type.EntityType;

/**
 * Created by vidhur2k on 4/19/17.
 */
public class OwnerComponent extends ObjectComponent<EntityType> {

    public OwnerComponent(EntityType initialValue) {
        super(initialValue);
    }
}
