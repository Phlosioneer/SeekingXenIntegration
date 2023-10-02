package com.phlosion.xen_integ.reg;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class XenBlockProperties {

	/** Used by the blast miners. */
	public static final BooleanProperty PRIMED = BooleanProperty.create("primed");

	private XenBlockProperties() {
		throw new UnsupportedOperationException();
	}
}
