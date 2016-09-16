package com.austindorff.mechanica.network;

import com.austindorff.mechanica.client.gui.GuiBasicSmelter;
import com.austindorff.mechanica.container.machine.ContainerBasicSmelter;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

	public static final int BASIC_MACHINE_CASING_MULTIBLOCK = 0;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
	    if (id == BASIC_MACHINE_CASING_MULTIBLOCK)
	        return new ContainerBasicSmelter();

	    return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
	    if (id == BASIC_MACHINE_CASING_MULTIBLOCK)
	        return new GuiBasicSmelter(new ContainerBasicSmelter());

	    return null;
	}
	
}